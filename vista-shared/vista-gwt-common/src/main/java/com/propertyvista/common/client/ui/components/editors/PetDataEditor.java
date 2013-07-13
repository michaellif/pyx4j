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

import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.lease.extradata.Pet;

public class PetDataEditor extends CEntityDecoratableForm<Pet> {

    private static final I18n i18n = I18n.get(PetDataEditor.class);

    public PetDataEditor() {
        super(Pet.class);
    }

    public PetDataEditor(IEditableComponentFactory factory) {
        super(Pet.class, factory);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setH3(++row, 0, 2, i18n.tr("Pet Data"));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 15).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().color()), 15).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().breed()), 15).build());

        row = 0; // skip header
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().weight()), 4).build());
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().weightUnit()), 4).build());
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().birthDate()), 9).build());

        return panel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
    }
}
