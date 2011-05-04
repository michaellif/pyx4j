/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.CompareHelper;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntitySuggestBox;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IAcceptText;

import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.Province;

public class ProvinceContryFilters {

    private static final List<String> countryProvinceRequired = new Vector<String>();

    static {
        countryProvinceRequired.add("canada");
        countryProvinceRequired.add("united states");
        countryProvinceRequired.add("us");
        countryProvinceRequired.add("usa");
    }

    /**
     * Add proper dependencies between shown list of countries and provinces/states
     */
    public static void attachFilters(CEditableComponent<Province, ?> province, final CEditableComponent<Country, ?> country,
            OptionsFilter<Province> provinceFilter) {
        if ((!(province instanceof CEntityComboBox)) || (!(country instanceof IAcceptText))) {
            return;
        }
        // Filter Province by Country
        @SuppressWarnings("unchecked")
        final CEntityComboBox<Province> provinceCombo = (CEntityComboBox<Province>) province;
        final boolean provinceComboIsMandatoryInitialy = provinceCombo.isMandatory();
        provinceCombo.setUseNamesComparison(true);
        provinceCombo.setOptionsComparator(new Comparator<Province>() {

            @Override
            public int compare(Province o1, Province o2) {
                return CompareHelper.compareTo(o1.name().getValue(), o2.name().getValue());
            }
        });

        provinceCombo.setOptionsFilter(provinceFilter);

        country.addValueChangeHandler(new ValueChangeHandler<Country>() {

            @Override
            public void onValueChange(ValueChangeEvent<Country> event) {
                if (event.getValue() != null) {
                    if ((provinceCombo.getValue() != null)
                            && (!EqualsHelper.equals(event.getValue().name().getValue(), provinceCombo.getValue().country().name().getValue()))) {
                        provinceCombo.setValue(null);
                    }
                    if (provinceComboIsMandatoryInitialy) {
                        provinceCombo.setEnabled(countryProvinceRequired.contains((event.getValue().name().getValue().toLowerCase())));
                        provinceCombo.setVisited(true);
                    }
                }
                provinceCombo.resetOptions();
                provinceCombo.retriveOptions(null);
            }
        });
        provinceCombo.addValueChangeHandler(new ValueChangeHandler<Province>() {

            @Override
            public void onValueChange(ValueChangeEvent<Province> event) {
                if (!provinceCombo.isValueEmpty()) {
                    if ((country instanceof CEntitySuggestBox) && (((CEntitySuggestBox<?>) country).isOptionsLoaded())) {
                        ((CEntitySuggestBox<?>) country).setValueByString(provinceCombo.getValue().country().name().getValue());
                        country.revalidate();
                    } else if ((country instanceof CEntityComboBox) && (((CEntityComboBox<?>) country).isOptionsLoaded())) {
                        ((CEntityComboBox<?>) country).setValueByString(provinceCombo.getValue().country().name().getValue());
                        country.revalidate();
                    }
                }
            }
        });

    }
}
