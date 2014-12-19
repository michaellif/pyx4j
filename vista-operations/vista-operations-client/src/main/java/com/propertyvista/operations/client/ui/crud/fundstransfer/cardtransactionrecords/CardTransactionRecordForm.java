/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;

public class CardTransactionRecordForm extends OperationsEntityForm<CardTransactionRecord> {

    private static final I18n i18n = I18n.get(CardTransactionRecordForm.class);

    public CardTransactionRecordForm(IPrimeFormView<CardTransactionRecord, ?> view) {
        super(CardTransactionRecord.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().merchantTerminalId()).decorate();

        formPanel.append(Location.Left, proto().paymentTransactionId()).decorate();
        formPanel.append(Location.Left, proto().completionDate()).decorate();
        formPanel.append(Location.Left, proto().cardType()).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate();
        formPanel.append(Location.Left, proto().feeAmount()).decorate();

        formPanel.append(Location.Left, proto().saleResponseCode()).decorate();
        formPanel.append(Location.Left, proto().voided()).decorate();
        formPanel.append(Location.Left, proto().saleResponseText()).decorate();

        formPanel.append(Location.Left, proto().feeResponseCode()).decorate();
        formPanel.append(Location.Left, proto().creationDate()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
