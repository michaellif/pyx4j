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
package com.propertyvista.crm.client.ui.tools.l1generation;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.ltbcommon.LtbAgentContactInfo;

public class LtbAgentContactInfoForm extends CEntityForm<LtbAgentContactInfo> {

    public LtbAgentContactInfoForm() {
        super(LtbAgentContactInfo.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().firstName())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lastName())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().companyName())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().mailingAddress())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unit())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().municipality())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().province())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().postalCode())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().phoneNumber())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().faxNumber())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().email())).build());
        return panel;
    }

}
