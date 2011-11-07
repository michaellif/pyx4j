/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 9, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.pmsite.server.PMSiteWebRequest;

public class TestimPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public TestimPanel(String id) {
        super(id);
        add(new ListView<Testimonial>("testimItem", ((PMSiteWebRequest) getRequest()).getContentManager().getTestimonials(
                ((PMSiteWebRequest) getRequest()).getSiteLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Testimonial> item) {
                Testimonial testim = item.getModelObject();

                //TODO cut it nicely
                String content = testim.content().getStringView();
                if (content.length() >= 100) {
                    content = content.substring(0, 100) + " ...";
                }

                item.add(new Label("quote", content));
                item.add(new Label("more", "&raquo;").setEscapeModelStrings(false));

                item.add(new Label("name", testim.author().getStringView()));
            }
        });
    }

}
