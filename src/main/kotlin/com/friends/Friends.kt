package com.friends

import com.database.User
import com.database.Users
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

val Database.friends get() = this.sequenceOf(Friends)

object Friends : Table<Friend>("friends") {
	val index = int("index").primaryKey().bindTo { it.index }
	val userId = text("user_id").bindTo { it.userId }
	val friendId = text("friend_id").bindTo { it.friendId }
}

interface Friend : Entity<Friend> {
	companion object : Entity.Factory<Friend>()

	var index: Int
	var friendId: String
	var userId: String
}