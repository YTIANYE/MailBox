package com.fsck.k9.mail.internet

import com.fsck.k9.mail.Address
import com.fsck.k9.mail.K9LibRobolectricTestRunner
import com.fsck.k9.mail.crlf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(K9LibRobolectricTestRunner::class)
class AddressHeaderBuilderTest {

    @Test
    fun createHeaderValue_withSingleAddress() {
        val addresses = arrayOf(Address("test@domain.example"))

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("test@domain.example", headerValue)
    }

    @Test
    fun createHeaderValue_withTwoAddressesThatFitOnSingleLine() {
        val addresses = arrayOf(
                Address("one@domain.example"),
                Address("two@domain.example")
        )

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("one@domain.example, two@domain.example", headerValue)
    }

    @Test
    fun createHeaderValue_withMultipleAddressesThatNeedWrapping() {
        val addresses = arrayOf(
                Address("one@domain.example", "Person One"),
                Address("two+because.i.can@this.is.quite.some.domain.example", "Person \"Long Email Address\" Two"),
                Address("three@domain.example", "Person Three"),
                Address("four@domain.example", "Person Four"),
                Address("five@domain.example", "Person Five")
        )

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("""
            |Person One <one@domain.example>,
            | "Person \"Long Email Address\" Two" <two+because.i.can@this.is.quite.some.domain.example>,
            | Person Three <three@domain.example>, Person Four <four@domain.example>,
            | Person Five <five@domain.example>
        """.trimMargin().crlf(), headerValue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createHeaderValue_withoutAddresses_shouldThrow() {
        AddressHeaderBuilder.createHeaderValue(emptyArray())
    }
}
