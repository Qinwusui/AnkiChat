package com.user

import com.data.UserRegisterReqData
import com.data.UserRespData
import com.database.DataBaseManager
import com.database.User
import com.database.users
import com.utils.generateId
import io.ktor.util.*
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.add
import org.ktorm.entity.count
import org.ktorm.entity.filter
import org.ktorm.entity.find
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object UserController {
	enum class UserType {
		Register, Login, Logout
	}

	//简单验证用户信息是否满足条件
	//TODO 这个验证应该做两次，一次在前端，一次在后端
	fun validateUserInfo(userRegisterReqData: UserRegisterReqData, register: UserType): UserRespData {
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

		return when (register) {
			UserType.Register -> register(userRegisterReqData)
			UserType.Login -> login(userRegisterReqData)
			UserType.Logout -> logOut(userRegisterReqData)
		}
	}

	private fun logOut(userRegisterReqData: UserRegisterReqData): UserRespData {
		if (!userExist(userRegisterReqData.userName)) return UserRespData(success = false, msg = "没有这个用户")

		return UserRespData(success = true, msg = "退出账号成功")
	}

	//检查用户是否存在 可以通过用户名和id同时查找
	fun userExist(userName: String): Boolean {
		return DataBaseManager.db.users.filter { (it.name eq userName) or (it.id eq userName) }.count() == 1
	}

	//登录
	private fun login(userRegisterReqData: UserRegisterReqData): UserRespData {
		//若用户不存在，则走注册流程
		if (!userExist(userRegisterReqData.userName)) return register(userRegisterReqData)
		val token = userRegisterReqData.pwd.generateToken()
		val b = DataBaseManager.db.users.find { (it.name eq userRegisterReqData.userName) and (it.pwd eq token) }
		return if (b != null) {
			b.lastOnlineTime = System.currentTimeMillis()
			b.flushChanges()
			UserRespData(userId = b.userId, token = token, success = true, msg = "登录成功")
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
		val user = User {
			userId = id
			pwd = token
			lastOnlineTime = System.currentTimeMillis()
			iconUrl = ""
			userName = userRegisterReqData.userName
		}
		DataBaseManager.db.users.add(user)
		return UserRespData(userId = id, token = token, success = true, msg = "注册成功", isRegister = true)

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