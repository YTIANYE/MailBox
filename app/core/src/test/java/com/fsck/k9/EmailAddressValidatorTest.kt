package com.fsck.k9

import org.junit.Assert
import org.junit.Test

class EmailAddressValidatorTest {

    @Test
    fun testEmailValidation() {
        // Most of the tests based on https://en.wikipedia.org/wiki/Email_address#Examples
        val validator = EmailAddressValidator()
        Assert.assertTrue(validator.isValidAddressOnly("simple@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("very.common@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("disposable.style.email.with+symbol@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("other.email-with-hyphen@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("fully-qualified-domain@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("user.name+tag+sorting@fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("fsck-indeed@strange-fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("fsck-indeed@strange_example.com"))
        Assert.assertTrue(validator.isValidAddressOnly("fsck@1.com"))
        Assert.assertTrue(validator.isValidAddressOnly("admin@mailserver1"))
        Assert.assertTrue(validator.isValidAddressOnly("user@localserver"))
        Assert.assertTrue(validator.isValidAddressOnly("\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.fsck.com"))
        Assert.assertTrue(validator.isValidAddressOnly("\"()<>[]:,;@\\\\\\\"!#$%&'-/=?^_`{}| ~.a\"@fsck.org"))
        Assert.assertTrue(validator.isValidAddressOnly("\" \"@fsck.org"))
        Assert.assertTrue(validator.isValidAddressOnly("x@fsck.com"))

        Assert.assertFalse(validator.isValidAddressOnly("Abc.fsck.com"))
        Assert.assertFalse(validator.isValidAddressOnly("\"not\"right@fsck.com"))
        Assert.assertFalse(validator.isValidAddressOnly("john.doe@fsck..com"))
        Assert.assertFalse(validator.isValidAddressOnly("fsck@c.2"))
        Assert.assertFalse(validator.isValidAddressOnly("this\\ still\\\"not\\\\allowed@fsck.com"))
        Assert.assertFalse(validator.isValidAddressOnly("john..doe@fsck.com"))
        Assert.assertFalse(validator.isValidAddressOnly("invalidperiod.@fsck.com"))
    }
}
