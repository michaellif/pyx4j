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
 * Created on Apr 23, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.ria.client.SectionPanel.StyleSuffix;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class HeaderPanel extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_HeaderPanel";

    public static enum StyleSuffix implements IStyleSuffix {
        Label, Logo,
    }

    private static final String HEADER_HEIGHT = "25px";

    private final Label label;

    private final Image logoImage;

    public HeaderPanel(String text, ImageResource image) {

        ensureDebugId("HeaderPanel");

        label = new Label(text, false);

        HorizontalPanel panel = new HorizontalPanel();
        panel.setSpacing(0);

        logoImage = new Image(image);

        panel.add(logoImage);
        panel.setCellVerticalAlignment(logoImage, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(label);
        panel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_LEFT);
        panel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellWidth(label, "100%");
        add(panel);

        setHeight(HEADER_HEIGHT);
        setWidth("100%");
        setStylePrefix(DEFAULT_STYLE_PREFIX);

    }

    public void setStylePrefix(String styleName) {
        setStyleName(styleName);
        label.setStyleName(styleName + StyleSuffix.Label);
        logoImage.setStyleName(styleName + StyleSuffix.Logo);
    }

}
