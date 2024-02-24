package com.wusui.user

import com.wusui.data.Results
import com.wusui.data.UserRegisterReqData
import com.wusui.database.Avatar
import com.wusui.database.DataBaseManager
import com.wusui.database.User
import com.wusui.database.avatars
import com.wusui.database.users
import com.wusui.utils.generateId
import io.ktor.util.*
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.add
import org.ktorm.entity.count
import org.ktorm.entity.find
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object UserController {
	enum class UserType {
		Register, Login, Logout
	}

	//简单验证用户信息是否满足条件
	//TODO 这个验证应该做两次，一次在前端，一次在后端
	fun validateUserInfo(userRegisterReqData: UserRegisterReqData, register: UserType): Results<*> {
		if (!userRegisterReqData.userName.isSimpleAlpha()) {
			return Results.failure(msg = "用户名只应由0..9 a..z A..Z 组成")
		}
		if (!userRegisterReqData.userName.lengthIn4To8()) {
			return Results.failure(msg = "用户名长度应在4-8位")
		}
		if (!userRegisterReqData.pwd.isSimpleAlpha()) {
			return Results.failure(msg = "密码只应由0..9 a..z A..Z 组成")
		}
		if (!userRegisterReqData.pwd.lengthIn8To12()) {
			return Results.failure(msg = "密码长度应在8-12位")
		}

		return when (register) {
			UserType.Register -> register(userRegisterReqData)
			UserType.Login -> login(userRegisterReqData)
			UserType.Logout -> logOut(userRegisterReqData)
		}
	}

	private fun logOut(userRegisterReqData: UserRegisterReqData): Results<*> {
		if (!userExist(userRegisterReqData.userName)) return Results.failure(msg = "没有这个用户")
		val user = DataBaseManager.db.users.find { it.id eq userRegisterReqData.userName }
		user?.apply {
			lastOnlineTime = System.currentTimeMillis()
			flushChanges()
		}
		return Results.success(msg = "退出账号成功")
	}

	//检查用户是否存在 可以通过用户名和id同时查找
	fun userExist(nameOrId: String): Boolean {
		println(nameOrId)
		return DataBaseManager.db.users.count { (it.id eq nameOrId) or (it.name eq nameOrId) } == 1
	}

	//登录
	private fun login(userRegisterReqData: UserRegisterReqData): Results<*> {
		//若用户不存在，则走注册流程
		if (!userExist(userRegisterReqData.userName)) return register(userRegisterReqData)
		val token = userRegisterReqData.pwd.generateToken()
		val b = DataBaseManager.db.users.find { (it.name eq userRegisterReqData.userName) and (it.pwd eq token) }
		return if (b != null) {
			b.lastOnlineTime = System.currentTimeMillis()
			b.flushChanges()
			val user =
				DataBaseManager.db.users.find { (it.name eq userRegisterReqData.userName) and (it.pwd eq token) }
			if (user == null) {
				Results.failure(msg = "登录失败")
			} else {
				Results.success(
					msg = "登录成功", data = mapOf(
						"userId" to user.userId,
						"token" to token,
						"userName" to user.userName
					)
				)
			}
		} else {

			Results.failure(msg = "登录失败，用户名或密码错误")
		}
	}

	//注册操作
	private fun register(userRegisterReqData: UserRegisterReqData): Results<*> {
		//用户存在时，注册失败
		if (userExist(userRegisterReqData.userName)) return Results.failure(msg = "用户已存在")
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
		return Results.success("注册成功", data = id to token)

	}

	//获取用户信息
	fun requestUserInfo(userId: String): Results<*> {
		val user = DataBaseManager.db.users.find { it.id eq userId }
		return if (user != null) {
			Results.success(
				mapOf(
					"userId" to user.userId,
					"userName" to user.userName,
					"iconUrl" to user.iconUrl,
					"lastOnlineTime" to user.lastOnlineTime
				)
			)
		} else {
			Results.failure("获取用户信息失败")
		}

	}

	//获取头像
	fun getAvatar(id: String): Results<*> {
		val avatar = DataBaseManager.db.avatars.find { it.id eq id }
		return if (avatar != null) {
			Results.success(
				mapOf(
					"id" to avatar.id,
					"uploadTime" to avatar.uploadTime,
					"avatar" to avatar.avatar.encodeBase64()
				)
			)
		} else {
			Results.failure("获取头像失败")
		}
	}

	//保存头像
	fun saveAvatar(id: String, avatar: ByteArray): Results<*> {
		DataBaseManager.db.avatars.add(
			Avatar {
				this.id = id
				this.uploadTime = System.currentTimeMillis()
				this.avatar = avatar
			})
		return Results.success("保存头像成功")
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
	fun findUserById(toUserId: String?): User? {
		if (toUserId == null) return null
		return DataBaseManager.db.users.find { it.id eq toUserId }
	}

}