
package com.example.rev_pass_testing_using_metrodroid.multi

import android.util.Log

expect annotation class VisibleForTesting()
expect annotation class Parcelize()
expect annotation class IgnoredOnParcel()
expect interface Parcelable
// Swift doesn't propagate RuntimeException, hence we need this ugly wrapper
fun <T> logAndSwiftWrap(tag: String, msg: String, f: () -> T): T {
    try {
        return f()
    } catch (ex: Exception) {
        Log.e(tag, msg, ex)
        throw Exception(ex)
    }
}