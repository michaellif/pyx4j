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
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimFileCrudService;

public class DirectDebitSimFileListerViewImpl extends AbstractListerView<DirectDebitSimFile> implements DirectDebitSimFileListerView {

    public DirectDebitSimFileListerViewImpl() {
        setDataTablePanel(new DirectDebitSimFileLister());
    }

    public static class DirectDebitSimFileLister extends SiteDataTablePanel<DirectDebitSimFile> {

        public DirectDebitSimFileLister() {
            super(DirectDebitSimFile.class, GWT.<DirectDebitSimFileCrudService> create(DirectDebitSimFileCrudService.class), true);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().serialNumber()).build(), //
                    new ColumnDescriptor.Builder(proto().creatationDate()).build(), //
                    new ColumnDescriptor.Builder(proto().sentDate()).build(), //
                    new ColumnDescriptor.Builder(proto().status()).build());

            setDataTableModel(new DataTableModel<DirectDebitSimFile>());
        }
    }
}
