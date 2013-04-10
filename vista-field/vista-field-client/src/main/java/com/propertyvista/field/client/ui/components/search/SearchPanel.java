/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 10, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.search;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.decorators.WatermarkDecoratorBuilder;

public class SearchPanel extends PopupPanel {

    private static final I18n i18n = I18n.get(SearchPanel.class);

    public SearchPanel() {
        setSize("100%", "100%");
        setStyleName(FieldTheme.StyleName.SearchPanel.name());

//        DockLayoutPanel layout = new DockLayoutPanel(Unit.PCT);
//        layout.setSize("100%", "100%");
//
//        layout.addNorth(new SearchPanelToolbar(), 10);
//        layout.add(new SearchResults());
//
//        add(layout);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.setSize("100%", "100%");
        vPanel.add(new SearchPanelToolbar());
        vPanel.add(new SearchResults());
        add(vPanel);
    }

    private class SearchPanelToolbar extends HorizontalPanel {

        public SearchPanelToolbar() {
            setSize("100%", "100%");
            setStyleName(FieldTheme.StyleName.SearchPanelToolbar.name());

            final Image backImage = new Image(FieldImages.INSTANCE.back());
            backImage.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    hideMainPanel();
                }
            });

            CTextField searchField = new CTextField();
            new WatermarkDecoratorBuilder<CTextField>(searchField).watermark(i18n.tr("Search")).build();

            final Image searchImage = new Image(FieldImages.INSTANCE.search());

            add(backImage);
            add(searchField);
            add(searchImage);
        }
    }

    private class SearchResults extends SimplePanel {

        public SearchResults() {
            setSize("100%", "100%");
            setStyleName(FieldTheme.StyleName.SearchResults.name());
            setWidget(new Button("Search Results"));
        }
    }

    private void hideMainPanel() {
        this.hide();
    }
}
