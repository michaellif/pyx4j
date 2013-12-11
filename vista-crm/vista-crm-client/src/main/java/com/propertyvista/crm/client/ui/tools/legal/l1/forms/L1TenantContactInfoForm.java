/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.l1.L1TenantContactInfo;

public class L1TenantContactInfoForm extends CEntityForm<L1TenantContactInfo> {

    private static final I18n i18n = I18n.get(L1TenantContactInfo.class);

    public L1TenantContactInfoForm() {
        super(L1TenantContactInfo.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setH2(++row, 0, 2, i18n.tr("Mailing Address"));
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().mailingAddress())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().unit())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().municipality())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().province())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().postalCode())).build());

        panel.setH2(++row, 0, 2, i18n.tr("Phones and Email"));
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().dayPhoneNumber())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().eveningPhoneNumber())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().faxNumber())).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().emailAddress())).build());

        return panel;
    }

}
