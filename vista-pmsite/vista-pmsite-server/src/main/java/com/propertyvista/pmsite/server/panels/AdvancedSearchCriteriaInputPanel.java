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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.pyx4j.entity.server.pojo.IPojo;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadio;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class AdvancedSearchCriteriaInputPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, final CompoundPropertyModel<IPojo<PropertySearchCriteria>> model) {
        super(id, model);

        // add searchType radio selectors
        RadioGroup<PropertySearchCriteria.SearchType> searchTypeRadio = new RadioGroup<PropertySearchCriteria.SearchType>("searchType");
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByCity", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.city)));
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByProx", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.proximity)));
        add(searchTypeRadio.setRequired(true));

        // add Province drop-down
        final Map<String, List<String>> provCityMap = ((PMSiteWebRequest) getRequest()).getContentManager().getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        DropDownChoice<String> provChoice = new WicketUtils.DropDownList<String>("province", provinces, false, true);
        provChoice.add(AttributeModifier
                .replace("onChange", "setSelectionOptions('citySelect', provCity[this.options[this.selectedIndex].text], 'Choose One')"));
        add(provChoice);

        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getEntityValue().province().getValue();
        if (selProv != null) {
            cities = provCityMap.get(selProv);
        } else {
            cities = Arrays.asList("- Select Province -");
        }
        add(new WicketUtils.DropDownList<String>("city", cities, false, true));

        // add JS city list
        String jsCityList = "\nvar provCity = {};\n";
        for (String _prov : provCityMap.keySet()) {
            String _list = "";
            for (String _city : provCityMap.get(_prov)) {
                _list += ("".equals(_list) ? "" : ",") + "'" + StringEscapeUtils.escapeJavaScript(_city) + "'";
            }
            jsCityList += "provCity['" + StringEscapeUtils.escapeJavaScript(_prov) + "'] = [" + _list + "];\n";
        }
        String selCity = model.getObject().getEntityValue().city().getValue();
        jsCityList += "var selCity = '" + (selCity == null ? "" : StringEscapeUtils.escapeJavaScript(selCity)) + "';\n";
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
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BedroomChoice>("minBeds", Arrays.asList(PropertySearchCriteria.BedroomChoice.values()), true,
                false));
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BedroomChoice>("maxBeds", Arrays.asList(PropertySearchCriteria.BedroomChoice.values()), true,
                false));
        // bathrooms
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BathroomChoice>("minBaths", Arrays.asList(PropertySearchCriteria.BathroomChoice.values()),
                true, false));
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BathroomChoice>("maxBaths", Arrays.asList(PropertySearchCriteria.BathroomChoice.values()),
                true, false));
        // price
        add(new TextField<Integer>("minPrice").add(new MinimumValidator<Integer>(100)));
        add(new TextField<Integer>("maxPrice").add(new MinimumValidator<Integer>(100)));

        // amenities
        CheckBoxMultipleChoice<PropertySearchCriteria.AmenityType> checkBoxMultipleChoice = new CheckBoxMultipleChoice<PropertySearchCriteria.AmenityType>(
                "amenities", Arrays.asList(PropertySearchCriteria.AmenityType.values()));
        add(checkBoxMultipleChoice);
    }
}
