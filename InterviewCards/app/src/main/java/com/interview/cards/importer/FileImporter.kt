package com.interview.cards.importer

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object FileImporter {
	fun readText(context: Context, uri: Uri): String {
		val resolver: ContentResolver = context.contentResolver
		resolver.openInputStream(uri).use { input ->
			requireNotNull(input) { "Не удалось открыть файл" }
			return BufferedReader(InputStreamReader(input)).readText()
		}
	}

	fun parseCsv(text: String): List<Pair<String, String>> {
		return text.lineSequence()
			.filter { it.isNotBlank() }
			.map { line ->
				val parts = splitCsvLine(line)
				val q = parts.getOrNull(0)?.trim().orEmpty()
				val a = parts.getOrNull(1)?.trim().orEmpty()
				q to a
			}
			.filter { it.first.isNotBlank() && it.second.isNotBlank() }
			.toList()
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

	fun parseJson(text: String): List<Pair<String, String>> {
		val arr = JSONArray(text)
		val res = mutableListOf<Pair<String, String>>()
		for (i in 0 until arr.length()) {
			val obj = arr.getJSONObject(i)
			val q = obj.optString("question").trim()
			val a = obj.optString("answer").trim()
			if (q.isNotBlank() && a.isNotBlank()) res.add(q to a)
		}
		return res
	}
}