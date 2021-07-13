package dotd.hmp.hepler

import java.io.File



fun readFileAsTextUsingInputStream(path: String) = File(path).inputStream().readBytes().toString(Charsets.UTF_8)

fun writeFileText(path: String, content: String) = File(path).writeText(content)
