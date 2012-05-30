/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.dto.BillDTO;

public class ViewBillForm extends CEntityForm<BillDTO> {

    private static final I18n i18n = I18n.get(ViewBillForm.class);

    public ViewBillForm() {
        super(BillDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();

        int row = -1;
        container.setH1(++row, 0, 1, "Bill content goes here...");

//        container.setWidget(++row, 0, inject(proto().charges(), new ChargeLineFolder()));
//        container.setHR(++row, 0, 1);
//        //TODO review later (display total)
//        container.setWidget(++row, 0, inject(proto().total()/* , new TotalLineViewer() */));
//        container.setWidget(++row, 0, inject(proto().dueDate(), new DueDateViewer()));
//        container.setWidget(++row, 0, inject(proto().paymentMethod(), new PaymentMethodViewer()));
//        container.setWidget(++row, 0, inject(proto().preAuthorized(), new PreauthorizedOutcomeViewer()));
        return container;
    }
}
