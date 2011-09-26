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
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.tree.analysis.AnalyzerException;

public class Verify {

    /**
     * @param args
     * @throws AnalyzerException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, AnalyzerException {
        InputStream in = ConstantTracker.class.getResourceAsStream("Sample.class");
        try {
            ConstantTracker.findConstantArgumentsToPrintln(in);
        } finally {
            in.close();
        }
    }

}
