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
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DynamicHTML extends HTMLPanel {

    private static final Logger log = LoggerFactory.getLogger(DynamicHTML.class);

    private final ClientBundleWithLookup bundle;

    public DynamicHTML(String html, ClientBundleWithLookup bundle, boolean wordWrap) {
        super(html);
        this.bundle = bundle;
        attachLocalAnchors();
        substituteBundleImages();

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

    private void substituteBundleImages() {
        NodeList<Element> imageElements = this.getElement().getElementsByTagName("img");
        if (imageElements != null) {
            for (int i = 0; i < imageElements.getLength(); i++) {
                ImageElement el = ImageElement.as(imageElements.getItem(i));
                if (el.getSrc().startsWith("data:")) {
                    Element parent = el.getParentElement();
                    String imageId = el.getSrc().substring("data:".length());
                    ImageResource imageResource = (ImageResource) bundle.getResource(imageId);
                    if (imageResource != null) {
                        log.debug("replace img {} with {}", imageId, imageResource.getURL());
                        Image image = new Image(imageResource);
                        ImageElement newImageElement = (ImageElement) Element.as(image.getElement());
                        newImageElement.setHeight(el.getHeight());
                        newImageElement.setWidth(el.getWidth());
                        newImageElement.setAlt(el.getAlt());
                        parent.insertAfter(image.getElement(), el);
                        adoptChild(image);
                        parent.removeChild(el);
                    } else {
                        log.error("failed to find image {} in bundle", imageId);
                    }
                }
            }
        }
    }

    public void adoptChild(Widget widget) {
        getChildren().add(widget);
        adopt(widget);
    }

    class BundleImage extends Image {
        BundleImage(Element element) {
            super(element);
        }
    }
}
