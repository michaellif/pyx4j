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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class QuickSearchCriteriaInputPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(QuickSearchCriteriaInputPanel.class);

    public QuickSearchCriteriaInputPanel(String id, CompoundIEntityModel<PropertySearchCriteria> model) {
        super(id, model);

        PropertySearchCriteria criteria = model.getObject().getEntityValue();
        // add Province drop-down
        final Map<String, List<String>> provCityMap = ((PMSiteWebRequest) getRequest()).getContentManager().getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        Component provChoice;
        if (provinces.size() == 1) {
            model.getObject().getEntityValue().province().setValue(provinces.get(0));
            provChoice = new WicketUtils.DropDownList<String>("province", provinces, false, false);
            provChoice.add(AttributeModifier.replace("disabled", "true"));
        } else {
            Collections.sort(provinces);
            provChoice = new WicketUtils.DropDownList<String>("province", model.bind(criteria.province()), provinces, false, i18n.tr("Select Province"));
            provChoice.add(AttributeModifier.replace("onChange",
                    "setSelectionOptions('citySelect', provCity[this.options[this.selectedIndex].text], '" + i18n.tr("Select City") + "')"));
        }
        add(provChoice);

        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getEntityValue().province().getValue();
        boolean singleCity = false;
        if (selProv != null && selProv.length() > 0) {
            cities = provCityMap.get(selProv);
            Collections.sort(cities);
            singleCity = (cities.size() == 1);
        } else {
            cities = Arrays.asList("- Select Province -");
        }
        if (singleCity) {
            model.getObject().getEntityValue().city().setValue(cities.get(0));
            Component cityChoice = new WicketUtils.DropDownList<String>("city", cities, false, false);
            cityChoice.add(AttributeModifier.replace("disabled", "true"));
            add(cityChoice);
        } else {
            add(new WicketUtils.DropDownList<String>("city", model.bind(criteria.city()), cities, false, i18n.tr("Select City")));

            // add JS city list
            String jsCityList =
            // set City for selected province
            "$(function() {\n" + "var sel = document.getElementById('provSelect');\n" + "if (! sel) return;\n"
                    + "setSelectionOptions('citySelect', provCity[sel.options[sel.selectedIndex].text], '" + i18n.tr("Select City") + "', selCity);\n"
                    + "});\n\n" + "var provCity = {};\n";
            for (String _prov : provCityMap.keySet()) {
                String _list = "";
                List<String> _cityList = new ArrayList<String>(provCityMap.get(_prov));
                Collections.sort(_cityList);
                for (String _city : _cityList) {
                    _list += ("".equals(_list) ? "" : ",") + "'" + StringEscapeUtils.escapeJavaScript(_city) + "'";
                }
                jsCityList += "provCity['" + StringEscapeUtils.escapeJavaScript(_prov) + "'] = [" + _list + "];\n";
            }
            String selCity = model.getObject().getEntityValue().city().getValue();
            jsCityList += "var selCity = '" + (selCity == null ? "" : StringEscapeUtils.escapeJavaScript(selCity)) + "';\n";
            add(new Label("jsCityList", jsCityList).setEscapeModelStrings(false));
        }
        // bedrooms
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BedroomRange>("bedsRange", model.bind(criteria.bedsRange()),
                Arrays.asList(PropertySearchCriteria.BedroomRange.values()), true, false));
        // price
        add(new WicketUtils.DropDownList<PropertySearchCriteria.PriceRange>("priceRange", model.bind(criteria.priceRange()),
                Arrays.asList(PropertySearchCriteria.PriceRange.values()), true, false));
    }
}
