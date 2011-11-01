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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.pyx4j.entity.server.pojo.IPojo;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class QuickSearchCriteriaInputPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public QuickSearchCriteriaInputPanel(String id, CompoundPropertyModel<IPojo<PropertySearchCriteria>> model) {
        super(id, model);

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

        // bedrooms
        add(new DropDownChoice<PropertySearchCriteria.BedroomRange>("bedsRange", Arrays.asList(PropertySearchCriteria.BedroomRange.values())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final String selected) {
                return "";
            }
        });

        IChoiceRenderer<PropertySearchCriteria.PriceRange> priceRangeRenderer = new IChoiceRenderer<PropertySearchCriteria.PriceRange>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getDisplayValue(PropertySearchCriteria.PriceRange paramT) {
                return paramT.toString();
            }

            @Override
            public String getIdValue(PropertySearchCriteria.PriceRange paramT, int paramInt) {
                return String.valueOf(paramInt);
            }

        };

        DropDownChoice<PropertySearchCriteria.PriceRange> priceRange = new DropDownChoice<PropertySearchCriteria.PriceRange>("priceRange",
                Arrays.asList(PropertySearchCriteria.PriceRange.values()), priceRangeRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getDefaultChoice(final String selected) {
                return "";
            }

        };
        add(priceRange);

    }
}
