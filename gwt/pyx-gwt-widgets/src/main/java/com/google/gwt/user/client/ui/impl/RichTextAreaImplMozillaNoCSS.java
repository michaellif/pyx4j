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
 * Created on 2010-07-19
 * @author vlads
 * @version $Id$
 */
package com.google.gwt.user.client.ui.impl;

public class RichTextAreaImplMozillaNoCSS extends RichTextAreaImplMozilla {

    private boolean initNoCss = true;

    @Override
    public void uninitElement() {
        super.uninitElement();
        initNoCss = true;
    }

    @Override
    void execCommand(String cmd, String param) {
        if (initNoCss) {
            super.execCommand("useCSS", "false");
            super.execCommand("styleWithCSS", "false");
            initNoCss = false;
        }
        super.execCommand(cmd, param);
    }

}
