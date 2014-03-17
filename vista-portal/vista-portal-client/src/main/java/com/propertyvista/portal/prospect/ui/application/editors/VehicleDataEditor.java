/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class VehicleDataEditor extends CEntityForm<Vehicle> {

    private static final I18n i18n = I18n.get(VehicleDataEditor.class);

    public VehicleDataEditor() {
        super(Vehicle.class);
    }

    public VehicleDataEditor(IEditableComponentFactory factory) {
        super(Vehicle.class, factory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setH3(++row, 0, 1, i18n.tr("Vehicle Data"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().make()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().model()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().color()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().year()), 60).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().plateNumber()), 120).build());
        CComponent<Province> province;
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(province = (CComponent<Province>) inject(proto().province()), 200).build());
        CComponent<Country> country;
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(country = (CComponent<Country>) inject(proto().country()), 150).build());

        ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
            @Override
            public boolean acceptOption(Province entity) {
                if (getValue() == null) {
                    return true;
                } else {
                    Country country = (Country) getValue().getMember(proto().country().getPath());
                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                }
            }
        });

        return panel;
    }

    @Override
    public void generateMockData() {
        get(proto().make()).setMockValue("Rolls Royce");
        get(proto().model()).setMockValue("Phantom");
        get(proto().color()).setMockValue("Black");
        get(proto().year()).setMockValue(new LogicalDate(108, 0, 0));
        get(proto().plateNumber()).setMockValue("777");
        get(proto().country()).setMockValueByString("Canada");
        get(proto().province()).setMockValueByString("Ontario");
    }

}
