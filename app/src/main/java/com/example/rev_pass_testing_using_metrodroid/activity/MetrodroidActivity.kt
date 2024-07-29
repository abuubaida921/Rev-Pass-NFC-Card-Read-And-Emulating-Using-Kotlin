package com.example.rev_pass_testing_using_metrodroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rev_pass_testing_using_metrodroid.R
import au.id.micolous.metrodroid.util.Preferences
import au.id.micolous.metrodroid.util.Utils
import android.content.Context

abstract class MetrodroidActivity : AppCompatActivity() {
    private var mAppliedTheme: Int = 0
    private var mAppliedLang: String = ""

    protected open val themeVariant: Int?
        get() = null

    override fun attachBaseContext(base: Context) {
        val locale = Utils.effectiveLocale()
        mAppliedLang = locale
        super.attachBaseContext(Utils.languageContext(base, locale))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val variant = themeVariant
        val baseTheme = chooseTheme()
        val theme: Int
        mAppliedTheme = baseTheme
        if (variant != null) {
            val a = obtainStyledAttributes(
                    baseTheme,
                    intArrayOf(variant))

            theme = a.getResourceId(0, baseTheme)
            a.recycle()
        } else
            theme = baseTheme
        setTheme(theme)
        if (mAppliedLang != "")
            Utils.resetActivityTitle(this)
        super.onCreate(savedInstanceState)
    }

    protected fun setDisplayHomeAsUpEnabled(b: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(b)
    }

    protected fun setHomeButtonEnabled(b: Boolean) {
        supportActionBar?.setHomeButtonEnabled(b)
    }

    override fun onResume() {
        super.onResume()

        if (chooseTheme() != mAppliedTheme || Utils.effectiveLocale() != mAppliedLang)
            recreate()
    }

    companion object {
        fun chooseTheme(): Int = when (Preferences.themePreference) {
            "light" -> R.style.Metrodroid_Light
            "farebot" -> R.style.FareBot_Theme_Common
            else -> R.style.Metrodroid_Dark
        }
    }
}
