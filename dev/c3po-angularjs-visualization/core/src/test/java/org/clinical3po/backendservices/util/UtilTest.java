/*
 * Copyright 2015 Clinical Personalized Pragmatic Predictions of Outcomes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.clinical3po.backendservices.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clinical3po.backendservices.util.Util;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.System;

/**
 * Created by w.ding on 05/01/15.
 */
public class UtilTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    public UtilTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(UtilTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUserHome() throws Exception {
        String userHome = Util.getUserHome();
        System.out.println("uesrHome = " + userHome);
    }

    public void testWrapErrorToJson() {
        String error = "invalid command";
        String result = Util.wrapErrorToJson(error);
        System.out.println(result);
        assertEquals("{\"error\":\"invalid command\"}", result);
    }
}
