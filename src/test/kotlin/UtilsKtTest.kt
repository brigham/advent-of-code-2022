import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UtilsKtTest {
    @Test
    fun overlap_none() {
        assertNull((1..5).overlap(7..9))
    }

    @Test
    fun overlap_none2() {
        assertNull((7..9).overlap(1..5))
    }

    @Test
    fun overlap_endpoint() {
        assertEquals(5..5, (1..5).overlap(5..9))
    }

    @Test
    fun overlap_endpoint2() {
        assertEquals(5..5, (5..9).overlap(1..5))
    }

    @Test
    fun overlap_inside() {
        assertEquals(3..5, (1..9).overlap(3..5))
    }

    @Test
    fun overlap_inside2() {
        assertEquals(3..5, (3..5).overlap(1..9))
    }
}
