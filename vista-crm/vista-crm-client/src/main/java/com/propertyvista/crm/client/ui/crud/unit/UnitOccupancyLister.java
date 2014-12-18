/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitOccupancyLister extends SiteDataTablePanel<AptUnitOccupancySegment> {

    public UnitOccupancyLister() {
        super(AptUnitOccupancySegment.class, GWT.<UnitOccupancyCrudService> create(UnitOccupancyCrudService.class), false);
        setFilteringEnabled(false);
        setItemZoomInCommand(new ItemZoomInCommand<AptUnitOccupancySegment>() {
            @Override
            public void execute(AptUnitOccupancySegment item) {
                if (item.status().getValue() == Status.occupied) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Lease.class).formViewerPlace(item.lease().getPrimaryKey()));
                }
            }
        });

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().dateFrom()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().dateTo()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().status()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().offMarket()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().lease().leaseId()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().lease().leaseFrom()).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().lease().leaseTo()).sortable(false).build());

        setDataTableModel(new DataTableModel<AptUnitOccupancySegment>());
    }
}
