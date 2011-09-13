/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;

public class QuickSearchCriteriaInputPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public QuickSearchCriteriaInputPanel(String id, CompoundPropertyModel<SearchCriteriaModel> model) {
        super(id, model);

        // add Province drop-down
        final Map<String, List<String>> provCityMap = PMSiteContentManager.getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        DropDownChoice<String> provChoice = new DropDownChoice<String>("province", provinces) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newProv) {
                // get city component
                @SuppressWarnings("unchecked")
                DropDownChoice<String> city = (DropDownChoice<String>) getParent().get("city");
                if (city != null)
                    city.setChoices(provCityMap.get(newProv));
            }
        };
        add(provChoice);
        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getProvince();
        if (selProv != null) {
            cities = provCityMap.get(selProv);
        } else {
            cities = Arrays.asList("- Select Province -");
        }
        DropDownChoice<String> cityChoice = new DropDownChoice<String>("city", cities);
        add(cityChoice);

        // bedrooms
        add(new DropDownChoice<SearchCriteriaModel.BedroomChoice>("bedrooms", Arrays.asList(SearchCriteriaModel.BedroomChoice.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });

        IChoiceRenderer<SearchCriteriaModel.PriceChoice> renderer = new IChoiceRenderer<SearchCriteriaModel.PriceChoice>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getDisplayValue(SearchCriteriaModel.PriceChoice paramT) {
                return paramT.toString();
            }

            @Override
            public String getIdValue(SearchCriteriaModel.PriceChoice paramT, int paramInt) {
                return String.valueOf(paramInt);
            }

        };

        DropDownChoice<SearchCriteriaModel.PriceChoice> priceChoice = new DropDownChoice<SearchCriteriaModel.PriceChoice>("priceRange",
                Arrays.asList(SearchCriteriaModel.PriceChoice.values()), renderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        };
        add(priceChoice);

    }
}
