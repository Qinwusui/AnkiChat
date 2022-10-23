package com.plugins

import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.sql.DriverManager
import java.sql.Statement
import kotlin.coroutines.CoroutineContext

object ChatUserManagement {
    private const val DB = "jdbc:sqlite:user.db"

    init {
        Class.forName("org.sqlite.JDBC")
        createTable()
    }

    data class ChatUser(
        val uName: String,
        val qq: String,
        val pwd: String,
    )

    data class LoginUser(
        val uName: String,
        val qq: String,
        val pwd: String
    )

    /**
     * String TO ChatUser
     */
    private fun convertContentToUser(content: String): ChatUser? {
        try {
            val strList = content.decodeBase64String().split("||")
            if (strList.size != 3) {
                return null
            }
            val uName = strList[0]
            val qq = strList[1]
            val pwd = strList[2]
            return ChatUser(uName, qq, pwd)
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }


    /**
     * 更改密码
     */
    fun updatePwd(content: String) = flowByIO {
        val strList = content.decodeBase64String().split("||")
        if (strList.size != 3) {
            return@flowByIO false
        }
        val oldContent = strList
            .joinToString("||")
            .substringBeforeLast("||")
            .encodeBase64()
        val newPwd = strList[3]
        //用户存在才可进行更新密码
        if (getUserExist(oldContent)) {
            val chatUser = convertContentToUser(oldContent) ?: return@flowByIO false
            return@flowByIO useDataBasePool {
                it.executeUpdate(
                    """
                update User set Pwd='${newPwd.encodeBase64()}' 
                where qq=='${chatUser.qq}'
                and Pwd =='${chatUser.pwd.encodeBase64()}'
            """.trimIndent()
                )
            }
        }
        return@flowByIO false
    }

    fun getUserQQ(loginUser: LoginUser) = flowByIO {
        try {
            var s = ""
            useDataBasePool {
                val res = it.executeQuery(
                    """
                    select qq from User 
                    where uName='${loginUser.uName}'
                    and Pwd ='${loginUser.pwd}'
                """.trimIndent()
                )
                while (res.next()) {
                    s = res.getString("qq")
                }
            }
            println(s)
            s
        } catch (e: Exception) {
            ""
        }

    }

    /**
     * 用户登录
     */
    fun userLogin(content: String, c: Boolean = getUserExist(content)) = flowByIO { c }

    /**
     * 用户登录时解码
     */
    private fun convertContentToLoginUser(content: String): LoginUser? {
        try {
            val strList = content.decodeBase64String().split("||")
            if (strList.size != 3) {
                return null
            }
            val uName = strList[0]
            val pwd = strList[2]
            val qq = strList[1]
            return LoginUser(uName, qq, pwd)
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }


    /**
     * 查询用户是否存在
     */
    private fun getUserExist(content: String): Boolean {
        try {
            var i = 0
            val chatUser = convertContentToUser(content) ?: return false
            useDataBasePool {
                val res = it.executeQuery(
                    """
                select * from User 
                where  uName='${chatUser.uName}'
                    and qq='${chatUser.qq}'
                    and Pwd='${chatUser.pwd}'
            """.trimIndent()
                )
                while (res.next()) {
                    i++
                }
            }
            return i == 1
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 插入用户
     */
    fun insertUser(content: String) = flowByIO {
        if (getUserExist(content)) {
            return@flowByIO false
        }
        try {
            val chatUser = convertContentToUser(content) ?: return@flowByIO false
            val uName = chatUser.uName
            val pwd = chatUser.pwd
            val qq = chatUser.qq
            useDataBasePool {
                it.executeUpdate("insert into User values ('$uName','${pwd}','$qq',null)")
            }
        } catch (e: Exception) {
            println(e.message)
            false
        }
    }


    /**
     * 创建数据库表
     *
     */
    private fun createTable() {
        useDataBasePool {
            it.execute(
                """
            create table User(
                uName TEXT,
                Pwd TEXT,
                qq TEXT,
                uid integer primary key AUTOINCREMENT
            );
        """.trimIndent()
            )
        }
    }

    /**
     * Kotlin 数据库 DSL 统一打开关闭数据库连接
     */
    private fun useDataBasePool(content: (statement: Statement) -> Unit): Boolean {
        val driverManager = DriverManager.getConnection(DB)
        driverManager.autoCommit = false
        val stmt = driverManager.createStatement()
        return try {
            stmt.apply(content)
            driverManager.commit()
            stmt.close()
            driverManager.close()
            true
        } catch (e: Exception) {
            println(e.message)
            false
        }
    }

    /**
     * 泛型`<T>` flow
     */
    private fun <T> flowByIO(coroutineContext: CoroutineContext = Dispatchers.IO, content: suspend () -> T) =
        flow {
            emit(content())
        }.distinctUntilChanged().flowOn(coroutineContext)
}