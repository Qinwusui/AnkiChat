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
	lateinit var db: Database
	private lateinit var dataSource: DataSource

	init {

		initDataSource(DataBaseConfig())
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

}
