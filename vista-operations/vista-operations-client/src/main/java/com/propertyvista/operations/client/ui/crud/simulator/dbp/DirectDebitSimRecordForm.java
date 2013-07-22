/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimRecordForm extends OperationsEntityForm<DirectDebitSimRecord> {

    public DirectDebitSimRecordForm(IForm<DirectDebitSimRecord> view) {
        super(DirectDebitSimRecord.class, view);

        TwoColumnFlexFormPanel formPanel = new TwoColumnFlexFormPanel();
        int row = -1;

        formPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().accountNumber())).build());
        formPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().amount())).build());
        formPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().paymentReferenceNumber())).build());
        formPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().customerName())).build());

        CEntityLabel<DirectDebitSimFile> fileLink = new CEntityLabel<DirectDebitSimFile>();
        fileLink.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                onGoToFile();
            }
        });
        formPanel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().file(), new CEntityLabel<DirectDebitSimFile>())).build());

        selectTab(addTab(formPanel));
    }

    protected void onGoToFile() {

    }

}
