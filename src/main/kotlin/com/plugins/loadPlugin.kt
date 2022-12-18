package com.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.net.URLClassLoader

fun unloadRoute(r: Routing, routes: Array<String>) {
    val list =
        r.javaClass.superclass.getDeclaredField("childList").apply { isAccessible = true }.get(r) as MutableList<*>
    list.removeIf { "$it" in routes }
}

fun loadPlugin(r: Routing, filePath: String, className: String, methodName: String) {
    val jar = File(filePath)
    if (!jar.exists()) {
        return
    }
    val loader = URLClassLoader(arrayOf(jar.toURI().toURL()))
    try {
        val clz = loader.loadClass(className)
        val m = clz.getDeclaredMethod(methodName, Routing::class.java)
        m.invoke(null, r)
    } catch (e: Exception) {
        println(e)
    }
}


//初次启动自动进行装载插件操作
fun addPluginFirstLoad(r: Routing) {
    val fileList = File("./plugins").list() ?: return
    for (i in fileList) {
        //文件名格式：com.wusui.plugin.LoginKt_login.jar
        val str = i.split("_")
        if (i.contains("com.wusui.plugin") && i.endsWith(".jar")) {
            //如果时jar插件，执行插件加载
            loadPlugin(r, "./plugins/$i", str[0], str[1].replace(".jar", ""))
            println("$i Load")
        }
    }
}

fun Application.loadPlugin() {

    routing {
        addPluginFirstLoad(this)
        route("/refreshPlugin") {
            get("/load") {
                val path = call.parameters["path"] ?: return@get
                val pluginName = call.parameters["pName"] ?: return@get
                val methodName = call.parameters["mName"] ?: return@get
                loadPlugin(this@routing, path, pluginName, methodName)
                call.respond("动态加载插件完成")
            }
            get("/unload") {
                val routing = call.parameters["routing"] ?: return@get
                unloadRoute(this@routing, arrayOf(routing))
                call.respond("Ok")
            }
        }
    }
}