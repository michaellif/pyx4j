/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 29, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.propertyvista.pmsite.server.model.SearchCriteriaModel;

public class AdvancedSearchCriteriaInputPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, CompoundPropertyModel<SearchCriteriaModel> model) {
        super(id, model);

        // add Error Message panel
        add(new FeedbackPanel("form_messages"));

        // add searchType radio selectors
        RadioGroup<SearchCriteriaModel.SearchType> searchTypeRadio = new RadioGroup<SearchCriteriaModel.SearchType>("searchType");
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByType", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.City)));
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByProx", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.Proximity)));
        add(searchTypeRadio.setRequired(true));
        // add Province drop-down
        final Map<String, List<String>> provCityMap = model.getObject().getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        add(new DropDownChoice<String>("province", provinces) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newProv) {
                // get city component
                DropDownChoice<String> city = (DropDownChoice<String>) getParent().get("city");
                if (city != null)
                    city.setChoices(provCityMap.get(newProv));
            }
        });
        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getProvince();
        if (selProv != null) {
            cities = provCityMap.get(selProv);
        } else {
            cities = Arrays.asList("- Select Province -");
        }
        add(new DropDownChoice<String>("city", cities));
        // add location input
        add(new TextField<String>("location"));
        // add distance input
        add(new TextField<Integer>("distance").add(new MinimumValidator<Integer>(1)));
        // add common fields
        // bedrooms
        add(new DropDownChoice<SearchCriteriaModel.BedroomChoice>("bedsMin", Arrays.asList(SearchCriteriaModel.BedroomChoice.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        add(new DropDownChoice<SearchCriteriaModel.BedroomChoice>("bedsMax", Arrays.asList(SearchCriteriaModel.BedroomChoice.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        // bathrooms
        add(new DropDownChoice<SearchCriteriaModel.BathroomChoice>("bathsMin", Arrays.asList(SearchCriteriaModel.BathroomChoice.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        add(new DropDownChoice<SearchCriteriaModel.BathroomChoice>("bathsMax", Arrays.asList(SearchCriteriaModel.BathroomChoice.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        // price
        add(new TextField<Integer>("priceMin").add(new MinimumValidator<Integer>(100)));
        add(new TextField<Integer>("priceMax").add(new MinimumValidator<Integer>(100)));
        // amenities
        add(new CheckBoxMultipleChoice<SearchCriteriaModel.AmenitySet>("amenities", Arrays.asList(SearchCriteriaModel.AmenitySet.values())));
    }

}
