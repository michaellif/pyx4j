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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.propertyvista.domain.ref.City;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.AptListPage;

public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        super("footer");

        final PMSiteContentManager contentManager = ((PMSiteSession) getSession()).getContentManager();

        add(new ListView<City>("footer_locations_city", PMSiteContentManager.getCities()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<City> item) {
                City city = item.getModelObject();
                String _city = city.name().getValue();
                String _prov = city.province().name().getValue();
                String _prov2 = city.province().code().getValue();
                if (_city != null && _prov != null && _prov2 != null) {
                    PageParameters params = new PageParameters();
                    params.add("city", _city);
                    params.add("province", _prov);
                    BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", AptListPage.class, params);
                    link.add(new Label("city", _city + " (" + _prov2 + ")"));
                    item.add(link);
                }
            }
        });

        add(new ListView<NavigationItem>("footer_link", ((PMSiteSession) getSession()).getFooterNavigItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", PMSiteContentManager.getCaption(navItem.getPageDescriptor(), contentManager.getLocale())));
                item.add(link);
            }
        });

        add(new Label("footer_legal", "© Starlight Apartments 2011"));
    }
}