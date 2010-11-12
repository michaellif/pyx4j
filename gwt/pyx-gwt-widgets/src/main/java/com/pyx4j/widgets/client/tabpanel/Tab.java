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
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Tab extends LayoutPanel {

    private ImageResource tabImage;

    private String tabTitle;

    private TabPanel<? extends Tab> parentTabPanel;

    public Tab(Widget contentPane, String tabTitle, ImageResource tabImage) {
        add(contentPane);
        this.tabImage = tabImage;
        this.tabTitle = tabTitle;
    }

    public Tab() {
    }

    public ImageResource getTabImage() {
        return tabImage;
    }

    public void setTabImage(ImageResource tabImage) {
        this.tabImage = tabImage;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public void setContentPane(Widget contentPane) {
        clear();
        add(contentPane);
    }

    protected void setParentTabPanel(TabPanel<? extends Tab> parentTabPanel) {
        this.parentTabPanel = parentTabPanel;
    }

    public TabPanel<? extends Tab> getParentTabPanel() {
        return parentTabPanel;
    }
}
