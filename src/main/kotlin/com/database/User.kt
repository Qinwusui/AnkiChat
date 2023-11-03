package com.database

import com.database.Users.bindTo
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

val Database.users get() = this.sequenceOf(Users)

object Users : Table<User>("user") {
	val index = long("i").primaryKey().bindTo { it.index }
	val id = text("user_id").bindTo { it.userId }
	val name = text("user_name").bindTo { it.userName }
	val iconUrl = text("icon_url").bindTo { it.iconUrl }
}

interface User : Entity<User> {
	companion object : Entity.Factory<User>()

	var index: Long
	var userId: String
	var userName: String
	var iconUrl: String
}
