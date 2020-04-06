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
        val addresses = arrayOf(Address("test@domain.fsck"))

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("test@domain.fsck", headerValue)
    }

    @Test
    fun createHeaderValue_withTwoAddressesThatFitOnSingleLine() {
        val addresses = arrayOf(
                Address("one@domain.fsck"),
                Address("two@domain.fsck")
        )

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("one@domain.fsck, two@domain.fsck", headerValue)
    }

    @Test
    fun createHeaderValue_withMultipleAddressesThatNeedWrapping() {
        val addresses = arrayOf(
                Address("one@domain.fsck", "Person One"),
                Address("two+because.i.can@this.is.quite.some.domain.fsck", "Person \"Long Email Address\" Two"),
                Address("three@domain.fsck", "Person Three"),
                Address("four@domain.fsck", "Person Four"),
                Address("five@domain.fsck", "Person Five")
        )

        val headerValue = AddressHeaderBuilder.createHeaderValue(addresses)

        assertEquals("""
            |Person One <one@domain.fsck>,
            | "Person \"Long Email Address\" Two" <two+because.i.can@this.is.quite.some.domain.fsck>,
            | Person Three <three@domain.fsck>, Person Four <four@domain.fsck>,
            | Person Five <five@domain.fsck>
        """.trimMargin().crlf(), headerValue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createHeaderValue_withoutAddresses_shouldThrow() {
        AddressHeaderBuilder.createHeaderValue(emptyArray())
    }
}
