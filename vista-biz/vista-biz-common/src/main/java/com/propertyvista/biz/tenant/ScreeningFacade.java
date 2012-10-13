/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;

public interface ScreeningFacade {

    void calculateSuggestedDecision(BigDecimal rentAmount, LeaseApprovalDTO leaseApproval);

    void runCreditCheck(BigDecimal rentAmount, LeaseParticipant<?> leaseParticipantId, Employee currentUserEmployee);

    /**
     * Retrieve draft if there are no final version
     */
    PersonScreening retrivePersonScreeningFinalOrDraft(Customer customerId, AttachLevel attachLevel);

    /**
     * Find if Draft exists, if not find final version
     */
    PersonScreening retrivePersonScreeningDraftOrFinal(Customer customerId, AttachLevel attachLevel);

    PersonCreditCheck retrivePersonCreditCheck(Customer customerId);
}
