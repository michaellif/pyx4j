/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial.moneyin;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface MoneyInCandidateDTO extends IEntity {

    Lease leaseIdStub();

    IPrimitive<String> building();

    IPrimitive<String> unit();

    IPrimitive<String> leaseId();

    IList<MoneyInLeaseParticipantDTO> payerCandidates();

    IPrimitive<BigDecimal> totalOutstanding();

    IPrimitive<Boolean> processPayment();

    MoneyInPaymentDTO payment();

}
