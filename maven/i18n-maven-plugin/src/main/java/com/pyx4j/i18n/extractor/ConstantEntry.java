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
 * Created on Oct 6, 2011
 * @author vlads
 */
package com.pyx4j.i18n.extractor;

import java.util.List;
import java.util.Vector;

public class ConstantEntry {

    public final String context;

    public final String text;

    public List<String> reference;

    public boolean javaFormatFlag;

    public List<String> comments;

    public ConstantEntry(String sourceFileName, int lineNr, String context, String text, boolean javaFormatFlag, String... comments) {
        this.text = text;
        this.context = context;
        this.reference = new Vector<String>();
        this.javaFormatFlag = javaFormatFlag;
        addReference(sourceFileName, lineNr, javaFormatFlag, comments);
    }

    public void addReference(String classSourceFileName, int lineNr, boolean javaFormatFlag, String... comments) {
        if (javaFormatFlag) {
            this.javaFormatFlag = true;
        }
        String fmt = classSourceFileName + ":" + lineNr;
        if (!this.reference.contains(fmt)) {
            this.reference.add(fmt);
        }

        if (comments != null) {
            for (String comment : comments) {
                if (comment == null) {
                    continue;
                }
                if (this.comments == null) {
                    this.comments = new Vector<String>();
                }
                if (!this.comments.contains(comment)) {
                    this.comments.add(comment);
                }
            }
        }
    }

}
