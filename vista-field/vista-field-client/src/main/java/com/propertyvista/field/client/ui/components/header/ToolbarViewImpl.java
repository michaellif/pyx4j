/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.event.ScreenShiftEvent;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.theme.FieldTheme;

public class ToolbarViewImpl extends HorizontalPanel implements ToolbarView {

    public ToolbarViewImpl() {
        setSize("100%", "100%");
        setStyleName(FieldTheme.StyleName.Toolbar.name());

        final Image menuImage = new Image(FieldImages.INSTANCE.menu());
        menuImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.instance();
                AppSite.getEventBus().fireEvent(new ScreenShiftEvent());
            }
        });

        final Image sortImage = new Image(FieldImages.INSTANCE.sort());
        final Image searchImage = new Image(FieldImages.INSTANCE.search());

        add(createHolder(menuImage));
        add(createHolder(sortImage));
        add(createHolder(searchImage));
    }

//    private static class ToolbarSectionPanel extends FlowPanel {
//
//        public ToolbarSectionPanel(String style) {
//            setStyleName(style);
//        }
//
//    }
//
//    protected void addSections(FlowPanel leftSection, FlowPanel centerSection, FlowPanel rightSection) {
//        leftSection.setStyleName(FieldTheme.StyleName.ToolbarSide.name());
//        centerSection.setStyleName(FieldTheme.StyleName.ToolbarCenter.name());
//        rightSection.setStyleName(FieldTheme.StyleName.ToolbarSide.name());
//    }

    private SimplePanel createHolder(Image image) {
        SimplePanel holder = new SimplePanel();
        holder.setStyleName(FieldTheme.StyleName.ToolbarImageHolder.name());
        holder.setWidget(image);
        return holder;
    }
}
