@file:JvmName("LocalizerKtActual")


package com.example.rev_pass_testing_using_metrodroid

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.LocaleSpan
import android.text.style.TtsSpan
import com.example.rev_pass_testing_using_metrodroid.ui.HiddenSpan
import com.example.rev_pass_testing_using_metrodroid.util.Preferences
import com.example.rev_pass_testing_using_metrodroid.util.Utils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

actual typealias StringResource = Int
actual typealias DrawableResource = Int
actual typealias PluralsResource = Int

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = StringResource::class)
actual class StringResourceSerializer : KSerializer<StringResource> {
    override fun deserialize(decoder: Decoder): StringResource {
        val id = String.serializer().deserialize(decoder)
        return Rmap.strings[id]!!
    }

    override fun serialize(encoder: Encoder, value: StringResource) {
        String.serializer().serialize(encoder,
            Rmap.strings.filterValues { it == value }.keys.first())
    }
}

actual object Localizer : LocalizerInterface {
    /**
     * Given a string resource (R.string), localize the string according to the language preferences
     * on the device.
     *
     * @param res R.string to localize.
     * @param v     Formatting arguments to pass
     * @return Localized string
     */
    override fun localizeString(res: StringResource, vararg v: Any?): String {
        val appRes = MetrodroidApplication.instance.resources
        return appRes.getString(res, *v)
    }

    override fun localizeFormatted(res: StringResource, vararg v: Any?): FormattedString {
        val appRes = MetrodroidApplication.instance.resources
        val spanned = SpannableString(appRes.getText(res))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Preferences.localisePlaces) {
            spanned.setSpan(LocaleSpan(Locale.getDefault()), 0, spanned.length, 0)
            if (Preferences.debugSpans)
                spanned.setSpan(ForegroundColorSpan(Color.GREEN), 0, spanned.length, 0)
        }
        return FormattedString(spanned).format(*v)
    }
    /**
     * Given a plural resource (R.plurals), localize the string according to the language preferences
     * on the device.
     *
     * @param res R.plurals to localize.
     * @param count       Quantity to use for pluaralisation rules
     * @param v     Formatting arguments to pass
     * @return Localized string
     */
    override fun localizePlural(res: PluralsResource, count: Int, vararg v: Any?): String {
            val appRes = MetrodroidApplication.instance.resources
            return appRes.getQuantityString(res, count, *v)
    }

    private val englishResources: Resources by lazy {
        val context = MetrodroidApplication.instance
        Utils.localeContext(context, Locale.ENGLISH).resources
    }

    fun englishString(res: StringResource, vararg v: Any?): String = englishResources.getString(res, *v)

    override fun localizeTts(res: StringResource, vararg v: Any?): FormattedString {
        val appRes = MetrodroidApplication.instance.resources
        val b = SpannableStringBuilder(appRes.getText(res))

        // Find the TTS-exclusive bits
        // They are wrapped in parentheses: ( )
        var x = 0
        while (x < b.toString().length) {
            val start = b.toString().indexOf("(", x)
            if (start == -1) break
            var end = b.toString().indexOf(")", start)
            if (end == -1) break

            // Delete those characters
            b.delete(end, end + 1)
            b.delete(start, start + 1)

            // We have a range, create a span for it
            b.setSpan(HiddenSpan(), start, --end, 0)

            x = end
        }

        // Find the display-exclusive bits.
        // They are wrapped in square brackets: [ ]
        x = 0
        while (x < b.toString().length) {
            val start = b.toString().indexOf("[", x)
            if (start == -1) break
            var end = b.toString().indexOf("]", start)
            if (end == -1) break

            // Delete those characters
            b.delete(end, end + 1)
            b.delete(start, start + 1)
            end--

            // We have a range, create a span for it
            // This only works properly on Lollipop. It's a pretty reasonable target for
            // compatibility, and most TTS software will not speak out Unicode arrows anyway.
            //
            // This works fine with Talkback, but *doesn't* work with Select to Speak.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                b.setSpan(TtsSpan.TextBuilder().setText(" ").build(), start, end, 0)
            }

            x = end
        }

        return FormattedString(b).format(*v)
    }
}
