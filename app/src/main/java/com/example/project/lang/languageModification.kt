package com.example.project.lang

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun updateAppLocale(languageCode: String){
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val localeList = LocaleListCompat.create(locale)
    AppCompatDelegate.setApplicationLocales(localeList)
}