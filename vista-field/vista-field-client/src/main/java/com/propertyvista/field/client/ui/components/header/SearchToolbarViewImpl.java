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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.event.SearchRequestEvent;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.decorators.WatermarkDecoratorBuilder;

public class SearchToolbarViewImpl extends HorizontalPanel implements SearchToolbarView {

    private static final I18n i18n = I18n.get(SearchToolbarViewImpl.class);

    public SearchToolbarViewImpl() {
        setSize("100%", "100%");
        setStyleName(FieldTheme.StyleName.Toolbar.name());

        final Image backImage = new Image(FieldImages.INSTANCE.back());
        backImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                History.back();
            }
        });

        final CTextField searchField = new CTextField();
        new WatermarkDecoratorBuilder<CTextField>(searchField).watermark(i18n.tr("Search")).build();
        searchField.setMaxLength(64);
        VerticalPanel searchFieldcontainer = new VerticalPanel();
        searchFieldcontainer.setStyleName(FieldTheme.StyleName.SearchField.name());
        searchFieldcontainer.add(searchField);

        final Image searchImage = new Image(FieldImages.INSTANCE.search());
        searchImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new SearchRequestEvent(searchField.getValue()));
            }
        });

        HorizontalPanel container = new HorizontalPanel();
        container.setSize("100%", "100%");
        container.setStyleName(FieldTheme.StyleName.SearchPanel.name());

        container.add(backImage);
        container.add(searchFieldcontainer);
        container.add(searchImage);

        container.setCellHorizontalAlignment(backImage, ALIGN_LEFT);
        container.setCellHorizontalAlignment(searchField, ALIGN_CENTER);
        container.setCellHorizontalAlignment(searchImage, ALIGN_RIGHT);

        add(container);
    }
}
