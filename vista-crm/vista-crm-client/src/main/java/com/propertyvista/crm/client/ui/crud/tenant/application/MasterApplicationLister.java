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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.MasterApplicationDTO;

public class MasterApplicationLister extends ListerBase<MasterApplicationDTO> {

    public MasterApplicationLister() {
        super(MasterApplicationDTO.class, CrmSiteMap.Tenants.MasterApplication.class, true);

        List<ColumnDescriptor<MasterApplicationDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<MasterApplicationDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().id(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().type(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().unit().belongsTo().propertyCode(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().unit(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().mainApplicant(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().numberOfOccupants(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().numberOfCoApplicants(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().numberOfGuarantors(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().rentPrice(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().parkingPrice(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().otherPrice(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().discounts(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().createDate(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().leaseFrom(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().leaseTo(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().expectedMoveIn(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().expectedMoveOut(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().actualMoveIn(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().actualMoveOut(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().lease().moveOutNotice(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().status(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().percenrtageApproved(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().suggestedDecision(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().decidedBy(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().decisionDate(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().decisionReason(), false));

        setColumnDescriptors(columnDescriptors);
    }

}
