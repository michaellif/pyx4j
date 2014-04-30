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

import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemForm.class);

    public UnitItemForm(IForm<AptUnitItem> view) {
        super(AptUnitItem.class, view);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().type()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();
        formPanel.append(Location.Left, proto().conditionNotes()).decorate();

        formPanel.h1(i18n.tr("Details"));
        formPanel.append(Location.Left, proto().wallColor()).decorate();
        formPanel.append(Location.Right, proto().counterTopInstallDate()).decorate();
        formPanel.append(Location.Left, proto().flooringType()).decorate();
        formPanel.append(Location.Right, proto().counterTopValue()).decorate();
        formPanel.append(Location.Left, proto().flooringInstallDate()).decorate();
        formPanel.append(Location.Right, proto().cabinetsType()).decorate();
        formPanel.append(Location.Left, proto().flooringValue()).decorate();
        formPanel.append(Location.Right, proto().cabinetsInstallDate()).decorate();
        formPanel.append(Location.Left, proto().counterTopType()).decorate();
        formPanel.append(Location.Right, proto().cabinetsValue()).decorate();

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
