package com.wusui.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors


fun Any?.errorOut() = println("\u001b[31m$this\u001B[0m")
fun Any?.successOut() = println("\u001b[36m$this\u001B[0m")

fun generateId(): String {
	return UUID.randomUUID().toString().replace("-", "")
}

val gson: Gson = GsonBuilder().disableHtmlEscaping().serializeNulls().create()

fun TestT() {
	val certificateFactory = CertificateFactory.getInstance("X.509")
	val caInputStream = FileInputStream("./m.pem")
	val caList =
		certificateFactory.generateCertificates(caInputStream).stream().map { v: Certificate? -> v as X509Certificate? }
			.collect(Collectors.toList())
	val keystore = KeyStore.getInstance("JKS")
	keystore.load(null, null)

	val privateKeyBytes = "Qinsansui233...".encodeToByteArray()
	val keyFactory = KeyFactory.getInstance("RSA")
	val privateKeySpec: KeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
	val privateKey = keyFactory.generatePrivate(privateKeySpec)
	keystore.setKeyEntry("wusui", privateKey, "Qinsansui233...".toCharArray(), caList.toTypedArray<X509Certificate?>())
	FileOutputStream("crt.jks").use { outputStream -> keystore.store(outputStream, "Qinsansui233...".toCharArray()) }
}

fun Long.toDate(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(this)
private val imgFileName = listOf(".png", ".jpg", ".jpeg", ".gif")
private val videoFileName = listOf(".mp4", ".mov")
fun String.isEndWithImg() = imgFileName.any { this.endsWith(it) }