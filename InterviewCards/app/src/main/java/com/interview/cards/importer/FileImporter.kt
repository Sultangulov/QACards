package com.interview.cards.importer

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object FileImporter {
	data class ParsedCard(
		val question: String,
		val answer: String,
		val section: String? = null,
		val examplesJson: String? = null
	)

	fun readText(context: Context, uri: Uri): String {
		val resolver: ContentResolver = context.contentResolver
		resolver.openInputStream(uri).use { input ->
			requireNotNull(input) { "Не удалось открыть файл" }
			return BufferedReader(InputStreamReader(input)).readText()
		}
	}

	fun parseCsv(text: String): List<ParsedCard> {
		return text.lineSequence()
			.filter { it.isNotBlank() }
			.map { line ->
				val parts = splitCsvLine(line)
				val q = parts.getOrNull(0)?.trim().orEmpty()
				val a = parts.getOrNull(1)?.trim().orEmpty()
				val section = parts.getOrNull(2)?.trim().takeIf { !it.isNullOrEmpty() }
				val examplesRaw = parts.getOrNull(3)?.trim().takeIf { !it.isNullOrEmpty() }
				val examplesJson = examplesRaw?.let { tryWrapExamples(it) }
				ParsedCard(q, a, section, examplesJson)
			}
			.filter { it.question.isNotBlank() && it.answer.isNotBlank() }
			.toList()
	}

	private fun tryWrapExamples(raw: String): String {
		return if (raw.trim().startsWith("[")) raw else JSONArray().put(raw).toString()
	}

	private fun splitCsvLine(line: String): List<String> {
		val result = mutableListOf<String>()
		var current = StringBuilder()
		var inQuotes = false
		var i = 0
		while (i < line.length) {
			val c = line[i]
			when (c) {
				'"' -> {
					if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
						current.append('"')
						i++
					} else {
						inQuotes = !inQuotes
					}
				}
				',' -> if (inQuotes) current.append(c) else { result.add(current.toString()); current = StringBuilder() }
				else -> current.append(c)
			}
			i++
		}
		result.add(current.toString())
		return result
	}

	fun parseJson(text: String): List<ParsedCard> {
		val arr = JSONArray(text)
		val res = mutableListOf<ParsedCard>()
		for (i in 0 until arr.length()) {
			val obj = arr.getJSONObject(i)
			val q = obj.optString("question", obj.optString("prompt")).trim()
			val a = obj.optString("answer").trim()
			val section = obj.optString("section").trim().takeIf { it.isNotEmpty() }
			val examplesJson = parseExamples(obj)
			if (q.isNotBlank() && a.isNotBlank()) res.add(ParsedCard(q, a, section, examplesJson))
		}
		return res
	}

	private fun parseExamples(obj: JSONObject): String? {
		if (obj.has("examples")) {
			val ex = obj.get("examples")
			return when (ex) {
				is JSONArray -> ex.toString()
				is String -> JSONArray().put(ex).toString()
				else -> null
			}
		}
		return null
	}
}