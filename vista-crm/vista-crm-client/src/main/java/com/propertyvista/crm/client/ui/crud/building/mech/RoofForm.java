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

import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.dto.RoofDTO;

public class RoofForm extends MechBaseForm<RoofDTO> {

    private static final I18n i18n = I18n.get(RoofForm.class);

    public RoofForm() {
        this(false);
    }

    public RoofForm(boolean viewMode) {
        super(RoofDTO.class, viewMode);
    }

    @Override
    protected FormFlexPanel createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().year()), 10).build());

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().year()));
    }
}