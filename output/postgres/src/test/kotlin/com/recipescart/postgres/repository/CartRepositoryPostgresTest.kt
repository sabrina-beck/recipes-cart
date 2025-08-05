package com.recipescart.postgres.repository

import com.recipescart.fixtures.aRecipe
import com.recipescart.model.Cart
import com.recipescart.model.CartItem
import com.recipescart.model.CartItemId
import com.recipescart.model.CartItemType
import com.recipescart.postgres.SharedPostgresContainer
import com.recipescart.postgres.adapters.CartDbAdapter
import com.recipescart.repository.CartRepository
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.repository.UpsertItemInCartResult
import com.recipescart.seed.RecipesSeed
import com.zaxxer.hikari.HikariDataSource
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CartRepositoryPostgresTest {
    private lateinit var dataSource: HikariDataSource

    private lateinit var productRepository: ProductRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var cartRepository: CartRepository

    private lateinit var recipesSeed: RecipesSeed

    @BeforeTest
    fun setup() {
        this.dataSource = SharedPostgresContainer.newCartRecipesDb()

        val jdbc = JdbcTemplate(this.dataSource)
        this.recipeRepository = RecipeRepositoryPostgres(jdbc)
        this.productRepository = ProductRepositoryPostgres(jdbc)
        this.cartRepository =
            CartRepositoryPostgres(
                jdbc,
                CartDbAdapter(productRepository, recipeRepository),
            )

        this.recipesSeed = RecipesSeed(this.productRepository, this.recipeRepository)
    }

    @AfterTest
    fun tearDown() {
        dataSource.close()
    }

    @Test
    fun `newCart should create new cart`() {
        val cart = cartRepository.newCart()

        val expectedCart = Cart(id = cart.id)
        assertEquals(expectedCart, cart)
    }

    @Test
    fun `getCartById when cart doesn't exist should return empty`() {
        val cart = cartRepository.getCartById(1)
        assertNull(cart)
    }

    @Test
    fun `getCartById when cart exist but it's empty should return cart`() {
        val emptyCart = cartRepository.newCart()

        val cart = cartRepository.getCartById(emptyCart.id)
        assertEquals(emptyCart, cart)
    }

    @Test
    fun `getCartById when cart has items should return cart`() {
        val emptyCart = cartRepository.newCart()

        val expectedCartItems =
            recipesSeed
                .givenExistentRecipes()
                .map {
                    CartItem(item = it, quantity = Random.nextInt(1, 5))
                }
        expectedCartItems.forEach {
            cartRepository.upsertCartItem(
                cartId = emptyCart.id,
                cartItem = it,
            )
        }
        cartRepository.updateCartTotalInCents(
            cartId = emptyCart.id,
            totalInCents = expectedCartItems.sumOf { it.item.priceInCents() * it.quantity },
        )

        val expectedCart =
            Cart(
                id = emptyCart.id,
                items = expectedCartItems,
            )

        val cart = cartRepository.getCartById(emptyCart.id)
        assertEquals(expectedCart, cart)
    }

    @Test
    fun `getCartItemQuantity when there's no cart item should return empty`() {
        val cart = cartRepository.newCart()
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )
        val quantity = cartRepository.getCartItemQuantity(cartItemId)

        assertNull(quantity)
    }

    @Test
    fun `upsertCartItem new recipe should add recipe to cart`() {
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)
        val cartItem =
            CartItem(
                item = recipe,
                quantity = 2,
            )
        val cart = cartRepository.newCart()

        val result =
            cartRepository.upsertCartItem(
                cartId = cart.id,
                cartItem = cartItem,
            )
        assertEquals(UpsertItemInCartResult.Success, result)

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )
        val savedQuantity = cartRepository.getCartItemQuantity(cartItemId)

        assertEquals(cartItem.quantity, savedQuantity)
    }

    @Test
    fun `upsertCartItem same recipe should update recipe's quantity on cart`() {
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)
        val initialCartItem =
            CartItem(
                item = recipe,
                quantity = 2,
            )
        val cart = cartRepository.newCart()

        cartRepository.upsertCartItem(
            cartId = cart.id,
            cartItem = initialCartItem,
        )

        val updatedCartItem =
            CartItem(
                item = recipe,
                quantity = 4,
            )
        val result =
            cartRepository.upsertCartItem(
                cartId = cart.id,
                cartItem = updatedCartItem,
            )
        assertEquals(UpsertItemInCartResult.Success, result)

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )
        val updatedQuantity = cartRepository.getCartItemQuantity(cartItemId)

        assertEquals(updatedCartItem.quantity, updatedQuantity)
    }

    @Test
    fun `upsertCartItem on inexistent cart should return cart not found`() {
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)
        val cartItem =
            CartItem(
                item = recipe,
                quantity = 2,
            )
        val cartId = 1

        val result =
            cartRepository.upsertCartItem(
                cartId = cartId,
                cartItem = cartItem,
            )
        assertEquals(UpsertItemInCartResult.CartNotFound, result)
    }

    @Test
    fun `updateCartTotalInCents should update cart total in cents`() {
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)
        val cartItem =
            CartItem(
                item = recipe,
                quantity = 2,
            )
        val cart = cartRepository.newCart()

        cartRepository.upsertCartItem(cartId = cart.id, cartItem = cartItem)
        val newTotalInCents = cart.totalInCents + cartItem.totalInCents()
        cartRepository.updateCartTotalInCents(cartId = cart.id, totalInCents = newTotalInCents)

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(newTotalInCents, updatedCart?.totalInCents)
    }

    @Test
    fun `updateCartTotalInCents with inexistent cart should do nothing`() {
        val cartId = 9999

        cartRepository.updateCartTotalInCents(cartId = cartId, totalInCents = 1000)
    }

    @Test
    fun `removeCartItem with inexistent cart should do nothing`() {
        val recipe = aRecipe(1)
        val cartId = 1

        val cartItemId =
            CartItemId(
                cartId = cartId,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )

        cartRepository.removeCartItem(cartItemId)
    }

    @Test
    fun `removeCartItem with inexistent recipe should do nothing`() {
        val recipe = aRecipe(1)
        val cart = cartRepository.newCart()

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )

        cartRepository.removeCartItem(cartItemId)

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeCartItem with recipe not in cart should do nothing`() {
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)
        val cart = cartRepository.newCart()

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )

        cartRepository.removeCartItem(cartItemId)

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeCartItem only recipe in cart should remove recipe`() {
        val cart = cartRepository.newCart()
        val recipe = recipesSeed.givenExistentRecipe(recipeId = 1)

        val cartItem = CartItem(item = recipe, quantity = 2)
        val upsertItemInCartResult =
            cartRepository.upsertCartItem(cartId = cart.id, cartItem = cartItem)
        assertEquals(UpsertItemInCartResult.Success, upsertItemInCartResult)

        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipe.id,
                cartItemType = CartItemType.RECIPE,
            )
        cartRepository.removeCartItem(cartItemId)

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeCartItem should remove recipe`() {
        val cart = cartRepository.newCart()

        val recipes =
            recipesSeed
                .givenExistentRecipes()
                .map {
                    CartItem(item = it, quantity = Random.nextInt(1, 5))
                }
        recipes.forEach {
            cartRepository.upsertCartItem(
                cartId = cart.id,
                cartItem = it,
            )
        }

        val recipeToRemove = recipes.first()
        val cartItemId =
            CartItemId(
                cartId = cart.id,
                itemId = recipeToRemove.item.id,
                cartItemType = CartItemType.RECIPE,
            )
        cartRepository.removeCartItem(cartItemId)

        val updatedQuantity = cartRepository.getCartItemQuantity(cartItemId)
        assertNull(updatedQuantity)
    }
}
