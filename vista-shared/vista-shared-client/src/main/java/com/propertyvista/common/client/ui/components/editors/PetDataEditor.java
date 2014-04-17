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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;

public class PetDataEditor extends CEntityForm<Pet> {

    private static final I18n i18n = I18n.get(PetDataEditor.class);

    public PetDataEditor() {
        super(Pet.class);
    }

    public PetDataEditor(IEditableComponentFactory factory) {
        super(Pet.class, factory);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setH3(++row, 0, 2, i18n.tr("Pet Data"));

        panel.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(15).build()));
        panel.setWidget(++row, 0, inject(proto().color(), new FieldDecoratorBuilder(15).build()));
        panel.setWidget(++row, 0, inject(proto().breed(), new FieldDecoratorBuilder(15).build()));

        row = 0; // skip header
        panel.setWidget(++row, 1, inject(proto().weight(), new FieldDecoratorBuilder(4).build()));
        panel.setWidget(++row, 1, inject(proto().weightUnit(), new FieldDecoratorBuilder(4).build()));
        panel.setWidget(++row, 1, inject(proto().birthDate(), new FieldDecoratorBuilder(9).build()));

        removeMandatory();
        return panel;
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
