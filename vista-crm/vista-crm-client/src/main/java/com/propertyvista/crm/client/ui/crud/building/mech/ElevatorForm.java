/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.mech;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.LicenseEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorForm extends MechBaseForm<ElevatorDTO> {

    private static final I18n i18n = I18n.get(ElevatorForm.class);

    public ElevatorForm(IForm<ElevatorDTO> view) {
        super(ElevatorDTO.class, view);
    }

    @Override
    protected TwoColumnFlexFormPanel createGeneralTab() {

        TwoColumnFlexFormPanel info = new TwoColumnFlexFormPanel();

        int row = -1;
        info.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 15).build());
        info.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().make()), 15).build());
        info.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().model()), 15).build());

        row = 0;
        info.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().build()), 9).build());
        info.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().description()), 20).build());
        info.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().isForMoveInOut()), 5).build());

        // form main panel:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("General"));

        row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, info);

        main.setH1(++row, 0, 2, proto().license().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().license(), new LicenseEditor()));

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().build()));
    }
}