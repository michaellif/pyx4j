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

import java.util.ArrayList;
import java.util.Date;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.domain.ref.City;
import com.propertyvista.pmsite.server.PMSiteContentManager.SocialSite;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.pages.AptListPage;

public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        super("footer");

        add(new ListView<City>("footer_locations_city", ((PMSiteWebRequest) getRequest()).getContentManager().getCities()) {
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
                    params.add("searchType", "city");
                    BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", AptListPage.class, params);
                    link.add(new Label("city", _city + " (" + _prov2 + ")"));
                    item.add(link);
                }
            }
        });

        final java.util.Map<SocialSite, String> socialLinks = ((PMSiteWebRequest) getRequest()).getContentManager().getSocialLinks();
        add(new ListView<SocialSite>("footer_social", new ArrayList<SocialSite>(socialLinks.keySet())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<SocialSite> item) {
                SocialSite site = item.getModelObject();
                item.add(new ExternalLink("link", socialLinks.get(site)).add(new AttributeAppender("class", " " + site.name())));
            }
        });

        add(new ListView<NavigationItem>("footer_link", ((PMSiteWebRequest) getRequest()).getContentManager().getFooterNavigItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", ((PMSiteWebRequest) getRequest()).getContentManager().getCaption(navItem.getPageDescriptor(),
                        ((PMSiteWebRequest) getRequest()).getSiteLocale())));
                item.add(link);
            }
        });

        Label copy = new Label("footer_legal", ((PMSiteWebRequest) getRequest()).getContentManager().getCopyrightInfo(
                ((PMSiteWebRequest) getRequest()).getSiteLocale()));
        if (ApplicationMode.isDevelopment()) {
            copy.setDefaultModelObject(copy.getDefaultModelObjectAsString() + "<br/>" + new Date());
            copy.setEscapeModelStrings(false);
        }

        add(copy);
    }
}