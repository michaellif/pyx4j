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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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

import com.propertyvista.pmsite.server.model.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;

public class AdvancedSearchCriteriaInputPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, CompoundPropertyModel<SearchCriteriaModel> model) {
        super(id, model);

        // add Error Message panel
        add(new FeedbackPanel("form_messages"));

        // add searchType radio selectors
        RadioGroup<SearchCriteriaModel.SearchType> searchTypeRadio = new RadioGroup<SearchCriteriaModel.SearchType>("searchType") {
            // TODO replace this BS (server round trip) with JS (onclick handler)
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(Object newType) {
                // get components
                @SuppressWarnings("unchecked")
                DropDownChoice<String> city = (DropDownChoice<String>) getParent().get("city");
                @SuppressWarnings("unchecked")
                DropDownChoice<String> prov = (DropDownChoice<String>) getParent().get("province");
                @SuppressWarnings("unchecked")
                TextField<String> lctn = (TextField<String>) getParent().get("location");
                @SuppressWarnings("unchecked")
                TextField<Integer> dist = (TextField<Integer>) getParent().get("distance");

                // TODO replace this BS with JS
                if (newType == SearchCriteriaModel.SearchType.City) {
                    if (lctn != null) {
                        lctn.add(new AttributeClassModifier(null, "input_disabled"));
                        lctn.add(new SimpleAttributeModifier("disabled", "true"));
                    }
                    if (dist != null) {
                        dist.add(new AttributeClassModifier(null, "input_disabled"));
                        dist.add(new SimpleAttributeModifier("disabled", "true"));
                    }
                    if (city != null) {
                        city.add(new AttributeClassModifier("input_disabled", null));
                        city.add(new AttributeModifier("disabled", new Model<String>(AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE)));
                    }
                    if (prov != null) {
                        prov.add(new AttributeClassModifier("input_disabled", null));
                        prov.add(new AttributeModifier("disabled", new Model<String>(AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE)));
                    }
                } else {
                    if (lctn != null) {
                        lctn.add(new AttributeClassModifier("input_disabled", null));
                        lctn.add(new AttributeModifier("disabled", new Model<String>(AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE)));
                    }
                    if (dist != null) {
                        dist.add(new AttributeClassModifier("input_disabled", null));
                        dist.add(new AttributeModifier("disabled", new Model<String>(AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE)));
                    }
                    if (city != null) {
                        city.add(new AttributeClassModifier(null, "input_disabled"));
                        city.add(new SimpleAttributeModifier("disabled", "true"));
                    }
                    if (prov != null) {
                        prov.add(new AttributeClassModifier(null, "input_disabled"));
                        prov.add(new SimpleAttributeModifier("disabled", "true"));
                    }
                }
            }
        };
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByType", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.City)));
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByProx", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.Proximity)));
        add(searchTypeRadio.setRequired(true));
        // add Province drop-down
        final Map<String, List<String>> provCityMap = model.getObject().getProvinceCityMap();
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
        if (model.getObject().getSearchType() == SearchCriteriaModel.SearchType.Proximity) {
            provChoice.add(new SimpleAttributeModifier("disabled", "true"));
        }
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
        // add location input
        TextField<String> lctnInput = new TextField<String>("location");
        add(lctnInput);
        // add distance input
        TextField<Integer> distInput = new TextField<Integer>("distance");
        distInput.add(new MinimumValidator<Integer>(1));
        add(distInput);
        if (model.getObject().getSearchType() == SearchCriteriaModel.SearchType.City) {
            lctnInput.add(new SimpleAttributeModifier("disabled", "true"));
            lctnInput.add(new AttributeClassModifier(null, "input_disabled"));
            distInput.add(new SimpleAttributeModifier("disabled", "true"));
            distInput.add(new AttributeClassModifier(null, "input_disabled"));
        } else if (model.getObject().getSearchType() == SearchCriteriaModel.SearchType.Proximity) {
            cityChoice.add(new SimpleAttributeModifier("disabled", "true"));
            cityChoice.add(new AttributeClassModifier(null, "input_disabled"));
            provChoice.add(new SimpleAttributeModifier("disabled", "true"));
            provChoice.add(new AttributeClassModifier(null, "input_disabled"));
        }
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
