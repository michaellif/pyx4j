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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.LocalizedPageLink;

public class LocalePanel extends Panel {

    private static final long serialVersionUID = 1L;

    public LocalePanel(String id) {
        super(id);

        ListView<AvailableLocale> listView = new ListView<AvailableLocale>("langItem", new ArrayList<AvailableLocale>(((PMSiteWebRequest) getRequest())
                .getContentManager().getAllAvailableLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<AvailableLocale> item) {

                final AvailableLocale locale = item.getModelObject();
                final String lang = locale.lang().getValue().name();

                LocalizedPageLink link = new LocalizedPageLink("langSelector", getPage().getClass(), getPage().getPageParameters(), lang);
                item.add(link.add(new Label("caption", lang)));
            }
        };
        add(listView);
    }
}