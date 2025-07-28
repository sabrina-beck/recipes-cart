package com.recipescart.postgres.repository

import com.recipescart.fixtures.aRecipe
import com.recipescart.model.Cart
import com.recipescart.model.CartItemWithQuantity
import com.recipescart.postgres.SharedPostgresContainer
import com.recipescart.postgres.adapters.CartDbAdapter
import com.recipescart.repository.CartRepository
import com.recipescart.repository.ProductRepository
import com.recipescart.repository.RecipeRepository
import com.recipescart.repository.UpsertItemInCart
import com.recipescart.repository.UpsertItemInCartResult
import com.recipescart.seed.givenExistentRecipe
import com.recipescart.seed.givenExistentRecipes
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
                this.recipeRepository,
            )
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
    fun `upsertRecipe new recipe should add recipe to cart`() {
        val quantity = 2
        val recipe = givenExistentRecipe(recipeRepository, productRepository, 1)
        val cart = cartRepository.newCart()

        val result =
            cartRepository.upsertRecipe(
                UpsertItemInCart(cartId = cart.id, itemId = recipe.id, quantity = quantity),
            )
        assertEquals(UpsertItemInCartResult.Success, result)

        val updatedCart = cartRepository.getCartById(cart.id)

        val expectedCart =
            Cart(
                id = cart.id,
                items = listOf(CartItemWithQuantity(recipe, quantity)),
            )
        assertEquals(expectedCart, updatedCart)
    }

    @Test
    fun `upsertRecipe same recipe should update recipe on cart`() {
        val initialQuantity = 2
        val recipe = givenExistentRecipe(recipeRepository, productRepository, 1)
        val cart = cartRepository.newCart()

        cartRepository.upsertRecipe(
            UpsertItemInCart(cartId = cart.id, itemId = recipe.id, quantity = initialQuantity),
        )

        val newQuantity = 4
        val result =
            cartRepository.upsertRecipe(
                UpsertItemInCart(cartId = cart.id, itemId = recipe.id, quantity = newQuantity),
            )
        assertEquals(UpsertItemInCartResult.Success, result)

        val updatedCart = cartRepository.getCartById(cart.id)

        val expectedCart =
            Cart(
                id = cart.id,
                items = listOf(CartItemWithQuantity(recipe, newQuantity)),
            )
        assertEquals(expectedCart, updatedCart)
    }

    @Test
    fun `upsertRecipe on inexistent cart should return cart not found`() {
        val quantity = 2
        val recipe = givenExistentRecipe(recipeRepository, productRepository, 1)
        val cartId = 1

        val result =
            cartRepository.upsertRecipe(
                UpsertItemInCart(cartId = cartId, itemId = recipe.id, quantity = quantity),
            )
        assertEquals(UpsertItemInCartResult.CartNotFound, result)
    }

    @Test
    fun `upsertRecipe with inexistent recipe should return recipe not found`() {
        val quantity = 2
        val recipe = aRecipe(1)
        val cart = cartRepository.newCart()

        val result =
            cartRepository.upsertRecipe(
                UpsertItemInCart(cartId = cart.id, itemId = recipe.id, quantity = quantity),
            )
        assertEquals(UpsertItemInCartResult.ItemNotFound, result)
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
            givenExistentRecipes(recipeRepository, productRepository)
                .map {
                    CartItemWithQuantity(item = it, quantity = Random.nextInt(1, 5))
                }
        expectedCartItems.forEach {
            cartRepository.upsertRecipe(
                UpsertItemInCart(
                    cartId = emptyCart.id,
                    itemId = it.item.id,
                    quantity = it.quantity,
                ),
            )
        }
        val expectedCart =
            Cart(
                id = emptyCart.id,
                items = expectedCartItems,
            )

        val cart = cartRepository.getCartById(emptyCart.id)
        assertEquals(expectedCart, cart)
    }

    @Test
    fun `removeRecipe with inexistent cart should do nothing`() {
        val recipe = aRecipe(1)
        val cartId = 1

        cartRepository.removeRecipe(
            cartId = cartId,
            recipeId = recipe.id,
        )
    }

    @Test
    fun `removeRecipe with inexistent recipe should do nothing`() {
        val recipe = aRecipe(1)
        val cart = cartRepository.newCart()

        cartRepository.removeRecipe(
            cartId = cart.id,
            recipeId = recipe.id,
        )

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeRecipe with recipe not in cart should do nothing`() {
        val recipe = givenExistentRecipe(recipeRepository, productRepository, 1)
        val cart = cartRepository.newCart()

        cartRepository.removeRecipe(
            cartId = cart.id,
            recipeId = recipe.id,
        )

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeRecipe only recipe in cart should remove recipe`() {
        val cart = cartRepository.newCart()

        val recipe = givenExistentRecipe(recipeRepository, productRepository, 1)

        val upsertItemInCartResult =
            cartRepository.upsertRecipe(
                UpsertItemInCart(cartId = cart.id, itemId = recipe.id, quantity = 2),
            )
        assertEquals(UpsertItemInCartResult.Success, upsertItemInCartResult)

        cartRepository.removeRecipe(
            cartId = cart.id,
            recipeId = recipe.id,
        )

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(cart, updatedCart)
    }

    @Test
    fun `removeRecipe should remove recipe`() {
        val cart = cartRepository.newCart()

        val recipes =
            givenExistentRecipes(recipeRepository, productRepository)
                .map {
                    CartItemWithQuantity(item = it, quantity = Random.nextInt(1, 5))
                }
        recipes.forEach {
            cartRepository.upsertRecipe(
                UpsertItemInCart(
                    cartId = cart.id,
                    itemId = it.item.id,
                    quantity = it.quantity,
                ),
            )
        }

        val recipeToRemove = recipes.first()
        cartRepository.removeRecipe(cartId = cart.id, recipeId = recipeToRemove.item.id)

        val expectedCart =
            Cart(
                id = cart.id,
                items = recipes.filterNot { it.item.id == recipeToRemove.item.id },
            )

        val updatedCart = cartRepository.getCartById(cart.id)
        assertEquals(expectedCart, updatedCart)
    }
}
