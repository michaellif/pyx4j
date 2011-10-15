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
 * Created on Oct 11, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class POFileWriterTest extends TestCase {

    public void testWrite() throws IOException {
        POFile po = new POFile();
        po.createDefaultHeader();

        POEntry entry1 = new POEntry();
        entry1.untranslated = "Text \"\t\" /A\\";
        entry1.translated = "\u0422\u0435\u043a\u0441\u0442";
        po.entries.add(entry1);

        POEntry entry2 = new POEntry();
        entry2.untranslated = "Access Key";
        entry2.translated = "cl\u00e9 d'acc\u00e8s";
        po.entries.add(entry2);

        POFileWriter poWriter = new POFileWriter();

        File file = new File(new File("target"), "a.po");
        poWriter.write(file, po);

        POFile po2 = new POFileReader().read(file);
        Assert.assertNotNull(po2);

        Assert.assertEquals(2, po.entries.size());

        Assert.assertEquals(entry1.untranslated, po.entries.get(0).untranslated);
        Assert.assertEquals(entry1.translated, po.entries.get(0).translated);

        Assert.assertEquals(entry2.untranslated, po.entries.get(1).untranslated);
        Assert.assertEquals(entry2.translated, po.entries.get(1).translated);
    }
}
