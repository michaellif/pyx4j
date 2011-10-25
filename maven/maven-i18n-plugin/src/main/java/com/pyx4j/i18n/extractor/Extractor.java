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
 * Created on Oct 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.xml.sax.SAXException;

import com.pyx4j.i18n.extractor.xml.XMLConstantExtractor;

public class Extractor {

    private final Map<String, ConstantEntry> constants = new HashMap<String, ConstantEntry>();

    BytecodeConstantExtractor bytecodeConstantExtractor;

    XMLConstantExtractor xmlConstantExtractor;

    public Extractor() {
        bytecodeConstantExtractor = new BytecodeConstantExtractor(this);
        xmlConstantExtractor = new XMLConstantExtractor(this);
    }

    public Collection<ConstantEntry> getConstants() {
        return constants.values();
    }

    public Collection<String> getConstantsText() {
        Collection<String> r = new HashSet<String>();
        for (ConstantEntry entry : this.getConstants()) {
            r.add(entry.text);
        }
        return r;
    }

    public void addEntry(String sourceFileName, int lineNr, String text, boolean javaFormatFlag, String... comments) {
        if (text.length() != 0) {
            ConstantEntry entry = constants.get(text);
            if (entry == null) {
                constants.put(text, new ConstantEntry(sourceFileName, lineNr, text, javaFormatFlag, comments));
            } else {
                entry.addReference(sourceFileName, lineNr, comments);
            }
        }
    }

    public void readClass(InputStream in) throws IOException, AnalyzerException {
        bytecodeConstantExtractor.readClass(in);
    }

    public void readClass(Class<?> in) throws IOException, AnalyzerException {
        bytecodeConstantExtractor.readClass(in);
    }

    public void readXMLFile(InputStream in, String name) throws IOException, SAXException {
        xmlConstantExtractor.readFile(in, name);
    }

    public void complete() {
        bytecodeConstantExtractor.analyzeTranslatableHierarchy();
    }

}
