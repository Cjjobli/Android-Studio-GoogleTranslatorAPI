package com.example.donuts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


@Composable
fun TranslatorApp(navController: NavController) {

    var inputText by remember { mutableStateOf("") }
    var spanishText by remember { mutableStateOf("") }
    var tagalogText by remember { mutableStateOf("") }
    var isModelDownloaded by remember { mutableStateOf(false) }

    // Creating a translator object and configuring it with the source and target language
    val spanishTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        Translation.getClient(options) // Creates translator client and handles the actual translation
    }

    val tagalogTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.TAGALOG)
            .build()
        Translation.getClient(options)
    }

    // Download models for both Spanish and Tagalog
    // Initializes a builder for downloading language models.
    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    val spanishDownload = spanishTranslator.downloadModelIfNeeded(conditions)
    val tagalogDownload = tagalogTranslator.downloadModelIfNeeded(conditions)

    // Language models are around 30MB
    spanishDownload.addOnSuccessListener {
        isModelDownloaded = true
    }.addOnFailureListener {
        spanishText = "Spanish translation model download failed."
    }

    tagalogDownload.addOnSuccessListener {
        isModelDownloaded = true
    }.addOnFailureListener {
        tagalogText = "Tagalog translation model download failed."
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Cj's Dual Wielding Translator",
            style = TextStyle(fontSize = 50.sp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text in English") },
            textStyle = TextStyle(fontSize = 30.sp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
        )

        Button(
            onClick = {
                // Translate to Spanish
                spanishTranslator.translate(inputText)
                    .addOnSuccessListener { spanishText = it }
                    .addOnFailureListener { spanishText = "Spanish translation failed." }

                // Translate to Tagalog
                tagalogTranslator.translate(inputText)
                    .addOnSuccessListener { tagalogText = it }
                    .addOnFailureListener { tagalogText = "Tagalog translation failed." }
            },
            enabled = inputText.isNotEmpty() && isModelDownloaded,
            colors = ButtonDefaults.buttonColors(containerColor = Color.hsl(125f, 0.32f, 0.64f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Translate to Spanish and Tagalog")
        }

        // Spanish Translation
        OutlinedTextField(
            value = spanishText,
            onValueChange = {},
            label = { Text("Translated to Spanish") },
            textStyle = TextStyle(fontSize = 30.sp),
            shape = RoundedCornerShape(16.dp),
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        )

        // Tagalog Translation
        OutlinedTextField(
            value = tagalogText,
            onValueChange = {},
            label = { Text("Translated to Tagalog") },
            textStyle = TextStyle(fontSize = 30.sp),
            shape = RoundedCornerShape(16.dp),
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        )

        // Clear Button
        Button(
            onClick = {
                // Clear all fields
                inputText = ""
                spanishText = ""
                tagalogText = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.hsl(0f, 0.7f, 0.7f)),
            modifier = Modifier.fillMaxWidth()
                .fillMaxWidth().padding(top = 30.dp)

        ) {
            Text("Clear")
        }
    }
}

