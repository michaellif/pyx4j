/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2014
 * @author Artyom
 */
package com.propertyvista.biz.tenant.lease.print;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;

public interface LeaseTermAgreementSigningProgressFacade {

    boolean shouldSign(LeaseTermParticipant<?> participant);

    LeaseAgreementSigningProgressDTO getSigningProgress(Lease leaseId);

    boolean isEmployeeSignatureRequired(Lease lease);
}
