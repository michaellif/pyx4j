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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.LicenseEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.dto.BoilerDTO;

public class BoilerForm extends MechBaseForm<BoilerDTO> {

    private static final I18n i18n = I18n.get(BoilerForm.class);

    public BoilerForm(IForm<BoilerDTO> view) {
        super(BoilerDTO.class, view);
    }

    @Override
    protected FormFlexPanel createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().make()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().model()), 15).build());

        row = 0;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().build()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().description()), 20).build());
        main.getFlexCellFormatter().setRowSpan(row, 1, 3);

        row += 2;
        main.setH1(++row, 0, 2, proto().license().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().license(), new LicenseEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().build()));
    }
}