/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.pad.batch;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimDebitRecord;

public class DebitRecordEditor extends OperationsEntityForm<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordEditor(IForm<PadSimDebitRecord> view) {
        super(PadSimDebitRecord.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().clientId()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().transactionId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().bankId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().branchTransitNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().accountNumber()).decorate().componentWidth(120);

        formPanel.append(Location.Dual, proto().acknowledgmentStatusCode()).decorate().componentWidth(120);
        formPanel.append(Location.Dual, proto().reconciliationStatus()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().reasonCode()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().fee()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().updated()).decorate();

        formPanel.append(Location.Right, proto().paymentDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().reasonText()).decorate().componentWidth(120);

        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}
