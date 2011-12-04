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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.News;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;

public class NewsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public NewsPanel(String id) {
        super(id);

        PMSiteWebRequest request = (PMSiteWebRequest) getRequest();
        PMSiteContentManager cm = request.getContentManager();
        final NavigationItem newsNav = cm.getSecondaryNavigItem("news");
        add(new ListView<News>("newsItem", cm.getNews(request.getSiteLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<News> item) {
                News news = item.getModelObject();
                item.add(new Label("date", news.date().getStringView()));
                Component headline = null;
                String headlineText = news.caption().getStringView();
                String headlineId = "headline";
                String itemAnchor = null;
                if (newsNav != null) {
                    itemAnchor = "item" + news.getPrimaryKey().asLong();
                    headline = new PageLink(headlineId, newsNav.getDestination(), newsNav.getPageParameters());
                    ((PageLink) headline).setText(headlineText).setAnchor(itemAnchor);
                } else {
                    headline = new Label(headlineId, headlineText);
                }
                item.add(headline);

                String content = news.content().getStringView();
                Component readMore = null;
                String readmoreId = "more";
                if (content.length() >= 150) {
                    content = content.substring(0, 150) + " ...";
                    if (newsNav != null) {
                        readMore = new PageLink(readmoreId, newsNav.getDestination(), newsNav.getPageParameters());
                        ((PageLink) readMore).setText("&raquo;").setAnchor(itemAnchor).setEscapeModelStrings(false);
                    }
                }
                if (readMore == null) {
                    readMore = new Label(readmoreId, "").setRenderBodyOnly(true);
                }
                item.add(new Label("text", content));
                item.add(readMore);
            }
        });
    }

}
