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

import com.propertyvista.biz.validation.framework.validators.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.validators.NotNullValidator;
import com.propertyvista.biz.validation.framework.validators.ValueConstraintValidator;
import com.propertyvista.domain.tenant.lease.Lease;

/**
 * This class defines/validates the conditions that must be met by a lease to get an approval.
 */
public class LeaseApprovalValidator extends CompositeEntityValidator<Lease> {

    public LeaseApprovalValidator() {
        super(Lease.class);
    }

    @Override
    protected void init() {
        bind(proto().version().status(), new ValueConstraintValidator<Lease.Status>(Lease.Status.Application, Lease.Status.Created));

        bind(proto().type(), new NotNullValidator());
        bind(proto().unit(), new NotNullValidator());
        bind(proto().paymentFrequency(), new NotNullValidator());

        bind(proto().leaseFrom(), new NotNullValidator());
        bind(proto().leaseTo(), new NotNullValidator());

        bind(proto().version().tenants(), new HasAtLeastOneApplicantValidator());
        bind(proto().version().tenants(), new TenantInApprovedLeaseValidator());

        bind(proto().version().leaseProducts().serviceItem(), new NotNullValidator());

        bind(proto(), new DatesConsistencyValidator());
    }
}
