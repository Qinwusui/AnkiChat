package com.database

import com.utils.errorOut
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement

object DataBaseManager {
	private const val DB = "jdbc:sqlite:D:/AnkiChat/user.db"
	private lateinit var connection: Connection
	private lateinit var statement: Statement

	init {
		runCatching {
			Class.forName("org.sqlite.JDBC")
			initDB()
			createTable()
		}.exceptionOrNull()?.let {
			it.errorOut()
		}

	}

	private fun initDB() {
		connection = DriverManager.getConnection(DB)
		connection.autoCommit = false
		statement = connection.createStatement()
	}

	//创建所有表
	private fun createTable() {
		useStatement {
			createUserTable()
			createGroupTable()
			createGroupAdminTable()
			createGroupMembersTable()
			createMessageTable()
		}
	}

	//创建用户表
	private fun Statement.createUserTable() {
		execute(
			"""
			create table user 
				(
					i          INTEGER not null on conflict rollback
						constraint user_pk
							primary key autoincrement,
					user_id          TEXT    not null,
					user_name        TEXT   ,
					pwd              TEXT    not null,
					reg_time         integer,
					last_online_time integer,
					validate         integer not null
				);
				""".trimIndent()
		)
	}

	//创建群组表
	private fun Statement.createGroupTable() {
		execute(
			"""
			create table groups
			(
			    i    integer not null on conflict rollback
			        constraint groups_pk
			            primary key autoincrement,
				group_id    TEXT not null,
			    group_name  TEXT,
			    create_time integer,
			    creator_id  integer not null,
			    owner_id    integer not null
			);
		""".trimIndent()
		)
	}

	//创建群管理员表
	private fun Statement.createGroupAdminTable() {
		execute(
			"""
			create table group_admin
			(
			    group_id integer not null,
			    id       integer not null
			);
		""".trimIndent()
		)
	}

	//创建群成员表
	private fun Statement.createGroupMembersTable() {
		execute(
			"""
			create table group_members
			(
			    group_id      integer not null,
			    user_id       integer not null
			);
		""".trimIndent()
		)
	}

	//创建消息表
	private fun Statement.createMessageTable() {
		execute(
			"""
			create table messages
			(
			    i  integer not null
			        constraint messages_pk
			            primary key autoincrement,
				message_id  TEXT not null,
			    send_id     integer not null,
			    receive_id  integer not null,
			    messge_type integer,
			    content     BLOB,
			    send_time   integer
			);
		""".trimIndent()
		)
	}

	//将数据库操作统一到一个地方
	fun useStatement(isSelect: Boolean = false, block: Statement.() -> Unit): Boolean {
		runCatching {
			statement.apply(block)
			if (!isSelect) {
				connection.commit()
			}
		}.exceptionOrNull()?.let {
			it.errorOut()
			return false
		}
		return true
	}

	//使用preparedStatement防止一部分SQL注入
	fun usePreparedStatement(sql: String, block: PreparedStatement.() -> Unit): Boolean {
		runCatching {
			val preparedStatement = connection.prepareStatement(sql)
			preparedStatement.apply(block)
			preparedStatement.execute()
			connection.commit()
		}.exceptionOrNull()?.let {
			it.errorOut()
			return false
		}
		return true
	}
}
