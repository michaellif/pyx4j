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
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class BillingCycleLeaseLister extends ListerBase<LeaseDTO> {

    public BillingCycleLeaseLister() {
        super(LeaseDTO.class, false);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).build(),
            
            new Builder(proto().version().status()).build(),
            new Builder(proto().version().completion()).build(),

            new Builder(proto().billingAccount().accountNumber()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().version().expectedMoveIn()).build(),
            new Builder(proto().version().expectedMoveOut(), false).build(),
            new Builder(proto().version().actualMoveIn(), false).build(),
            new Builder(proto().version().actualMoveOut(), false).build(),
            new Builder(proto().version().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build(),
            
            new Builder(proto().version().tenants()).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.current()));
        return super.updateCriteria(criteria);
    }
}
