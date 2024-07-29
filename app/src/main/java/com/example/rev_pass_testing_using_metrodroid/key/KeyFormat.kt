

package com.example.rev_pass_testing_using_metrodroid.key

import com.example.rev_pass_testing_using_metrodroid.multi.Log
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * Used by [au.id.micolous.metrodroid.key.KeyFormat.Companion.detectKeyFormat] to return the format of a key contained within a
 * file.
 */
enum class KeyFormat {
    /** Format is unknown  */
    UNKNOWN,
    /** Traditional raw (farebotkeys) binary format  */
    RAW_MFC,
    /** JSON format (unspecified)  */
    JSON,
    /** JSON format (MifareClassic, with UID)  */
    JSON_MFC,
    /** JSON format (MifareClassic, without UID)  */
    JSON_MFC_NO_UID,
    /** JSON format (MifareClassicStatic)  */
    JSON_MFC_STATIC;

    val isJSON: Boolean
        get() = (this == JSON
                || this == JSON_MFC
                || this == JSON_MFC_NO_UID
                || this == JSON_MFC_STATIC)

    companion object {
        const val TAG = "KeyFormat"
        private const val MIFARE_SECTOR_COUNT_MAX = 40
        private const val MIFARE_KEY_LENGTH = 6

        private fun isRawMifareClassicKeyFileLength(length: Int): Boolean {
            return length > 0 &&
                    length % MIFARE_KEY_LENGTH == 0 &&
                    length <= MIFARE_SECTOR_COUNT_MAX * MIFARE_KEY_LENGTH * 2
        }

        private fun rawFormat(length: Int) = if (isRawMifareClassicKeyFileLength(length)) RAW_MFC else UNKNOWN

        fun detectKeyFormat(data: ByteArray): KeyFormat {
            if (data[0] != '{'.code.toByte()) {
                // This isn't a JSON file.
                Log.d(TAG, "couldn't find starting {")
                return rawFormat(data.size)
            }

            // Scan for the } at the end of the file.
            for (i in (data.size - 1) downTo 0) {
                val c = data[i]
                if (c <= 0) {
                    Log.d(TAG, "unsupported encoding at byte $i")
                    return rawFormat(data.size)
                }
                if (c in listOf('\n', '\r', '\t', ' ').map { it.code.toByte() }) {
                    continue
                }

                if (c == '}'.code.toByte()) {
                    break
                }

                // This isn't a JSON file.
                Log.d(TAG, "couldn't find ending }")
                return if (isRawMifareClassicKeyFileLength(data.size)) RAW_MFC else UNKNOWN
            }

            // Now see if it actually parses.
            try {
                val o = CardKeys.jsonParser.parseToJsonElement(
                    data.decodeToString()
                ).jsonObject
                val type = o[CardKeys.JSON_KEY_TYPE_KEY]?.jsonPrimitiveOrNull?.contentOrNull
                when(type) {
                    CardKeys.TYPE_MFC ->
                        return if (o[CardKeys.JSON_TAG_ID_KEY]?.jsonPrimitiveOrNull?.contentOrNull?.isEmpty() != false) {
                            JSON_MFC_NO_UID
                        } else {
                            JSON_MFC
                        }

                    CardKeys.TYPE_MFC_STATIC -> return JSON_MFC_STATIC
                }

                // Unhandled JSON format
                return JSON
            } catch (e: Exception) {
                Log.d(TAG, "couldn't parse JSON object in detectKeyFormat", e)
            }

            // Couldn't parse as JSON -- fallback
            return rawFormat(data.size)
        }

    }
}
