/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 8, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.NewsDataModel;

public class NewsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public NewsPanel(String id) {
        super(id);

        add(new ListView<NewsDataModel>("newsItem", PMSiteContentManager.getNews()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NewsDataModel> item) {
                NewsDataModel news = item.getModelObject();
                item.add(new Label("date", news.getDate().toString()));
                item.add(new Label("headline", news.getHeadline()));
                item.add(new Label("text", news.getText()));
                item.add(new Label("more", ""));
            }
        });
    }

}
