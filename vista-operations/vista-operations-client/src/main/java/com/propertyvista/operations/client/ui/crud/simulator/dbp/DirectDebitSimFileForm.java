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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile.DirectDebitSimFileStatus;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimFileForm extends OperationsEntityForm<DirectDebitSimFile> {

    private static final I18n i18n = I18n.get(DirectDebitSimFileForm.class);

    public static class DirectDebitSimRecordTableFolder extends VistaTableFolder<DirectDebitSimRecord> {

        private final List<FolderColumnDescriptor> columns;

        public DirectDebitSimRecordTableFolder() {
            super(DirectDebitSimRecord.class);
            columns = Arrays.asList(//@formatter:off
                    new FolderColumnDescriptor(proto().accountNumber(), "15em"),
                    new FolderColumnDescriptor(proto().paymentReferenceNumber(), "20em"),
                    new FolderColumnDescriptor(proto().customerName(), "20em"),
                    new FolderColumnDescriptor(proto().amount(), "10em"),
                    new FolderColumnDescriptor(proto().receivedDate(), "10em")
            );//@formatter:off
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return columns;
        }

    }

    public DirectDebitSimFileForm(IForm<DirectDebitSimFile> view) {
        super(DirectDebitSimFile.class, view);

        FormPanel formPanel = new FormPanel(this);
        
        formPanel.append(Location.Dual, proto().status()).decorate();
        formPanel.append(Location.Dual, proto().serialNumber()).decorate();
        formPanel.append(Location.Dual, proto().creatationDate()).decorate();
        formPanel.append(Location.Dual, proto().sentDate()).decorate(); 
        formPanel.h1("Direct Debit Records");
        formPanel.append(Location.Dual, proto().records(), new DirectDebitSimRecordTableFolder());
        
        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));

    }
    
    @Override
    protected void onValueSet(boolean populate) {     
        super.onValueSet(populate);
        get(proto().records()).setEnabled(getValue().status().getValue() == DirectDebitSimFileStatus.New | getValue().status().getValue() == null);         
    }

}
