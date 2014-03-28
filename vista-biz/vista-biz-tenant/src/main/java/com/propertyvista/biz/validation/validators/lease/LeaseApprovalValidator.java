/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators.lease;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.validation.framework.validators.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.validators.NotNullValidator;
import com.propertyvista.biz.validation.framework.validators.ValueConstraintValidator;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

/**
 * This class defines/validates the conditions that must be met by a lease to get an approval.
 */
public class LeaseApprovalValidator extends CompositeEntityValidator<Lease> {

    public LeaseApprovalValidator() {
        super(Lease.class);
    }

    @Override
    protected void init() {
        bind(proto().status(), new ValueConstraintValidator<Lease.Status>(Lease.Status.Application, Lease.Status.NewLease, Lease.Status.ExistingLease));

        bind(proto().type(), new NotNullValidator());
        bind(proto().unit(), new NotNullValidator());
        bind(proto().billingAccount().billingPeriod(), new NotNullValidator());

        bind(proto().currentTerm(), new NotNullValidator());

        bind(proto().currentTerm().version().tenants(), new HasAtLeastOneApplicantValidator());
        bind(proto().currentTerm().version().tenants(), new TenantInApprovedLeaseValidator());
        bind(proto().currentTerm().version().tenants(), new LeaseTermParticipantBirthDateValidator<LeaseTermTenant>());
        bind(proto().currentTerm().version().guarantors(), new GuarantorInApprovedLeaseValidator());
        bind(proto().currentTerm().version().guarantors(), new LeaseTermParticipantBirthDateValidator<LeaseTermGuarantor>());

        bind(proto().currentTerm().version().leaseProducts().serviceItem(), new NotNullValidator());

        bind(proto(), new DatesConsistencyValidator());
    }

    @Override
    protected void prepare(Lease lease) {
        // load detached members:
        if (lease.getPrimaryKey() != null) {
            Persistence.service().retrieve(lease.currentTerm().version().tenants());
            Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        }
    }
}
