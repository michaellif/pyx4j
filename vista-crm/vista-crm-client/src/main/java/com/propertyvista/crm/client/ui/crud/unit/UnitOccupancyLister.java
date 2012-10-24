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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitOccupancyLister extends ListerBase<AptUnitOccupancySegment> {

    public UnitOccupancyLister() {
        super(AptUnitOccupancySegment.class, true, false);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().dateFrom()).build(),
            new MemberColumnDescriptor.Builder(proto().dateTo()).build(),
            new MemberColumnDescriptor.Builder(proto().status()).build(),
            new MemberColumnDescriptor.Builder(proto().offMarket()).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseId()).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseFrom()).build(),
            new MemberColumnDescriptor.Builder(proto().lease().leaseTo()).build()
        );//@formatter:on
    }

    @Override
    protected void onItemSelect(AptUnitOccupancySegment item) {
        if (item.status().getValue() == Status.leased) {
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Lease.class).formViewerPlace(item.lease().getPrimaryKey()));
        } else if (item.status().getValue() == Status.reserved) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(item.lease().getPrimaryKey()));
        }
    }
}
