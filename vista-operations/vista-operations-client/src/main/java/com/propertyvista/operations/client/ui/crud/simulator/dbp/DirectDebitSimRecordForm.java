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
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimRecordForm extends OperationsEntityForm<DirectDebitSimRecord> {

    private static final I18n i18n = I18n.get(DirectDebitSimRecordForm.class);

    public DirectDebitSimRecordForm(IFormView<DirectDebitSimRecord, ?> view) {
        super(DirectDebitSimRecord.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().accountNumber()).decorate().componentWidth(240);
        formPanel.append(Location.Dual, proto().amount()).decorate().componentWidth(240);
        formPanel.append(Location.Dual, proto().paymentReferenceNumber()).decorate().componentWidth(240);
        formPanel.append(Location.Dual, proto().customerName()).decorate().componentWidth(240);
        formPanel.append(Location.Dual, proto().receivedDate()).decorate().componentWidth(240);

        CEntityLabel<DirectDebitSimFile> fileLink = new CEntityLabel<DirectDebitSimFile>();
        fileLink.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                onGoToFile();
            }
        });
        formPanel.append(Location.Dual, proto().directDebitFile(), fileLink).decorate();

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }

    protected void onGoToFile() {

    }

}
