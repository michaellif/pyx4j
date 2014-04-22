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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.legal.ltbcommon.LtbAgentContactInfo;

public class LtbAgentContactInfoForm extends CForm<LtbAgentContactInfo> {

    public LtbAgentContactInfoForm() {
        super(LtbAgentContactInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, inject(proto().firstName(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().lastName(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().companyName(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().mailingAddress(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().unit(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().municipality(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().province(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().postalCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().phoneNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().faxNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().email(), new FieldDecoratorBuilder().build()));
        return panel;
    }

}
