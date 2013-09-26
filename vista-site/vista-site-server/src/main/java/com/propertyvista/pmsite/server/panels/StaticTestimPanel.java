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
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.pmsite.server.PMSiteWebRequest;

public class StaticTestimPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public StaticTestimPanel(String id) {
        super(id);

        PMSiteWebRequest request = (PMSiteWebRequest) getRequest();
        add(new ListView<Testimonial>("testimItem", request.getContentManager().getTestimonials(request.getSiteLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Testimonial> item) {
                Testimonial testim = item.getModelObject();
                item.add(new Label("quote", testim.content().getStringView()).add(AttributeModifier.replace("id", "item" + testim.getPrimaryKey().asLong())));
                item.add(new Label("name", testim.author().getStringView()));
            }
        });
    }
}
