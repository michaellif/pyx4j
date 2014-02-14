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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PetDataEditor extends CEntityForm<Pet> {

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
        panel.setH3(++row, 0, 1, i18n.tr("Pet Data"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().color()), 180).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().breed()), 180).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().weight()), 50).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().weightUnit()), 50).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().birthDate()), 120).build());

        return panel;
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
