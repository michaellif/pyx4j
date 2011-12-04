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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.site.Testimonial;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;

public class TestimPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public static I18n i18n = I18n.get(TestimPanel.class);

    public TestimPanel(String id) {
        super(id);

        PMSiteWebRequest request = (PMSiteWebRequest) getRequest();
        PMSiteContentManager cm = request.getContentManager();
        final NavigationItem testimNav = cm.getSecondaryNavigItem("testimonials");

        Component moreTestim = null;
        String moreTestimId = "moreTestim";
        if (testimNav != null) {
            moreTestim = new PageLink(moreTestimId, testimNav.getDestination(), testimNav.getPageParameters());
            ((PageLink) moreTestim).setText(i18n.tr("More") + " &raquo;").setEscapeModelStrings(false);
        } else {
            moreTestim = new Label(moreTestimId, "").setRenderBodyOnly(true);
        }
        add(moreTestim);
        //<wicket:i18n>More</wicket:i18n> &raquo;</a>
        add(new ListView<Testimonial>("testimItem", cm.getTestimonials(request.getSiteLocale())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Testimonial> item) {
                Testimonial testim = item.getModelObject();

                String content = testim.content().getStringView();
                Component readMore = null;
                String readmoreId = "more";
                if (content.length() >= 100) {
                    content = content.substring(0, 100) + " ...";
                    if (testimNav != null) {
                        String itemAnchor = "item" + testim.getPrimaryKey().asLong();
                        readMore = new PageLink(readmoreId, testimNav.getDestination(), testimNav.getPageParameters());
                        ((PageLink) readMore).setText("&raquo;").setAnchor(itemAnchor).setEscapeModelStrings(false);
                    }
                }
                if (readMore == null) {
                    readMore = new Label(readmoreId, "").setRenderBodyOnly(true);
                }

                item.add(new Label("quote", content));
                item.add(readMore);

                item.add(new Label("name", testim.author().getStringView()));
            }
        });
    }

}
