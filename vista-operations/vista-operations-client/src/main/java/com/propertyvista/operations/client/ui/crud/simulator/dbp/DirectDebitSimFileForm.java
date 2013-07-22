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

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimFileForm extends OperationsEntityForm<DirectDebitSimFile> {

    public static class DirectDebitSimRecordTableFolder extends VistaTableFolder<DirectDebitSimRecord> {

        private final List<EntityFolderColumnDescriptor> columns;

        public DirectDebitSimRecordTableFolder() {
            super(DirectDebitSimRecord.class);
            columns = Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().accountNumber(), "15em"),
                    new EntityFolderColumnDescriptor(proto().paymentReferenceNumber(), "20em"),
                    new EntityFolderColumnDescriptor(proto().customerName(), "20em")
            );//@formatter:off
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return columns;
        }

    }

    public DirectDebitSimFileForm(IForm<DirectDebitSimFile> view) {
        super(DirectDebitSimFile.class, view);

        TwoColumnFlexFormPanel formPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        
        formPanel.setWidget(++row, 0, 2, new DecoratorBuilder(inject(proto().status())).build());
        formPanel.setWidget(++row, 0, 2, new DecoratorBuilder(inject(proto().creatationDate())).build());
        formPanel.setWidget(++row, 0, 2, new DecoratorBuilder(inject(proto().sentDate())).build());
        formPanel.setWidget(++row, 0, 2, inject(proto().records(), new DirectDebitSimRecordTableFolder()));

        selectTab(addTab(formPanel));

    }

}
