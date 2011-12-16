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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadio;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class AdvancedSearchCriteriaInputPanel extends Panel {

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaInputPanel.class);

    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaInputPanel(String id, final CompoundIEntityModel<PropertySearchCriteria> model) {
        super(id, model);

        PropertySearchCriteria criteria = model.getObject().getEntityValue();
        // add searchType radio selectors
        RadioGroup<PropertySearchCriteria.SearchType> searchTypeRadio = new RadioGroup<PropertySearchCriteria.SearchType>("searchType", model.bind(criteria
                .searchType()));
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByCity", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.city)));
        searchTypeRadio.add(new SimpleRadio<PropertySearchCriteria.SearchType>("searchByProx", new Model<PropertySearchCriteria.SearchType>(
                PropertySearchCriteria.SearchType.proximity)));
        add(searchTypeRadio.setRequired(true));

        // add Province drop-down
        final Map<String, List<String>> provCityMap = ((PMSiteWebRequest) getRequest()).getContentManager().getProvinceCityMap();
        List<String> provinces = new ArrayList<String>(provCityMap.keySet());
        Collections.sort(provinces);
        DropDownChoice<String> provChoice = new WicketUtils.DropDownList<String>("province", model.bind(criteria.province()), provinces, false,
                i18n.tr("Select Province"));
        provChoice.add(AttributeModifier.replace("onChange",
                "setSelectionOptions('citySelect', provCity[this.options[this.selectedIndex].text], '" + i18n.tr("Select City") + "')"));
        add(provChoice);

        // add City drop-down
        List<String> cities;
        String selProv = model.getObject().getEntityValue().province().getValue();
        if (selProv != null) {
            cities = provCityMap.get(selProv);
            Collections.sort(cities);
        } else {
            cities = Arrays.asList("- Select Province -");
        }
        add(new WicketUtils.DropDownList<String>("city", model.bind(criteria.city()), cities, false, i18n.tr("Select City")));

        // add JS city list
        String jsCityList =
        // set City for selected province
        "$(function() {\n" + "var sel = document.getElementById('provSelect');\n" + "if (! sel) return;\n"
                + "setSelectionOptions('citySelect', provCity[sel.options[sel.selectedIndex].text], '" + i18n.tr("Select City") + "', selCity);\n" + "});\n\n"
                + "var provCity = {};\n";
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

        // add location input
        add(new TextField<String>("location", model.bind(criteria.location())));
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
        add(new TextField<Integer>("distance").add(new MinimumValidator<Integer>(1)));

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
        add(new TextField<Integer>("minPrice", model.bind(criteria.minPrice())).add(new MinimumValidator<Integer>(100)));
        add(new TextField<Integer>("maxPrice", model.bind(criteria.maxPrice())).add(new MinimumValidator<Integer>(100)));

        // amenities
        CheckBoxMultipleChoice<BuildingAmenity.Type> checkBoxMultipleChoice = new CheckBoxMultipleChoice<BuildingAmenity.Type>("amenities", model.bind(criteria
                .amenities()), Arrays.asList(PropertySearchCriteria.AmenityChoice));
        add(checkBoxMultipleChoice);
    }
}
