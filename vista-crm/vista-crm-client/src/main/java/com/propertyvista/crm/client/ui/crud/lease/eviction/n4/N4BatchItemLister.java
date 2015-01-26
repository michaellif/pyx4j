/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchItemCrudService;
import com.propertyvista.domain.legal.n4.N4BatchItem;

public class N4BatchItemLister extends SiteDataTablePanel<N4BatchItem> {

    public N4BatchItemLister() {
        super(N4BatchItem.class, GWT.<AbstractCrudService<N4BatchItem>> create(N4BatchItemCrudService.class), true);

        setColumnDescriptors(createColumnDescriptors());
        setDataTableModel(new DataTableModel<N4BatchItem>());
        getDataTableModel().setMultipleSelection(false);
    }

    public ColumnDescriptor[] createColumnDescriptors() {
        return new ColumnDescriptor[] { //
        new ColumnDescriptor.Builder(proto().lease().unit()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().lease()._applicant()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().leaseArrears().totalRentOwning()).build(), //
                new ColumnDescriptor.Builder(proto().lease().type()).build(), //
                new ColumnDescriptor.Builder(proto().lease().expectedMoveIn()).build(), //
                new ColumnDescriptor.Builder(proto().lease().expectedMoveOut()).build() //
        };
    }
}
