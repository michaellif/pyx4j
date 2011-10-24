/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Nov 10, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.test;

import junit.framework.TestCase;

import com.pyx4j.commons.css.StyleManger;

public class StyleMangerTest extends TestCase {

    public void testAlternativeHostnames() {
        StyleManger.setAlternativeHostnameSufix("www44.pyx4j.com", "www", "-a", "-b");
        assertEquals("http://www-a.pyx4j.com/", StyleManger.getAlternativeHostname());
        assertEquals("http://www-b.pyx4j.com/", StyleManger.getAlternativeHostname());
        assertEquals("http://www-a.pyx4j.com/", StyleManger.getAlternativeHostname());
    }
}
