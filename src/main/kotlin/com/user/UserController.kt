package com.user

import com.database.DataBaseManager
import com.utils.generateId
import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object UserController {
	//简单验证用户信息是否满足条件
	//TODO 这个验证应该做两次，一次在前端，一次在后端
	fun validate(userRegisterReqData: UserRegisterReqData, register: Boolean): UserRespData {
		if (!userRegisterReqData.userName.isSimpleAlpha()) {
			return UserRespData(success = false, msg = "用户名只应由0..9 a..z A..Z 组成")
		}
		if (!userRegisterReqData.userName.lengthIn4To8()) {
			return UserRespData(success = false, msg = "用户名长度应在4-8位")
		}
		if (!userRegisterReqData.pwd.isSimpleAlpha()) {
			return UserRespData(success = false, msg = "密码只应由0..9 a..z A..Z 组成")
		}
		if (!userRegisterReqData.pwd.lengthIn8To12()) {
			return UserRespData(success = false, msg = "密码长度应在8-12位")
		}

		return if (register) {
			register(userRegisterReqData)
		} else {
			login(userRegisterReqData)
		}
	}

	//检查用户是否存在 可以通过用户名和id同时查找
	fun userExist(userName: String): Boolean {
		var sum = 0
		DataBaseManager.useStatement {
			val set = executeQuery("select count() from user where user_name='$userName' or user_id='$userName'")
			sum = set.getInt("count()")
		}
		return sum == 1

	}

	//登录
	private fun login(userRegisterReqData: UserRegisterReqData): UserRespData {
		//若用户不存在，则走注册流程
		if (!userExist(userRegisterReqData.userName)) return register(userRegisterReqData)
		val token = userRegisterReqData.pwd.generateToken()
		var i = 0
		var userId = ""
		DataBaseManager.useStatement {
			val set =
				executeQuery(
					"""
					select count(),user_id from user
					 where (user_id='${userRegisterReqData.userName}' and pwd='${userRegisterReqData.pwd}') 
					 or (user_name='${userRegisterReqData.userName}' and pwd='${token}')
				""".trimIndent()
				)
			i = set.getInt("count()")
			userId = set.getString("user_id")
		}
		return if (i == 1) {
			UserRespData(userId = userId, token = token, success = true, msg = "登录成功")
		} else {
			UserRespData(success = false, msg = "登录失败，用户名或密码错误")
		}
	}

	//注册操作
	private fun register(userRegisterReqData: UserRegisterReqData): UserRespData {
		//用户存在时，注册失败
		if (userExist(userRegisterReqData.userName)) return UserRespData(success = false, msg = "用户已存在")
		val id = generateId()
		val token = userRegisterReqData.pwd.generateToken()
		val b = DataBaseManager.usePreparedStatement(
			sql = """
			insert into user (user_id, user_name, pwd, reg_time, last_online_time, validate) values (?,?, ?, ?, ?, ?)
		""".trimIndent()
		) {
			setString(1, id)
			setString(2, userRegisterReqData.userName)
			setString(3, token)
			setInt(4, System.currentTimeMillis().toInt())
			setInt(5, System.currentTimeMillis().toInt())
			setInt(6, 1)
		}
		return if (b) {
			UserRespData(userId = id, token = token, success = true, msg = "注册成功", isRegister = true)
		} else {
			UserRespData(success = false, msg = "注册失败", isRegister = true)
		}
	}

	//生成Token
	private fun String.generateToken(key: String = "Qinsansui233...") = runCatching {
		val sha256 = Mac.getInstance("HmacSHA256")
		val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
		sha256.init(secretKey)
		return hex(sha256.doFinal(this.toByteArray()))
	}.getOrNull() ?: ""

	private fun String.isSimpleAlpha(): Boolean = this.all { it in '0'..'9' || it in 'a'..'z' || it in 'A'..'Z' }
	private fun String.lengthIn8To12(): Boolean = this.length in 8..12
	private fun String.lengthIn4To8(): Boolean = this.length in 4..8
}