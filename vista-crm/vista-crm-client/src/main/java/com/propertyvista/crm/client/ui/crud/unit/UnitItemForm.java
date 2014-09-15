/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemForm.class);

    public UnitItemForm(IForm<AptUnitItem> view) {
        super(AptUnitItem.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(120);
        formPanel.append(Location.Dual, proto().description()).decorate();
        formPanel.append(Location.Dual, proto().conditionNotes()).decorate();

        formPanel.h1(i18n.tr("Details"));

        formPanel.append(Location.Left, proto().wallColor()).decorate().componentWidth(180);

        formPanel.h3(i18n.tr("Flooring"));
        formPanel.append(Location.Left, proto().flooringType()).decorate().componentWidth(120).customLabel(i18n.tr("Type"));
        formPanel.append(Location.Left, proto().flooringInstallDate()).decorate().componentWidth(120).customLabel(i18n.tr("Installation Date"));
        formPanel.append(Location.Right, proto().flooringValue()).decorate().componentWidth(120).customLabel(i18n.tr("Value"));

        formPanel.h3(i18n.tr("Counter Top"));
        formPanel.append(Location.Left, proto().counterTopType()).decorate().componentWidth(120).customLabel(i18n.tr("Type"));
        formPanel.append(Location.Left, proto().counterTopInstallDate()).decorate().componentWidth(120).customLabel(i18n.tr("Installation Date"));
        formPanel.append(Location.Right, proto().counterTopValue()).decorate().componentWidth(120).customLabel(i18n.tr("Value"));

        formPanel.h3(i18n.tr("Cabinets"));
        formPanel.append(Location.Left, proto().cabinetsType()).decorate().componentWidth(120).customLabel(i18n.tr("Type"));
        formPanel.append(Location.Left, proto().cabinetsInstallDate()).decorate().componentWidth(120).customLabel(i18n.tr("Installation Date"));
        formPanel.append(Location.Right, proto().cabinetsValue()).decorate().componentWidth(120).customLabel(i18n.tr("Value"));

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("Unit Item")));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().flooringInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().counterTopInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().cabinetsInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
    }
}
