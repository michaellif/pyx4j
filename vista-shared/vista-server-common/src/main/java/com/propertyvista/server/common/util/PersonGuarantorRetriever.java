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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.tenant.Guarantor_Old;
import com.propertyvista.domain.tenant.PersonGuarantor;

public class PersonGuarantorRetriever extends TenantRetriever {

    public PersonGuarantor personGuarantor;

    // Construction:
    public PersonGuarantorRetriever() {
        super(Guarantor_Old.class);
    }

    public PersonGuarantorRetriever(Key personGuarantorId) {
        super(Guarantor_Old.class, false);
        retrieve(personGuarantorId);
    }

    public PersonGuarantorRetriever(Key personGuarantorId, boolean financial) {
        super(Guarantor_Old.class, financial);
        retrieve(personGuarantorId);
    }

    public PersonGuarantorRetriever(Guarantor_Old guarantor) {
        super(Guarantor_Old.class, true);
        // retrieve data opposite way:
        super.retrieve(guarantor.getPrimaryKey());
        EntityQueryCriteria<PersonGuarantor> criteria = EntityQueryCriteria.create(PersonGuarantor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().guarantor(), guarantor));
        personGuarantor = Persistence.service().retrieve(criteria);
        if ((personGuarantor == null) /* || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }
    }

    // Manipulation:
    @Override
    public void retrieve(Key personGuarantorId) {
        personGuarantor = Persistence.service().retrieve(PersonGuarantor.class, personGuarantorId);
        // TODO correct this check:
        if ((personGuarantor == null) /* || (!tenantInLease.lease().getPrimaryKey().equals(PtAppContext.getCurrentUserLeasePrimaryKey())) */) {
            throw new SecurityViolationException("Invalid data access");
        }

        super.retrieve(personGuarantor.guarantor().getPrimaryKey());
    }
}
