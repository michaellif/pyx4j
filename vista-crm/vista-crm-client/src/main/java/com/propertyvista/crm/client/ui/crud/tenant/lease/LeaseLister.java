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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.LeaseDTO;

public class LeaseLister extends ListerBase<LeaseDTO> {

    public LeaseLister() {
        super(LeaseDTO.class, CrmSiteMap.Tenants.Lease.class, false, true);

        setColumnDescriptors(

        //TODO Fix make sortable
                new Builder(proto().unit().belongsTo().propertyCode()).sortable(false).build(),

                new Builder(proto().unit()).build(),

                new Builder(proto().leaseID()).build(),

                new Builder(proto().type()).build(),

                new Builder(proto().leaseFrom()).build(),

                new Builder(proto().leaseTo()).build(),

                new Builder(proto().expectedMoveIn()).build(),

                new Builder(proto().expectedMoveOut()).build(),

                new Builder(proto().actualMoveIn()).build(),

                new Builder(proto().actualMoveOut()).build(),

                new Builder(proto().moveOutNotice()).build(),

                new Builder(proto().status()).build(),

                new Builder(proto().signDate()).build());

    }

}
