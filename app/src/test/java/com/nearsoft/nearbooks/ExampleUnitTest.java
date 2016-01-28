package com.nearsoft.nearbooks;

import com.google.common.base.Optional;

import junit.framework.Assert;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void testGuava() {
        Optional<Integer> possible = Optional.of(5);
        Assert.assertTrue(possible.isPresent());
        Assert.assertEquals(5, possible.get().intValue());
    }

}