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

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo;

public class L1LandlordsContactInfoForm extends CEntityForm<L1LandlordsContactInfo> {

    public L1LandlordsContactInfoForm() {
        super(L1LandlordsContactInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().typeOfLandlord(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().firstName(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().lastName(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().streetAddress(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().unit(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().municipality(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().province(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().postalCode(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().dayPhoneNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().eveningPhoneNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().faxNumber(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 2, inject(proto().emailAddress(), new FormDecoratorBuilder().build()));
        return panel;
    }
}
