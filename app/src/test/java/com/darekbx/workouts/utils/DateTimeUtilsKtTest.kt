package com.darekbx.workouts.utils

import org.junit.Test

class DateTimeUtilsKtTest {

    @Test
    fun testToSeconds() {
        assert(10L == "00:10".toSeconds())
        assert(61L == "01:01".toSeconds())
        assert(3045L == "50:45".toSeconds())
        assert(7342L == "02:02:22".toSeconds())
    }
}
