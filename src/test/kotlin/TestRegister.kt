import com.utils.successOut
import java.util.Date

fun main() {
	val b = OnClickListener {
		Date()
	}
	b.onClick().successOut()

}

interface OnClickListener<T> {
	fun onClick(): T

	companion object {
		operator fun <T> invoke(block: () -> T): OnClickListener<T> {
			return object : OnClickListener<T> {
				override fun onClick(): T {
					return block()
				}
			}
		}
	}
}