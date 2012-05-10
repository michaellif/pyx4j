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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.News;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.domain.site.gadgets.NewsGadgetContent;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;

public class NewsGadgetPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public NewsGadgetPanel(String id, HomePageGadget gadget) {
        super(id);

        @SuppressWarnings("unchecked")
        GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
        if (GadgetType.news.equals(type)) {
            PMSiteWebRequest request = (PMSiteWebRequest) getRequest();
            PMSiteContentManager cm = request.getContentManager();
            final NavigationItem newsNav = cm.getSecondaryNavigItem("news");
            // get localized news from the gadget content
            NewsGadgetContent content = gadget.content().cast();
            List<News> locNews = new ArrayList<News>();
            AvailableLocale siteLocale = request.getSiteLocale();
            for (News item : content.news()) {
                if (siteLocale.equals(item.locale())) {
                    locNews.add(item);
                }
            }
            add(new ListView<News>("newsItem", locNews) {
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
        } else {
            setVisible(false);
        }
    }
}
