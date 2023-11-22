package com.friends

import com.data.Results
import com.database.Apply
import com.database.DataBaseManager
import com.database.Friend
import com.database.applies
import com.database.friends
import com.database.users
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.entity.removeIf
import org.ktorm.entity.toList

object FriendsController {
	//查找用户对应的所有好友
	fun findFriendsById(id: String): Results<*> {
		val friends = DataBaseManager.db.friends.filter { it.userId eq id }.toList()
		return Results.success(data = friends)
	}


	//获取用户收到的所有好友申请
	fun getAllApplies(userId: String): Results<*> {
		val applies = DataBaseManager.db.applies.filter { it.receiveId eq userId }.map {
			mapOf(
				"applyId" to it.applyId,
				"applyMessage" to it.applyMessage,
				"sendTime" to it.sendTime,
				"sendId" to it.sendId
			)
		}
		return Results.success(applies)
	}

	//拒绝好友申请
	fun refuseApply(applyId: String): Results<Nothing> {
		DataBaseManager.db.applies.find { it.applyId eq applyId } ?: return Results.failure("没有找到该申请")
		DataBaseManager.db.applies.removeIf { it.applyId eq applyId }
		return Results.success("拒绝好友成功")
	}

	//发送好友申请
	fun sendAddApply(apply: Apply): Results<*> {
		//检查是否已经发送过好友申请
		val find = DataBaseManager.db.applies.find { (it.sendId eq apply.sendId) and (it.receiveId eq apply.receiveId) }
		if (find != null) {
			return Results.failure(msg = "您已发送好友申请")
		}
		val toUser =
			DataBaseManager.db.users.find { it.id eq apply.receiveId } ?: return Results.failure("当前用户不存在")
		DataBaseManager.db.applies.add(apply)
		return Results.success("发送成功，请等待${toUser.userName}同意")
	}

	//同意好友申请
	fun agreeApply(applyId: String): Results<*> {

		val apply =
			DataBaseManager.db.applies.find { it.applyId eq applyId } ?: return Results.failure("好友申请不存在")
		DataBaseManager.db.friends.apply {
			add(
				Friend {
					userId = apply.sendId
					friendId = apply.receiveId
				}
			)
			add(
				Friend {
					userId = apply.receiveId
					friendId = apply.sendId
				}
			)
		}
		DataBaseManager.db.applies.removeIf { it.applyId eq applyId }
		return Results.success("同意好友申请成功")
	}
}