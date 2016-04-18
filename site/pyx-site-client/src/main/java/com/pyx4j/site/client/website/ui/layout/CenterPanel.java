/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 29, 2013
 * @author michaellif
 */
package com.pyx4j.site.client.website.ui.layout;

import com.google.gwt.dom.client.Style.Position;

import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

public class CenterPanel extends FlowPanel {

    public CenterPanel(FlowPanel contentPanel) {
        ensureDebugId(getClass().getSimpleName());
        setStyleName(WebSiteLayoutTheme.StyleName.WebSiteLayoutContentHolder.name());
        getStyle().setProperty("maxWidth", WebSiteLayoutPanel.MAX_WIDTH + "px");
        addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());
        getStyle().setPosition(Position.RELATIVE);

        SimplePanel backgroundPanel = new SimplePanel();
        backgroundPanel.getStyle().setPosition(Position.ABSOLUTE);
        backgroundPanel.getStyle().setProperty("top", "0");
        backgroundPanel.getStyle().setProperty("bottom", "0");
        backgroundPanel.getStyle().setProperty("left", "0");
        backgroundPanel.getStyle().setProperty("right", "0");
        backgroundPanel.setStyleName(WebSiteLayoutTheme.StyleName.WebSiteLayoutContentBackground.name());
        add(backgroundPanel);

        FlowPanel containerPanel = new FlowPanel();
        containerPanel.ensureDebugId(getClass().getSimpleName() + ".containerPanel");
        add(containerPanel);

        containerPanel.add(contentPanel);
    }

}
