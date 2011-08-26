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

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteSession;

public class NavigationPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public NavigationPanel(String id) {
        super(id);

        ListView<NavigationItem> listView = new ListView<NavigationItem>("navigationItem", ((PMSiteSession) getSession()).getMainNavigationItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("destination", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", navItem.getCaption()));
                item.add(link);

                //TODO enhance code to support inner pages, check static pages separately (they can't be located under FindApt or Resident so parent of Static page is another static page).
                //Use some interface marker to group pages under FindApt and Resident
                if (NavigationPanel.this.getPage().getClass().equals(navItem.getDestination())) {

                    String currentPageId = null;
                    if (NavigationPanel.this.getPage().getPageParameters() != null) {
                        currentPageId = NavigationPanel.this.getPage().getPageParameters().getString(PMSiteContentManager.PAGE_ID_PARAM_NAME);
                    }

                    String navigItemPageId = null;
                    if (navItem.getPageParameters() != null) {
                        navigItemPageId = navItem.getPageParameters().getString(PMSiteContentManager.PAGE_ID_PARAM_NAME);
                    }

                    if ((currentPageId == null && navigItemPageId == null) || currentPageId.equals(navigItemPageId)) {
                        link.getParent().add(new AttributeAppender("class", new Model<String>("active"), " "));
                    }
                }
            }
        };
        add(listView);
    }
}