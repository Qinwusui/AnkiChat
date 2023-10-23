import com.user.UserController
import com.user.UserRegisterReqData
import com.utils.successOut

fun main() {
	UserController.userExist("1f143cdf50d24bf3824754a6f0293393").successOut()
}