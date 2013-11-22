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

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseApprovalDTO;

public interface ScreeningFacade {

    boolean isCreditCheckActivated();

    boolean isReadReportLimitReached();

    PmcEquifaxStatus getCreditCheckServiceStatus();

    void calculateSuggestedDecision(BigDecimal rentAmount, LeaseApprovalDTO leaseApproval);

    void runCreditCheck(BigDecimal rentAmount, LeaseTermParticipant<?> leaseParticipantId, Employee currentUserEmployee);

    /**
     * Retrieve draft if there are no final version
     */
    CustomerScreening retrivePersonScreeningFinalOrDraft(Customer customerId, AttachLevel attachLevel);

    /**
     * Find if Draft exists, if not find final version
     */
    CustomerScreening retrivePersonScreeningDraftOrFinal(Customer customerId, AttachLevel attachLevel);

    /**
     * Find if Draft exists, if not find final version. If Draft - finalize it.
     * Used on Lease Application approval (and every LeaseTerm finalization)
     */
    CustomerScreening retriveAndFinalizePersonScreening(Customer customerId, AttachLevel attachLevel);

    CustomerCreditCheck retrivePersonCreditCheck(Customer customerId);

    /**
     * @return null if Report storage already expired
     */
    CustomerCreditCheckLongReportDTO retriveLongReport(Customer customerId);
}
