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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.LocalizedPageLink;
import com.propertyvista.shared.i18n.CompiledLocale;

public class LocalePanel extends Panel {

    private static final long serialVersionUID = 1L;

    public LocalePanel(String id) {
        super(id);

        add(new ListView<AvailableLocale>("langItem", new ArrayList<AvailableLocale>(((PMSiteWebRequest) getRequest()).getContentManager()
                .getAllAvailableLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<AvailableLocale> item) {

                AvailableLocale locale = item.getModelObject();
                CompiledLocale cl = locale.lang().getValue();
                String lang = cl.name();
                String label = cl.getNativeDisplayName();
                String title = cl.toString();
                LocalizedPageLink link = new LocalizedPageLink("langLink", getPage().getClass(), getPage().getPageParameters(), lang);
                link.setText(label);
                link.add(AttributeModifier.replace("title", title));
                item.add(link);
            }
        });
    }
}