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

import js.JSResources;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
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

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;

public class AdvancedSearchCriteriaInputPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, CompoundPropertyModel<SearchCriteriaModel> model) {
        super(id, model);

        getApplication().getSharedResources().putClassAlias(JSResources.class, "js");
//        add(new JavaScriptReference("jquery", JSResources.class, "jquery-1.6.3.min.js"));

        // add Error Message panel
        add(new FeedbackPanel("form_messages"));

        // add searchType radio selectors
        RadioGroup<SearchCriteriaModel.SearchType> searchTypeRadio = new RadioGroup<SearchCriteriaModel.SearchType>("searchType");
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByCity", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.City)));
        searchTypeRadio.add(new Radio<SearchCriteriaModel.SearchType>("searchByProx", new Model<SearchCriteriaModel.SearchType>(
                SearchCriteriaModel.SearchType.Proximity)));
        add(searchTypeRadio.setRequired(true));

        // add Province drop-down
        final Map<String, List<String>> provCityMap = PMSiteContentManager.getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        DropDownChoice<String> provChoice = new DropDownChoice<String>("province", provinces);
        provChoice.add(new SimpleAttributeModifier("onChange",
                "setSelectionOptions('citySelect', provCity[this.options[this.selectedIndex].text], 'Choose One')"));
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

        // add JS city list
        String jsCityList = "\nvar provCity = {};\n";
        for (String _prov : provCityMap.keySet()) {
            String _list = "";
            for (String _city : provCityMap.get(_prov)) {
                _list += ("".equals(_list) ? "" : ",") + "'" + StringEscapeUtils.escapeJavaScript(_city) + "'";
            }
            jsCityList += "provCity['" + StringEscapeUtils.escapeJavaScript(_prov) + "'] = [" + _list + "];\n";
        }
        add(new Label("jsCityList", jsCityList).setEscapeModelStrings(false));

        // add location input
        TextField<String> lctnInput = new TextField<String>("location");
        add(lctnInput);

        // add distance input
        TextField<Integer> distInput = new TextField<Integer>("distance");
        distInput.add(new MinimumValidator<Integer>(1));
        add(distInput);

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
