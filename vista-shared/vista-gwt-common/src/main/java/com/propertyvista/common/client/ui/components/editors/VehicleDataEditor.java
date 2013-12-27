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
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

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
        panel.setH3(++row, 0, 2, i18n.tr("Vehicle Data"));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().make()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().model()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().color()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().year()), 5).build());

        row = 0; // skip header
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().plateNumber()), 10).build());
        CComponent<Country> country;
        panel.setWidget(++row, 1, new FormDecoratorBuilder(country = (CComponent<Country>) inject(proto().country()), 13).build());
        CComponent<Province> province;
        panel.setWidget(++row, 1, new FormDecoratorBuilder(province = (CComponent<Province>) inject(proto().province()), 17).build());

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
    public void addValidations() {
        super.addValidations();
        if (ApplicationMode.isDevelopment()) {
            this.addDevShortcutHandler(new DevShortcutHandler() {
                @Override
                public void onDevShortcut(DevShortcutEvent event) {
                    if (event.getKeyCode() == 'Q') {
                        event.consume();
                        devGenerateVehicle();
                    }
                }
            });
        }
    }

    private void devGenerateVehicle() {
        get(proto().make()).setValue("BMW");
        get(proto().model()).setValue("i666");
        get(proto().color()).setValue("Rose");
        get(proto().year()).setValue(new LogicalDate());
        get(proto().plateNumber()).setValue("LastTimeDrive");
        ((CComboBox<?>) get(proto().province())).setValueByString("Ontario");

    }
}
