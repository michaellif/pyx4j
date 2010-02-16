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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.pub;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.InlineWidget;

public class PartnersWidget extends InlineWidget {

    private final VerticalPanel contentPanel;

    private final Image image;

    public PartnersWidget() {

        getElement().getStyle().setPadding(5, Unit.PX);

        contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        image = new Image("/images/partners/MEFPartners.png");
        image.setSize("200px", "50px");
        contentPanel.add(image);

        addPartner("Digital Mesh", "/images/partners/1.gif", "http://cnn.com");
        addPartner("Bomgar", "/images/partners/2.gif", "http://cnn.com");
        addPartner("Standpoint", "/images/partners/3.gif", "http://cnn.com");
        addPartner("Efficient Frontier", "/images/partners/4.gif", "http://cnn.com");
        addPartner("Expeed", "/images/partners/5.gif", "http://cnn.com");
        addPartner("Dosatron", "/images/partners/6.gif", "http://cnn.com");
        addPartner("Adaptdev", "/images/partners/7.gif", "http://cnn.com");

        Anchor anchor = new Anchor("more...", "javascript:void(0)");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                History.newItem("pub&aboutUs", true);
            }
        });

        contentPanel.add(anchor);
    }

    private void addPartner(String name, final String imageUrl, String url) {
        Anchor anchor = new Anchor(name, url);

        anchor.getElement().setPropertyString("target", "_blank");

        anchor.getElement().getStyle().setFontSize(1.2, Unit.EM);
        contentPanel.add(anchor);
        anchor.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                image.setUrl(imageUrl);
            }
        });
        anchor.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                image.setUrl("/images/partners/MEFPartners.png");
            }
        });
    }
}