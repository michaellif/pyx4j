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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadio;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria;

public class AdvancedSearchCriteriaInputPanel extends Panel {

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaInputPanel.class);

    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, final CompoundIEntityModel<PropertySearchCriteria> model) {
        super(id, model);

        PropertySearchCriteria criteria = model.getObject().getEntityValue();

        // add Province drop-down
        final Map<String, List<String>> provCityMap = ((PMSiteWebRequest) getRequest()).getContentManager().getProvinceCityMap();
//        new HashMap<String, List<String>>();
//        provCityMap.put("Ontario", Arrays.asList("Toronto"));
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        Collections.sort(provinces);
        boolean singleProv = (provinces.size() == 1);
        Component provChoice;
        if (singleProv) {
            model.getObject().getEntityValue().province().setValue(provinces.get(0));
            provChoice = new WicketUtils.DropDownList<String>("province", provinces, false, false);
            provChoice.add(AttributeModifier.replace("disabled", "true"));
        } else {
            provChoice = new WicketUtils.DropDownList<String>("province", model.bind(criteria.province()), provinces, false, i18n.tr("Select Province"));
            provChoice.add(AttributeModifier.replace("onChange",
                    "setSelectionOptions('citySelect', provCity[this.options[this.selectedIndex].text], '" + i18n.tr("Select City") + "')"));
        }
        add(provChoice);

        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getEntityValue().province().getValue();
        boolean singleCity = false;
        if (selProv != null) {
            cities = provCityMap.get(selProv);
            Collections.sort(cities);
            singleCity = (cities.size() == 1);
        } else {
            cities = Arrays.asList(i18n.tr("Select Province"));
        }
        Component cityChoice;
        if (singleCity && singleProv) {
            model.getObject().getEntityValue().city().setValue(cities.get(0));
            cityChoice = new WicketUtils.DropDownList<String>("city", cities, false, false);
            cityChoice.add(AttributeModifier.replace("disabled", "true"));
        } else {
            cityChoice = new WicketUtils.DropDownList<String>("city", model.bind(criteria.city()), cities, false, i18n.tr("Select City"));
            if (!singleProv) {
                // add JS city list
                String jsCityList =
                // set City for selected province
                "$(function() {\n" + "var sel = document.getElementById('provSelect');\n" + "if (! sel) return;\n"
                        + "setSelectionOptions('citySelect', provCity[sel.options[sel.selectedIndex].text], '" + i18n.tr("Select City") + "', selCity);\n"
                        + "});\n\n" + "var provCity = {};\n";
                for (String _prov : provCityMap.keySet()) {
                    String _list = "";
                    List<String> _cityList = provCityMap.get(_prov);
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
        }
        add(cityChoice);
        // add searchType radio selectors
        RadioGroup<PropertySearchCriteria.SearchType> searchTypeRadio = new RadioGroup<PropertySearchCriteria.SearchType>("searchType", model.bind(criteria
                .searchType()));
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByCity", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.city)));
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByProx", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.proximity)));
        add(searchTypeRadio.setRequired(true).setVisible(!(singleCity && singleProv)));

        // add location input
        add(new TextField<String>("location", model.bind(criteria.location())).setVisible(!(singleCity && singleProv)));
        add(new HiddenField<GeoPoint>("geolocation", model.bind(criteria.geolocation())) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                if (!GeoPoint.class.isAssignableFrom(type)) {
                    return super.getConverter(type);
                }

                return (IConverter<C>) new IConverter<GeoPoint>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public GeoPoint convertToObject(String value, Locale locale) {
                        return GeoPoint.valueOf(value);
                    }

                    @Override
                    public String convertToString(GeoPoint value, Locale locale) {
                        return value.toString();
                    }

                };
            }
        });

        // add distance input
        add(new TextField<Integer>("distance").setVisible(!(singleCity && singleProv)));

        // add common fields
        // bedrooms
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BedroomChoice>("minBeds", model.bind(criteria.minBeds()),
                Arrays.asList(PropertySearchCriteria.BedroomChoice.values()), true, false));
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BedroomChoice>("maxBeds", model.bind(criteria.maxBeds()),
                Arrays.asList(PropertySearchCriteria.BedroomChoice.values()), true, false));
        // bathrooms
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BathroomChoice>("minBaths", model.bind(criteria.minBaths()),
                Arrays.asList(PropertySearchCriteria.BathroomChoice.values()), true, false));
        add(new WicketUtils.DropDownList<PropertySearchCriteria.BathroomChoice>("maxBaths", model.bind(criteria.maxBaths()),
                Arrays.asList(PropertySearchCriteria.BathroomChoice.values()), true, false));
        // price
        add(new TextField<Integer>("minPrice", model.bind(criteria.minPrice())));
        add(new TextField<Integer>("maxPrice", model.bind(criteria.maxPrice())));
        // error panel
        add(new FormErrorPanel("inputErrors", "minPrice", "maxPrice"));

        // amenities
        List<Building> buildings = PropertyFinder.getPropertyList(null);
        boolean singleBuilding = singleProv && singleCity && buildings != null && buildings.size() == 1;
        CheckBoxMultipleChoice<BuildingAmenity.Type> checkBoxMultipleChoice = new CheckBoxMultipleChoice<BuildingAmenity.Type>("amenities", model.bind(criteria
                .amenities()), Arrays.asList(PropertySearchCriteria.AmenityChoice));
        add(checkBoxMultipleChoice.setVisible(!singleBuilding));
    }
}
