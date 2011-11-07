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
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;

public class PromoPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public PromoPanel(String id) {
        super(id);

        add(new Label("promoTitle", ((PMSiteWebRequest) getRequest()).getContentManager().getSiteTitles(((PMSiteWebRequest) getRequest()).getSiteLocale())
                .residentPortalPromotions().getStringView()));

        add(new ListView<PromoDataModel>("promoItem", ((PMSiteWebRequest) getRequest()).getContentManager().getPromotions()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<PromoDataModel> item) {
                PromoDataModel promo = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add("propId", promo.getPropId());
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("propLink", AptDetailsPage.class, params);
                link.add(new SimpleImage("picture", promo.getImg()));
                item.add(link);

                item.add(new Label("address", promo.getAddress()));
            }
        });
    }

}
