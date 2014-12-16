/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

@SuppressWarnings("serial")
public class TextCreationRequest extends DocCreationRequest {

    public enum TextFormat {

        DOCX("docx", "Microsoft Office Word 2007 (DOCX)"),

        ODT("odt", "OpenDocument Text Document (ODT)");

        private String[] extensions;

        private String name;

        TextFormat(String extension, String name) {
            this.extensions = new String[] { extension };
            this.name = name;
        }

    }

    private TextFormat textFormat;

}
