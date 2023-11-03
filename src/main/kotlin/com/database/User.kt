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

object Users : Table<User>("users") {
	val index = long("index").primaryKey().bindTo { it.index }
	val id = text("id").bindTo { it.userId }
	val name = text("name").bindTo { it.userName }
	val pwd=text("p").bindTo { it.pwd }
	val iconUrl = text("icon_url").bindTo { it.iconUrl }
	val lastOnlineTime=long("last_online_time").bindTo { it.lastOnlineTime }
}

interface User : Entity<User> {
	companion object : Entity.Factory<User>()

	var index: Long
	var userId: String
	var userName: String
	var pwd:String
	var iconUrl: String
	var lastOnlineTime:Long
}
