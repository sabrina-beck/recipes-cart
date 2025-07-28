package com.recipescart.httpserver.adapters.cart

import com.recipescart.httpserver.adapters.cartitem.toApi
import com.recipescart.httpserver.api.cart.CartApi
import com.recipescart.httpserver.api.cart.CartItemWithQuantityApi
import com.recipescart.model.Cart
import com.recipescart.model.CartItemWithQuantity

fun Cart.toApi(): CartApi =
    CartApi(
        id = id,
        totalInCents = totalInCents,
        items = items.map { it.toApi() },
    )

fun CartItemWithQuantity.toApi(): CartItemWithQuantityApi =
    CartItemWithQuantity(
        item = item,
        quantity = quantity,
    ).let {
        CartItemWithQuantityApi(
            item = it.item.toApi(),
            quantity = it.quantity,
        )
    }
