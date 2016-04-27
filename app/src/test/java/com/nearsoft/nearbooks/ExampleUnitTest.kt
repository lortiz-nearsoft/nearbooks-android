package com.nearsoft.nearbooks

import com.google.common.base.Optional

import org.junit.Assert

import org.junit.Test

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {

    @Test
    fun testGuava() {
        val possible = Optional.of(5)
        Assert.assertTrue(possible.isPresent)
        Assert.assertEquals(5, possible.get().toInt())
    }

}