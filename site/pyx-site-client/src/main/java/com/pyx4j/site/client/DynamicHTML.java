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
 * Created on Feb 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DynamicHTML extends HTMLPanel {

    private static final Logger log = LoggerFactory.getLogger(DynamicHTML.class);

    public DynamicHTML(String html) {
        super(html);
        attachLocalAnchors();
    }

    public DynamicHTML(String html, boolean wordWrap) {
        this(html);
        setWordWrap(wordWrap);
    }

    public void setWordWrap(boolean wrap) {
        getElement().getStyle().setProperty("whiteSpace", wrap ? "normal" : "nowrap");
    }

    /**
     * Fix for Local anchor link reload in IE
     */
    protected void attachLocalAnchors() {
        NodeList<Element> linkElements = this.getElement().getElementsByTagName("a");
        if (linkElements != null) {
            String baseUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost() + Window.Location.getPath() + "#";
            for (int i = 0; i < linkElements.getLength(); i++) {
                AnchorElement el = AnchorElement.as(linkElements.getItem(i));
                if (el.getHref().startsWith(baseUrl)) {
                    adoptChild(new HistoryAnchor(el, el.getHref().substring(baseUrl.length())));
                    log.debug("replace href {}", el.getHref());
                }
            }
        }
    }

    public void adoptChild(Widget widget) {
        getChildren().add(widget);
        adopt(widget);
    }
}
