package com.recipescart.app.tests

import com.recipescart.app.AbstractIntegrationTest
import com.recipescart.features.cart.GetCartUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired

class ApplicationStartupTest : AbstractIntegrationTest() {
    @Autowired
    lateinit var getCartUseCase: GetCartUseCase

    @Test
    fun `application should start successfully and dependency injection should work`() {
        val cart = getCartUseCase.execute(1)
        assertNull(cart)
    }
}
