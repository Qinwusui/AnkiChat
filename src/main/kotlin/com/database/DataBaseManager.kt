package com.database

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.data.DataBaseConfig
import com.utils.errorOut
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.support.mysql.MySqlDialect
import java.sql.PreparedStatement
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

object DataBaseManager {
	private lateinit var db: Database
	private lateinit var dataSource: DataSource

	init {


	}

	private fun initDataSource(config: DataBaseConfig) {
		val props = Properties()
		config.javaClass.declaredFields.forEach {
			props[it.name] = "${it.apply { isAccessible = true }.get(config)}"
		}
		dataSource = DruidDataSourceFactory.createDataSource(props)
		db = Database.connect(
			dataSource = dataSource,
			dialect = MySqlDialect(),
			logger = ConsoleLogger(config.logLevel)
		)
	}

	private fun initDB() {

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
			    creator_id  TEXT not null,
			    owner_id    TEXT not null
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
			    group_id TEXT not null,
			    id       TEXT not null
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
			    i           integer not null
			        constraint messages_pk
			            primary key autoincrement,
				message_id  TEXT not null,
				type        TEXT not null,
			    send_id     TEXT not null,
			    to_id       TEXT not null,
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
