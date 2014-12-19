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
package com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;

public class DirectDebitRecordForm extends OperationsEntityForm<DirectDebitRecord> {

    private static final I18n i18n = I18n.get(DirectDebitRecordForm.class);

    public DirectDebitRecordForm(IPrimeFormView<DirectDebitRecord, ?> view) {
        super(DirectDebitRecord.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().pmc().name()).decorate().customLabel("PMC:");
        formPanel.append(Location.Left, proto().accountNumber()).decorate();
        formPanel.append(Location.Left, proto().amount()).decorate();
        formPanel.append(Location.Left, proto().paymentReferenceNumber()).decorate();
        formPanel.append(Location.Left, proto().customerName()).decorate();
        formPanel.append(Location.Left, proto().receivedDate()).decorate();
        formPanel.append(Location.Left, proto().processingStatus()).decorate();
        formPanel.append(Location.Left, proto().operationsNotes()).decorate();

        formPanel.h1("File");

        formPanel.append(Location.Left, proto().directDebitFile().fileName()).decorate();
        formPanel.append(Location.Left, proto().directDebitFile().fileSerialNumber()).decorate();
        formPanel.append(Location.Left, proto().directDebitFile().fileSerialDate()).decorate();

        formPanel.h1("Trace");

        formPanel.append(Location.Left, proto().trace().collection()).decorate();
        formPanel.append(Location.Left, proto().trace().collectionDate()).decorate();
        formPanel.append(Location.Left, proto().trace().locationCode()).decorate();
        formPanel.append(Location.Left, proto().trace().sourceCode()).decorate();
        formPanel.append(Location.Left, proto().trace().traceNumber()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
