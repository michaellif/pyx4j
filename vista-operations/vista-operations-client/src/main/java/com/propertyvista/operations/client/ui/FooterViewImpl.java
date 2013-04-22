/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.config.client.ClientApplicationVersion;
import com.pyx4j.site.client.ui.layout.RiaLayoutPanelTheme;

public class FooterViewImpl extends SimplePanel implements FooterView {

    public FooterViewImpl() {
        setSize("100%", "100%");
        setStyleName(RiaLayoutPanelTheme.StyleName.SiteViewFooter.name());
        HTML label = new HTML(ClientApplicationVersion.instance().getBuildInformation());
        label.getElement().getStyle().setFontSize(1.2, Unit.EM);
        label.getElement().getStyle().setColor("white");
        label.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.open("config", "new", null);

            }
        });
        setWidget(label);
    }
}
