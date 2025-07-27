import com.recipescart.adapters.cart.toAddRecipeUseCaseResult
import com.recipescart.features.cart.UpsertRecipeInCartUseCaseResult
import com.recipescart.repository.CartRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class UpsertRecipeInCartAdaptersTest {
    @Test
    fun `should map Success correctly`() {
        val input = CartRepository.UpsertRecipeResult.Success
        val expected = UpsertRecipeInCartUseCaseResult.Success
        assertEquals(expected, input.toAddRecipeUseCaseResult())
    }

    @Test
    fun `should map RecipeNotFound correctly`() {
        val input = CartRepository.UpsertRecipeResult.RecipeNotFound
        val expected = UpsertRecipeInCartUseCaseResult.RecipeInCartNotFound
        assertEquals(expected, input.toAddRecipeUseCaseResult())
    }

    @Test
    fun `should map CartNotFound correctly`() {
        val input = CartRepository.UpsertRecipeResult.CartNotFound
        val expected = UpsertRecipeInCartUseCaseResult.CartNotFound
        assertEquals(expected, input.toAddRecipeUseCaseResult())
    }

    @Test
    fun `should have test for every AddRecipeResult variant`() {
        val testedVariants =
            setOf(
                CartRepository.UpsertRecipeResult.Success::class,
                CartRepository.UpsertRecipeResult.RecipeNotFound::class,
                CartRepository.UpsertRecipeResult.CartNotFound::class,
            )

        val actualVariants = CartRepository.UpsertRecipeResult::class.sealedSubclasses.toSet()

        if (testedVariants != actualVariants) {
            val missing = actualVariants - testedVariants
            fail("Missing test coverage for: ${missing.joinToString()}")
        }
    }
}
