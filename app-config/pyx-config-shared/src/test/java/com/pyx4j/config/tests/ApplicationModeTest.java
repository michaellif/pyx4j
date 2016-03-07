/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-05-07
 * @author vlads
 */
package com.pyx4j.config.tests;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.junitcategories.Regression;

import junit.framework.TestCase;

@Category({ Regression.class })
public class ApplicationModeTest extends TestCase {

    public void testOfflineDevelopmentNotInSVN() {
        assertFalse("ApplicationMode.offlineDevelopment = true; should not be committed to GIT", ApplicationMode.offlineDevelopment);
    }
}
