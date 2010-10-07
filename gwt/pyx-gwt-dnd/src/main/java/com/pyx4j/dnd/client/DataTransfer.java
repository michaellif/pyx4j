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
 * Created on 2010-10-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.dnd.client;

import java.util.Arrays;

import com.google.gwt.core.client.JavaScriptObject;

import com.pyx4j.commons.ConverterUtils;

public class DataTransfer extends JavaScriptObject {

    public static final String TYPE_TEXT = "text/plain";

    public static final String TYPE_HTML = "text/html";

    public static final String TYPE_URL = "text/uri-list";

    protected DataTransfer() {
    }

    public final native String[] getTypes() /*-{
        return this.types;
    }-*/;

    public final String toDebugString() {
        StringBuilder b = new StringBuilder();
        if (getTypes() != null) {
            b.append(" types:").append(ConverterUtils.convertStringCollection(Arrays.asList(getTypes())));
        }
        return b.toString();
    }
}
