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
 * Created on Oct 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class POFileReaderTest extends TestCase {

    public void testBasicRead() throws IOException {
        POFile po = new POFileReader().readResource(this.getClass().getClassLoader(), "sample1.po");
        Assert.assertNotNull(po);

        Assert.assertEquals(2, po.entries.size());

        Assert.assertEquals("''{0}'' is not valid. {1}", po.entries.get(0).untranslated);
        Assert.assertEquals("''{0}''n'est pas valide. {1}", po.entries.get(0).translated);
        Assert.assertTrue(po.entries.get(0).references.contains("com/pyx4j/entity/client/ui/flex/CEntityContainer.java:87"));

        Assert.assertEquals("Application is in read-only due to short maintenance.\nPlease try again in one hour", po.entries.get(1).untranslated);
        Assert.assertEquals("L'application est en lecture seule pour cause de maintenance courts.\nS'il vous plaît essayez de nouveau en une heure",
                po.entries.get(1).translated);

    }
}
