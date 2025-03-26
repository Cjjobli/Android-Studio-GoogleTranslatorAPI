package com.example.donuts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.nl.translate.*
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.common.model.DownloadConditions
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions

@Composable
fun DetectLanguage(navController: NavController) {

    var inputText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var detectedLanguage by remember { mutableStateOf("") }
    var isDetecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Language Detection",
            style = TextStyle(fontSize = 50.sp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text") },
            textStyle = TextStyle(fontSize = 30.sp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)
        )

        Button(
            onClick = {
                isDetecting = true

                // Detect language using ML Kit's Language Identification
                val languageIdentifier = LanguageIdentification.getClient()

                languageIdentifier.identifyLanguage(inputText)
                    .addOnSuccessListener { languageCode ->

                        if (languageCode != "und") {
                            detectedLanguage = "Detected language: $languageCode"

                            // Translate only if language is detected
                            val options = TranslatorOptions.Builder()
                                .setSourceLanguage(languageCode)
                                .setTargetLanguage(TranslateLanguage.ENGLISH)
                                .build()

                            val englishTranslator = Translation.getClient(options)

                            val conditions = DownloadConditions.Builder()
                                .requireWifi()
                                .build()

                            englishTranslator.downloadModelIfNeeded(conditions)
                                .addOnSuccessListener {
                                    englishTranslator.translate(inputText)
                                        .addOnSuccessListener {  translatedText = it }
                                        .addOnFailureListener { translatedText = "Translation failed." }
                                }
                                .addOnFailureListener {
                                    translatedText = "Model download failed."
                                }

                            // Language is undetected
                        } else {
                            detectedLanguage = "Language not detected."
                        }
                    }
                    .addOnFailureListener {
                        detectedLanguage = "Language detection failed."
                    }
                    .addOnCompleteListener {
                        isDetecting = false
                    }
            },
            enabled = inputText.isNotEmpty() && !isDetecting,
            colors = ButtonDefaults.buttonColors(containerColor = Color.hsl(125f, 0.32f, 0.64f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detect and Translate")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = detectedLanguage, style = TextStyle(fontSize = 20.sp))

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = translatedText,
            onValueChange = {},
            label = { Text("Translated to English") },
            textStyle = TextStyle(fontSize = 30.sp),
            shape = RoundedCornerShape(16.dp),
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        )

        // Clear Button
        Button(
            onClick = {
                inputText = ""
                translatedText = ""
                detectedLanguage = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.hsl(0f, 0.7f, 0.7f)),
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        ) {
            Text("Clear")
        }
    }
}
