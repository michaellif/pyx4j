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
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;

public class PetDataEditor extends CForm<Pet> {

    private static final I18n i18n = I18n.get(PetDataEditor.class);

    public PetDataEditor() {
        super(Pet.class);
    }

    public PetDataEditor(IEditableComponentFactory factory) {
        super(Pet.class, factory);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h3(i18n.tr("Pet Data"));

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().color()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().breed()).decorate().componentWidth(150);

        formPanel.append(Location.Right, proto().weight()).decorate().componentWidth(60);
        formPanel.append(Location.Right, proto().weightUnit()).decorate().componentWidth(60);
        formPanel.append(Location.Right, proto().birthDate()).decorate().componentWidth(120);

        removeMandatory();
        return formPanel;
    }

    public void removeMandatory() {
        get(proto().name()).setMandatory(false);
        get(proto().color()).setMandatory(false);
        get(proto().breed()).setMandatory(false);
        get(proto().weight()).setMandatory(false);
        get(proto().weightUnit()).setMandatory(false);
        get(proto().birthDate()).setMandatory(false);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
    }

    @Override
    public void generateMockData() {
        get(proto().name()).setMockValue("Sweety");
        get(proto().color()).setMockValue("Pink");
        get(proto().breed()).setMockValue("Bull Terrier");
        get(proto().weight()).setMockValue(222);
        get(proto().weightUnit()).setMockValue(WeightUnit.kg);
        get(proto().birthDate()).setMockValue(new LogicalDate(1, 1, 1));
    }
}
