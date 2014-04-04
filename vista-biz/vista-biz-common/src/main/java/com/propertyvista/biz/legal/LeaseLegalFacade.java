/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.util.List;

import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.tenant.lease.Lease;

public interface LeaseLegalFacade {

    LegalStatus getCurrentLegalStatus(Lease leaseId);

    List<LegalStatus> getLegalStatusHistory(Lease leaseId);

    void removeLegalStatus(LegalStatus legalStatusId);

    void setLegalStatus(Lease leaseId, LegalStatus status, List<LegalLetter> attachedLetters);

}
