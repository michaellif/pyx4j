/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 */
package com.propertyvista.domain.legal;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;

@Deprecated
@Transient
public interface LegalNoticeCandidate extends IEntity {

    Lease leaseId();

    IPrimitive<BigDecimal> amountOwed();

    /** Last N4 date and of previously issued N4s and */
    IPrimitive<String> n4Issued();

}
