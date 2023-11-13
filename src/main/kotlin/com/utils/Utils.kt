package com.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

fun Any?.errorOut() = println("\u001b[31m$this\u001B[0m")
fun Any?.successOut() = println("\u001b[36m$this\u001B[0m")

fun generateId(): String {
	return UUID.randomUUID().toString().replace("-", "")
}

val gson: Gson = GsonBuilder().disableHtmlEscaping().serializeNulls().create()