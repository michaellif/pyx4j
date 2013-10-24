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
package com.propertyvista.crm.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.config.client.ClientApplicationVersion;
import com.pyx4j.widgets.client.Tooltip;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.SiteViewTheme;

public class FooterViewImpl extends SimplePanel implements FooterView {

    public FooterViewImpl() {
        setSize("100%", "100%");
        setStyleName(SiteViewTheme.StyleName.SiteViewFooter.name());
        VerticalPanel panel = new VerticalPanel();
        final Image image = new Image(VistaImages.INSTANCE.logo().getSafeUri());

        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("http://www.propertyvista.com", "home", null);
            }
        });

        image.addDragHandler(new DragHandler() {
            @Override
            public void onDrag(DragEvent event) {
                Tooltip.tooltip(image, ClientApplicationVersion.instance().getBuildInformation());
            }
        });

        panel.setHeight("45px");
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(image);
        setWidget(panel);
    }
}
