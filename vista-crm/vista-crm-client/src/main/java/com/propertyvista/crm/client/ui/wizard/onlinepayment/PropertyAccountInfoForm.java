/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class PropertyAccountInfoForm extends CEntityDecoratableForm<OnlinePaymentSetupDTO.PropertyAccountInfo> {

    public PropertyAccountInfoForm() {
        super(PropertyAccountInfo.class);

    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().averageMonthlyRent())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().numberOfRentedUnits())).build());
        panel.setWidget(++row, 0, new HTML("&nbsp;"));
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().transitNumber())).build());

        int irow = row; // save the row that will hold the image with the cheque guide
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().institutionNumber())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountNumber())).build());

        panel.setWidget(irow, 1, new Image(VistaImages.INSTANCE.eChequeGuide()));
        panel.getFlexCellFormatter().setRowSpan(irow, 1, 3);
        return panel;
    }
}