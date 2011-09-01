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

import java.util.List;

import org.apache.wicket.PageParameters;
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
import com.propertyvista.pmsite.server.pages.StaticPage;

public class SecondaryNavigationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public SecondaryNavigationPanel(String id, StaticPage page) {
        super(id);

        final PMSiteContentManager contentManager = ((PMSiteSession) getSession()).getContentManager();

        PageParameters mainNavigParams = new PageParameters();
        mainNavigParams.add(PMSiteContentManager.PARAMETER_NAMES[0], page.getPageParameters().getString(PMSiteContentManager.PARAMETER_NAMES[0]));

        PageDescriptor descriptor = contentManager.getStaticPageDescriptor(mainNavigParams);

        List<NavigationItem> items = ((PMSiteSession) getSession()).getNavigItems(descriptor);

        ListView<NavigationItem> listView = new ListView<NavigationItem>("secondaryNavigItem", items) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("destination", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", PMSiteContentManager.getCaption(navItem.getPageDescriptor(), contentManager.getLocale())));
                item.add(link);

                boolean active = false;

                PageDescriptor currentPage = contentManager.getStaticPageDescriptor(SecondaryNavigationPanel.this.getPage().getPageParameters());
                if (currentPage.equals(navItem.getPageDescriptor())) {
                    active = true;
                } else if (!currentPage._path().isNull() && !currentPage._path().isEmpty()) {
                    for (PageDescriptor descriptor : currentPage._path()) {
                        if (!descriptor.isNull() && descriptor.equals(navItem.getPageDescriptor())) {
                            active = true;
                            break;
                        }
                    }
                }
                if (active) {
                    link.getParent().add(new AttributeAppender("class", new Model<String>("active"), " "));
                }

            }
        };
        if (listView.getViewSize() == 0) {
            setVisible(false);
        } else {
            add(listView);
        }
    }
}