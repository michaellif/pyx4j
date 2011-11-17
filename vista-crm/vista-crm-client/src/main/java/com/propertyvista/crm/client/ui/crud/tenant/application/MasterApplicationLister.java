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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.MasterApplicationDTO;

public class MasterApplicationLister extends ListerBase<MasterApplicationDTO> {

    public MasterApplicationLister() {
        super(MasterApplicationDTO.class, CrmSiteMap.Tenants.MasterApplication.class, true);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<MasterApplicationDTO>> columnDescriptors, MasterApplicationDTO proto) {
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.id()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().type()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().unit().belongsTo().propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().unit()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.mainApplicant().tenant().person().name(), proto.mainApplicant()
                .getMeta().getCaption()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.numberOfCoApplicants()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.numberOfGuarantors()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentPrice()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.createDate()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().leaseFrom()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().leaseTo()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().status()));
    }

    @Override
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<MasterApplicationDTO>> columnDescriptors, MasterApplicationDTO proto) {
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.id()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().type()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().unit().belongsTo().propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().unit()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptorEx(proto, proto.mainApplicant().tenant().person().name(), proto.mainApplicant()
                .getMeta().getCaption()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.numberOfOccupants()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.numberOfCoApplicants()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.numberOfGuarantors()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.rentPrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.parkingPrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.otherPrice()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.discounts()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.createDate()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().leaseFrom()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().leaseTo()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().status()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().expectedMoveIn()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().expectedMoveOut()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().actualMoveIn()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().actualMoveOut()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.lease().moveOutNotice()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.percenrtageApproved()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.suggestedDecision()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.decidedBy()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.decisionDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.decisionReason()));
    }
}
