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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.pyx4j.entity.server.pojo.IPojo;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class AdvancedSearchCriteriaInputPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static enum NumNames {
        zero, One, Two, Three, Four, Five, Six, Seven, Eight;
    }

    public AdvancedSearchCriteriaInputPanel(String id, CompoundPropertyModel<IPojo<PropertySearchCriteria>> model) {
        super(id, model);

        // add Error Message panel
        add(new FeedbackPanel("form_messages"));

        // add searchType radio selectors
        RadioGroup<PropertySearchCriteria.SearchType> searchTypeRadio = new RadioGroup<PropertySearchCriteria.SearchType>("searchType");
        searchTypeRadio.add(new Radio<PropertySearchCriteria.SearchType>("searchByCity", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.city)));
        searchTypeRadio.add(new Radio<PropertySearchCriteria.SearchType>("searchByProx", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.proximity)));
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
        String selProv = model.getObject().getEntityValue().province().getValue();
        String selCity = null;
        if (selProv != null) {
            cities = provCityMap.get(selProv);
            selCity = model.getObject().getEntityValue().city().getValue();
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
        if (selProv != null && selCity != null) {
            jsCityList += "var selCity = '" + StringEscapeUtils.escapeJavaScript(selCity) + "';\n";
        }
        add(new Label("jsCityList", jsCityList).setEscapeModelStrings(false));

        // add location input
        TextField<String> lctnInput = new TextField<String>("location");
        add(lctnInput);

        // add distance input
        TextField<Integer> distInput = new TextField<Integer>("distance");
        distInput.add(new MinimumValidator<Integer>(1));
        add(distInput);

        IChoiceRenderer<Integer> intRenderer = new IChoiceRenderer<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getDisplayValue(Integer param) {
                return param == null ? "Any" : NumNames.values()[param].name();
            }

            @Override
            public String getIdValue(Integer param, int paramInt) {
                return String.valueOf(paramInt);
            }

        };

        // add common fields
        // bedrooms
        add(new DropDownChoice<Integer>("minBeds", Arrays.asList(new Integer[] { null, 1, 2, 3, 4, 5 }), intRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }

        });
        add(new DropDownChoice<Integer>("maxBeds", Arrays.asList(new Integer[] { null, 1, 2, 3, 4, 5 }), intRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        // bathrooms
        add(new DropDownChoice<Integer>("minBath", Arrays.asList(new Integer[] { null, 1, 2, 3 }), intRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        add(new DropDownChoice<Integer>("maxBath", Arrays.asList(new Integer[] { null, 1, 2, 3 }), intRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final Object selected) {
                return "";
            }
        });
        // price
        add(new TextField<Integer>("minPrice").add(new MinimumValidator<Integer>(100)));
        add(new TextField<Integer>("maxPrice").add(new MinimumValidator<Integer>(100)));
        // amenities
        //add(new CheckBoxMultipleChoice<PropertySearchCriteria.AmenitySet>("amenities", Arrays.asList(PropertySearchCriteria.AmenitySet.values())));
    }
}
