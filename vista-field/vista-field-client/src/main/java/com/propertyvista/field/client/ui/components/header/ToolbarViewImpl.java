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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.DropDownPanel;

import com.propertyvista.field.client.event.EventSource;
import com.propertyvista.field.client.event.ScreenShiftEvent;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.components.sort.SortDropDownPanel;
import com.propertyvista.field.rpc.FieldSiteMap;

public class ToolbarViewImpl extends HorizontalPanel implements ToolbarView {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    public ToolbarViewImpl() {
        setSize("100%", "100%");
        setStyleName(FieldTheme.StyleName.Toolbar.name());

        final SortDropDownPanel sortPanel = new SortDropDownPanel();

        final Image menuImage = new Image(FieldImages.INSTANCE.menu());
        menuImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new ScreenShiftEvent(EventSource.ToolbarMenuImage));
            }
        });

        final Image sortImage = new Image(FieldImages.INSTANCE.sort());
        sortImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (sortPanel.isShowing()) {
                    sortPanel.hide();
                } else {
                    showPanel(sortPanel);
                }
            }
        });

        final Image searchImage = new Image(FieldImages.INSTANCE.search());
        searchImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new FieldSiteMap.Search());
            }
        });

        add(createHolder(menuImage));
        add(createHolder(sortImage));
        add(createHolder(searchImage));
    }

    private void showPanel(DropDownPanel panel) {
        panel.showRelativeTo(this);
    }

    private SimplePanel createHolder(Image image) {
        SimplePanel holder = new SimplePanel();
        holder.setStyleName(FieldTheme.StyleName.ToolbarImageHolder.name());
        holder.setWidget(image);
        return holder;
    }

}
