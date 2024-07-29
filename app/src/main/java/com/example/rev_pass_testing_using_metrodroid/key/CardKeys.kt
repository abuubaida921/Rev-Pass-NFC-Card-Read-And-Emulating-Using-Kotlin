

package com.example.rev_pass_testing_using_metrodroid.key

import com.example.rev_pass_testing_using_metrodroid.serializers.CardSerializer
import com.example.rev_pass_testing_using_metrodroid.serializers.jsonObjectOrNull
import com.example.rev_pass_testing_using_metrodroid.serializers.jsonPrimitiveOrNull
import kotlinx.serialization.json.*

interface CardKeys {

    val type: String

    val description: String?

    val fileType: String

    val uid: String?

    val sourceDataLength: Int

    fun toJSON(): JsonObject

    companion object {
        const val JSON_KEY_TYPE_KEY = "KeyType"
        const val TYPE_MFC = "MifareClassic"
        const val TYPE_MFC_STATIC = "MifareClassicStatic"
        const val JSON_TAG_ID_KEY = "TagId"
        const val CLASSIC_STATIC_TAG_ID = "staticclassic"

        /**
         * Reads ClassicCardKeys from the internal (JSON) format.
         *
         * See https://github.com/micolous/metrodroid/wiki/Importing-MIFARE-Classic-keys#json
         */
        fun fromJSON(keyJSON: JsonObject, cardType: String, defaultBundle: String): CardKeys? = when (cardType) {
            TYPE_MFC -> ClassicCardKeys.fromJSON(keyJSON, defaultBundle)
            TYPE_MFC_STATIC -> ClassicStaticKeys.fromJSON(keyJSON, defaultBundle)
            else -> throw IllegalArgumentException("Unknown card type for key: $cardType")
        }

        fun fromJSON(keyJSON: JsonObject, defaultBundle: String): CardKeys? = fromJSON(
            keyJSON,
            keyJSON[JSON_KEY_TYPE_KEY]?.jsonPrimitiveOrNull?.contentOrNull ?: "",
            defaultBundle)

        val jsonParser get() = CardSerializer.jsonPlainStable

        fun fromJsonString(keyJSON: String, defaultBundle: String): CardKeys?
            = jsonParser.parseToJsonElement(keyJSON).jsonObjectOrNull?.let { it ->
                fromJSON(it, defaultBundle) }
    }
}
