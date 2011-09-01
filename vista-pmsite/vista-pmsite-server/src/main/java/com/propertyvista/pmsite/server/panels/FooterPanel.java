/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.ref.City;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.AptListPage;

public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        super("footer");

        add(new ListView<City>("footer_locations_city", PMSiteContentManager.getCities()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<City> item) {
                City city = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("city", city.name().getValue());
                params.add("province", city.province().code().getValue());
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", AptListPage.class, params);
                link.add(new Label("city", city.name().getValue() + " (" + city.province().code().getValue() + ")"));
                item.add(link);
            }
        });

        add(new ListView<NavigationItem>("footer_link", ((PMSiteSession) getSession()).getFooterNavigItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", navItem.getCaption()));
                item.add(link);
            }
        });

        add(new Label("footer_legal", "� Starlight Apartments 2011"));
    }
}