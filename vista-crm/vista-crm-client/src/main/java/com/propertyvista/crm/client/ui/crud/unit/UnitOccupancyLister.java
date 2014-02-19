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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitOccupancyLister extends AbstractLister<AptUnitOccupancySegment> {

    public UnitOccupancyLister() {
        super(AptUnitOccupancySegment.class, false);
        getDataTablePanel().setFilteringEnabled(false);
        setAllowZoomIn(true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().dateFrom()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().dateTo()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().status()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().offMarket()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseId()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseFrom()).sortable(false).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseTo()).sortable(false).build()
        );//@formatter:on        
    }

    @Override
    protected void onItemSelect(AptUnitOccupancySegment item) {
        if (item.status().getValue() == Status.occupied) {
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Lease.class).formViewerPlace(item.lease().getPrimaryKey()));
        }
    }
}
