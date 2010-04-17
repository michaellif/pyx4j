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
 * Created on Jan 17, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class InlineWidgetRootPanel extends AbsolutePanel {

    private static Map<String, InlineWidgetRootPanel> rootPanels = new HashMap<String, InlineWidgetRootPanel>();

    public static InlineWidgetRootPanel get(String widgetId) {
        InlineWidgetRootPanel rp = rootPanels.get(widgetId);

        Element elem = null;
        if (widgetId != null) {
            if (null == (elem = Document.get().getElementById(widgetId))) {
                return null;
            }
        }

        if (rp != null) {
            // If the element associated with an existing RootPanel has been replaced
            // for any reason, return a new RootPanel rather than the existing one (
            // see issue 1937).
            if ((elem == null) || (rp.getElement() == elem)) {
                // There's already an existing RootPanel for this element. Return it.
                return rp;
            }
        }

        rp = new InlineWidgetRootPanel(elem);

        rootPanels.put(widgetId, rp);
        return rp;
    }

    private InlineWidgetRootPanel(Element elem) {
        super(elem.<com.google.gwt.user.client.Element> cast());
        onAttach();
    }

    InlineWidgetRootPanel(Element elem, boolean tmpTestHackVlads) {
        super(elem.<com.google.gwt.user.client.Element> cast());
    }
}