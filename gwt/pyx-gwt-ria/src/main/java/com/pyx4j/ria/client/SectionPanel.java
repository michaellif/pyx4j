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
 * Created on Apr 21, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;

public class SectionPanel extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_SectionPanel";

    public static enum StyleSuffix implements IStyleName {
        Root, Content
    }

    private final LayoutPanel contentPanelHolder;

    private final DockLayoutPanel rootPanel;

    private final SimplePanel headerMark;

    public SectionPanel() {

        super();

        rootPanel = new DockLayoutPanel(Unit.EM);

        headerMark = new SimplePanel();
        rootPanel.addNorth(headerMark, 0);

        contentPanelHolder = new LayoutPanel();

        rootPanel.add(contentPanelHolder);

        add(rootPanel);

        setStylePrefix(DEFAULT_STYLE_PREFIX);
    }

    public void setStylePrefix(String styleName) {
        setStyleName(styleName);
        rootPanel.setStyleName(styleName + StyleSuffix.Root);
        contentPanelHolder.setStyleName(styleName + StyleSuffix.Content);
    }

    protected void setHeaderPane(Widget headerPane) {
        rootPanel.insertNorth(headerPane, 1.9, headerMark);
    }

    protected void setContentPane(Widget pagePane) {
        contentPanelHolder.add(pagePane);
    }

}
