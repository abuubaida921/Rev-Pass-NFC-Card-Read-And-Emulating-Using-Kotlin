package com.example.rev_pass_testing_using_metrodroid.util

interface Input {
    fun readBytes(sz: Int): ByteArray
    fun readToString(): String
    fun close()
}
