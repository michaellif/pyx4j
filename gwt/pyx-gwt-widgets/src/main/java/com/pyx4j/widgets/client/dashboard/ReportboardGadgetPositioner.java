/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 19, 2011
 * @author Misha
 */
package com.pyx4j.widgets.client.dashboard;

import com.pyx4j.gwt.commons.ui.SimplePanel;

class ReportboardGadgetPositioner extends SimplePanel {

    public ReportboardGadgetPositioner(int height) {
        addStyleName(DashboardTheme.StyleName.DashboardDndReportPositioner.name());

        getStyle().setProperty("WebkitBoxSizing", "border-box");
        getStyle().setProperty("MozBoxSizing", "border-box");
        getStyle().setProperty("boxSizing", "border-box");
        getStyle().setZIndex(100);

        setHeight(height + "px");
    }
}
