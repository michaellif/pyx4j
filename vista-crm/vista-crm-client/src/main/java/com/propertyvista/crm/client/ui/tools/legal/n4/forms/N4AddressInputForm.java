/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.dto.legal.n4.N4AddressInputDTO;

public class N4AddressInputForm extends CEntityForm<N4AddressInputDTO> {

    private static final I18n i18n = I18n.get(N4AddressInputForm.class);

    public N4AddressInputForm() {
        super(N4AddressInputDTO.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Agent/Company Mailing Information"));
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().companyName())).build());
        panel.setWidget(++row, 0, inject(proto().mailingAddress(), new AddressSimpleEditor(true)));
        row = -1;
        panel.setH1(++row, 1, 1, i18n.tr("Building Owner Mailing Information"));
        panel.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().buildingOwnerName())).build());
        panel.setWidget(++row, 1, inject(proto().buildingOwnerMailingAddress(), new AddressSimpleEditor(true)));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().buildingOwnerSameAsLandlord())).build());
        return panel;
    }

}
