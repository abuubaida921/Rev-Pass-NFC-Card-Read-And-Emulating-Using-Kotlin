package com.example.rev_pass_testing_using_metrodroid

import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate

import au.id.micolous.metrodroid.util.Utils
import com.example.rev_pass_testing_using_metrodroid.util.Preferences

class MetrodroidApplication : MultiDexApplication() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
    }

    override fun attachBaseContext(base: Context) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(base)
        val v = prefs.getString(Preferences.PREF_LANG_OVERRIDE, "") ?: ""
        val locale = Utils.effectiveLocale(v)
        super.attachBaseContext(Utils.languageContext(base, locale))
    }

    companion object {
        lateinit var instance: MetrodroidApplication
            private set
    }
}
