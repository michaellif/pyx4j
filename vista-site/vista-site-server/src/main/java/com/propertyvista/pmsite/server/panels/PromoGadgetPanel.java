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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.PromoDataModel;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.LandingPage;

public class PromoGadgetPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(PromoGadgetPanel.class);

    private final static String ParamNamePromo = "promo";

    public PromoGadgetPanel(String id) {
        super(id);

    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        add(new Label("promoTitle", ((PMSiteWebRequest) getRequest()).getContentManager().getSiteTitles(((PMSiteWebRequest) getRequest()).getSiteLocale())
                .residentPortalPromotions().getStringView()));

        // add City-Province drop-down
        final Map<String, List<String>> provCityMap = ((PMSiteWebRequest) getRequest()).getContentManager().getProvinceCityMap(true);
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        Collections.sort(provinces);
        final List<String> cities = new ArrayList<String>();
        final List<String> cityOpts = new ArrayList<String>();
        for (String prov : provinces) {
            List<String> provCities = provCityMap.get(prov);
            Collections.sort(provCities);
            for (String city : provCities) {
                cities.add(city);
                cityOpts.add(prov + " - " + city);
            }
        }
        String promoCity = getPage().getPageParameters().get(ParamNamePromo).toString();
        // if we have not been given city, select randomly from list
        if (cities.size() > 0 && (promoCity == null || cities.indexOf(promoCity) == -1)) {
            int cityIdx = Math.round((float) Math.random() * (cities.size() - 1));
            promoCity = cities.get(cityIdx);
        }
        List<PromoDataModel> promoList = ((PMSiteWebRequest) getRequest()).getContentManager().getPromotions(promoCity);
        // if no promos found, reset promoCity and grab random promo list
        if (promoList == null || promoList.size() == 0) {
            promoCity = null;
            promoList = ((PMSiteWebRequest) getRequest()).getContentManager().getPromotions(null);
        }
        final Component cityChoice;
        if (cities.size() == 1) {
            cityChoice = new WicketUtils.DropDownList<String>("promoCity", cityOpts, false, false);
            cityChoice.add(AttributeModifier.replace("disabled", "true"));
        } else {
            String selectedCity = "";
            try {
                selectedCity = cityOpts.get(cities.indexOf(promoCity));
            } catch (Exception ignore) {
            }
            cityChoice = new WicketUtils.DropDownList<String>("promoCity", Model.of(selectedCity), cityOpts, true, i18n.tr("Select City"));
            cityChoice.add(AttributeModifier.replace("onChange", "this.form.submit()"));
        }

        final StatelessForm<String> form = new StatelessForm<String>("citySelectionForm") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                PageParameters pp = null;
                try {
                    String city = cities.get(cityOpts.indexOf(cityChoice.getDefaultModelObjectAsString()));
                    pp = new PageParameters().set(ParamNamePromo, city);
                } catch (Exception ignore) {
                }
                setResponsePage(LandingPage.class, pp);
            }
        };
        form.add(cityChoice);
        add(form);

        add(new ListView<PromoDataModel>("promoItem", promoList) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<PromoDataModel> item) {
                PromoDataModel promo = item.getModelObject();
                PageParameters params = new PageParameters();
                params.add(PMSiteApplication.ParamNameBuilding, promo.getPropCode());
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("propLink", AptDetailsPage.class, params);
                link.add(new SimpleImage("picture", promo.getImg()));
                link.add(new Label("address", promo.getAddress()));
                item.add(link);
            }
        });
    }
}
