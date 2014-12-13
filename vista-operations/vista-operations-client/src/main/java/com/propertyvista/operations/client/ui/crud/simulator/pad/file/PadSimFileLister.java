/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.pad.file;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;
import com.propertyvista.operations.rpc.services.simulator.PadSimFileCrudService;

public class PadSimFileLister extends SiteDataTablePanel<PadSimFile> {

    public PadSimFileLister() {
        super(PadSimFile.class, GWT.<AbstractCrudService<PadSimFile>> create(PadSimFileCrudService.class), false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().fileName()).build(), //
                new ColumnDescriptor.Builder(proto().fundsTransferType()).build(), //
                new ColumnDescriptor.Builder(proto().fileCreationNumber()).build(), //
                new ColumnDescriptor.Builder(proto().state()).build(), //
                new ColumnDescriptor.Builder(proto().returns()).build(), //
                new ColumnDescriptor.Builder(proto().received()).build(), //
                new ColumnDescriptor.Builder(proto().recordsCount()).build(), //
                new ColumnDescriptor.Builder(proto().fileAmount()).build(), //
                new ColumnDescriptor.Builder(proto().acknowledgmentStatusCode()).build());

        setDataTableModel(new DataTableModel<PadSimFile>());
    }
}
