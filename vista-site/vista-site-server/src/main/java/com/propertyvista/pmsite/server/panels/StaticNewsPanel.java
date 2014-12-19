/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 30, 2011
 * @author stanp
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.News;
import com.propertyvista.pmsite.server.PMSiteWebRequest;

public class StaticNewsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public StaticNewsPanel(String id) {
        super(id);

        PMSiteWebRequest request = (PMSiteWebRequest) getRequest();
        add(new ListView<News>("newsItem", request.getContentManager().getNews(request.getSiteLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<News> item) {
                News news = item.getModelObject();
                item.add(new Label("date", news.date().getStringView()).add(AttributeModifier.replace("id", "item" + news.getPrimaryKey().asLong())));
                item.add(new Label("headline", news.caption().getStringView()));
                item.add(new Label("text", news.content().getStringView()));
            }
        });
    }
}
