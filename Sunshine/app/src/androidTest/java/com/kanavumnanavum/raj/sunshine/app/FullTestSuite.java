package com.kanavumnanavum.raj.sunshine.app;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by raj on 9/28/14.
 */
public class FullTestSuite
{
    public FullTestSuite()
    {

    }
   public static Test suite()
   {
       return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
   }
}
