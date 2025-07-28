package com.recipescart.httpserver.adapters.cart

import com.recipescart.httpserver.adapters.cartitem.toApi
import com.recipescart.httpserver.api.cart.CartApi
import com.recipescart.httpserver.api.cart.CartItemApi
import com.recipescart.model.Cart
import com.recipescart.model.CartItem

fun Cart.toApi(): CartApi =
    CartApi(
        id = id,
        totalInCents = totalInCents,
        items = items.map { it.toApi() },
    )

fun CartItem.toApi(): CartItemApi =
    CartItemApi(
        item = item.toApi(),
        quantity = quantity,
    )
