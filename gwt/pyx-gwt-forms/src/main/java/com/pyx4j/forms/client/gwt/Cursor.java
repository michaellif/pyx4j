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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.gwt;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.widgets.client.util.BrowserType;

/**
 * For details see: http://www.quirksmode.org/css/cursor.html
 * 
 */
public class Cursor {

    public static void setDefault(UIObject object) {
        setDefault(object.getElement());
    }

    public static void setDefault(Element element) {
        setCursor(element, "default");
    }

    public static void setHand(UIObject object) {
        setHand(object.getElement());
    }

    public static void setHand(Element element) {
        if (BrowserType.isIE()) {
            setCursor(element, "hand");
        }
        setCursor(element, "pointer");
    }

    public static void setWait(UIObject object) {
        setWait(object.getElement());
    }

    public static void setWait(Element element) {
        setCursor(element, "wait");
    }

    public static void setProgress(Element element) {
        setCursor(element, "progress");
    }

    public static void setText(Element element) {
        setCursor(element, "text");
    }

    public static void setNoDrop(Element element) {
        setCursor(element, "no-drop");
    }

    public static void setCursor(Element element, String selector) {
        DOM.setStyleAttribute(element, "cursor", selector);
    }

}
