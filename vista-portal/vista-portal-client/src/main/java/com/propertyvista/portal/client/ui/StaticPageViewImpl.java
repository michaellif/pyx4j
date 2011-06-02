/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class StaticPageViewImpl extends SimplePanel implements StaticPageView {

    private final HTML label;

    public StaticPageViewImpl() {
        FlowPanel panel = new FlowPanel();
        label = new HTML("Static content");
        panel.add(label);
        setWidget(panel);
    }

    @Override
    public void setContent(String content) {
        label.setHTML(content);
    }

}
