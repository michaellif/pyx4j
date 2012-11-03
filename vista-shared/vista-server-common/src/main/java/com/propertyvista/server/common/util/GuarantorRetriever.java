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

import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;

public class GuarantorRetriever extends CustomerRetriever {

    private LeaseTermGuarantor guarantor;

    public GuarantorRetriever() {
        super();
    }

    public GuarantorRetriever(Key guarantorId) {
        this();
        retrieve(guarantorId);
    }

    public GuarantorRetriever(Key guarantorId, boolean retrieveFinancialData) {
        super(retrieveFinancialData);
        retrieve(guarantorId);
    }

    public LeaseTermGuarantor getGuarantor() {
        return guarantor;
    }

    @Override
    public void retrieve(Key guarantorId) {
        guarantor = Persistence.service().retrieve(LeaseTermGuarantor.class, guarantorId);
        if (guarantor == null) {
            throw new SecurityViolationException("Invalid data access");
        }
        super.retrieve(guarantor.leaseParticipant().customer());
        super.retrieve(guarantor.screening());
    }
}
