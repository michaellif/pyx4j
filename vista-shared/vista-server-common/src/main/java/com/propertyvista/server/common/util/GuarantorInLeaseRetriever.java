/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;

public class GuarantorInLeaseRetriever extends TenantRetriever {

    public Guarantor guarantorInLease;

    // Construction:
    public GuarantorInLeaseRetriever() {
        super(Customer.class);
    }

    public GuarantorInLeaseRetriever(Key tenanInLeasetId) {
        super(Customer.class, false);
        retrieve(tenanInLeasetId);
    }

    public GuarantorInLeaseRetriever(Key tenanInLeasetId, boolean financial) {
        super(Customer.class, financial);
        retrieve(tenanInLeasetId);
    }

    // Manipulation:
    @Override
    public void retrieve(Key tenanInLeasetId) {
        guarantorInLease = Persistence.service().retrieve(Guarantor.class, tenanInLeasetId);
        // TODO correct this check:
        if ((guarantorInLease == null) /* || (!guarantorInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }

        super.retrieve(guarantorInLease.customer().getPrimaryKey());
    }
}
