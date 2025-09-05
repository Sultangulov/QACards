package com.interview.cards.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
	onStartStudy: () -> Unit,
	onImportFile: (Uri) -> Unit
) {
	val context = LocalContext.current
	var lastImportOk by remember { mutableStateOf(false) }
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument(),
		onResult = { uri ->
			if (uri != null) {
				(context as? Activity)?.contentResolver?.takePersistableUriPermission(
					uri,
					Intent.FLAG_GRANT_READ_URI_PERMISSION
				)
				onImportFile(uri)
				lastImportOk = true
			}
		}
	)

	Column(
		modifier = Modifier.fillMaxSize().padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
	) {
		Button(onClick = onStartStudy) { Text("Начать обучение") }
		Button(onClick = {
			launcher.launch(arrayOf("text/*", "application/json", "text/csv"))
		}) { Text("Импортировать вопросы") }
		if (lastImportOk) Text("Импорт завершён")
	}
}