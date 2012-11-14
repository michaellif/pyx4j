/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;

@Transient
public interface TenantSureTenantInsuranceStatusDTO extends TenantInsuranceStatusDTO {

    @Format("#,##0.00")
    IPrimitive<BigDecimal> monthlyPremiumPayment();

    IPrimitive<LogicalDate> expirationDate();

    /**
     * Can be <code>null</code> if the there's some problem with credit card, i.e. credit limit, cancelled or whatever, anything that caused last payment to
     * fail.
     */
    IPrimitive<LogicalDate> nextPaymentDate();

    IList<TenantSureMessageDTO> messages();

}
