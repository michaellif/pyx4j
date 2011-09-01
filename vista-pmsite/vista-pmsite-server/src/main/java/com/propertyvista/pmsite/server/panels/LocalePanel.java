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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.Locale;
import com.propertyvista.pmsite.server.PMSiteSession;

public class LocalePanel extends Panel {

    private static final long serialVersionUID = 1L;

    public LocalePanel(String id) {
        super(id);

        ListView<Locale> listView = new ListView<Locale>("langItem", ((PMSiteSession) getSession()).getContentManager().getSiteDescriptor().locales()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Locale> item) {

                final Locale locale = item.getModelObject();

                Link<Void> link = new Link<Void>("langSelector") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        ((PMSiteSession) getSession()).getContentManager().setLocale(locale);
                        setResponsePage(getPage().getPageClass(), getPage().getPageParameters());
                    }
                };
                item.add(link);
                link.add(new Label("caption", locale.lang().getValue().name()));

            }
        };
        add(listView);
    }
}