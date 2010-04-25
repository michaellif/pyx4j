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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class SvgDOM extends DOM {

    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    public static native Element createElementNS(final String ns, final String name)/*-{
        return document.createElementNS(ns, name);
    }-*/;

    public static void setAttributeNS(Element elem, String attr, String value) {
        setAttributeNS(null, elem, attr, value);
    }

    public static native void setAttributeNS(String uri, Element elem, String attr, String value) /*-{
        elem.setAttributeNS(uri, attr, value);
    }-*/;
}
