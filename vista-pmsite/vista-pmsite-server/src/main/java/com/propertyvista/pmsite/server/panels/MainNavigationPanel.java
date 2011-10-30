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

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.InquiryPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.SignInPage;
import com.propertyvista.pmsite.server.pages.StaticPage;
import com.propertyvista.pmsite.server.pages.UnitDetailsPage;

public class MainNavigationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public MainNavigationPanel(String id) {
        super(id);

        ListView<NavigationItem> listView = new ListView<NavigationItem>("navigationItem", ((PMSiteSession) getSession()).getMainNavigItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("destination", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", PMSiteContentManager.getCaption(navItem.getPageDescriptor(), PMSiteContentManager.getLocale())));
                item.add(link);

                boolean active = false;

                if (MainNavigationPanel.this.getPage() instanceof StaticPage) {
                    PageDescriptor currentPage = PMSiteContentManager.getStaticPageDescriptor(MainNavigationPanel.this.getPage().getPageParameters());
                    if (currentPage.equals(navItem.getPageDescriptor())) {
                        active = true;
                    } else if (!currentPage._path().isNull() && !currentPage._path().isEmpty()) {
                        PageDescriptor mainNavigParent = currentPage._path().get(0);
                        if (!mainNavigParent.isNull() && mainNavigParent.equals(navItem.getPageDescriptor())) {
                            active = true;
                        }
                    }

                } else if (FindAptPage.class.equals(navItem.getDestination())

                && ((MainNavigationPanel.this.getPage() instanceof FindAptPage)

                || (MainNavigationPanel.this.getPage() instanceof InquiryPage)

                || (MainNavigationPanel.this.getPage() instanceof AptDetailsPage)

                || (MainNavigationPanel.this.getPage() instanceof UnitDetailsPage)

                || (MainNavigationPanel.this.getPage() instanceof AptListPage))) {

                    active = true;

                } else if (ResidentsPage.class.equals(navItem.getDestination())

                && ((MainNavigationPanel.this.getPage() instanceof ResidentsPage)

                || (MainNavigationPanel.this.getPage() instanceof SignInPage))) {

                    active = true;

                }

                if (active) {
                    link.getParent().add(new AttributeAppender("class", new Model<String>("active"), " "));
                }

            }

        };

        add(listView);
    }
}