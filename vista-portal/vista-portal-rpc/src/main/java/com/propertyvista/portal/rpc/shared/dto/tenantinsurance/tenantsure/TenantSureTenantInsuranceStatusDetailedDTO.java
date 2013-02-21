/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface TenantSureTenantInsuranceStatusDetailedDTO extends IEntity {

    @Caption(name = "Certificate Number")
    IPrimitive<String> insuranceCertificateNumber();

    /**
     * If tenant sure has been cancelled, it should hold the expiry date
     */
    IPrimitive<LogicalDate> expiryDate();

    TenantSureQuoteDTO quote();

    TenantSurePaymentDTO nextPaymentDetails();

    IPrimitive<Boolean> isPaymentFailed();

    IList<TenantSureMessageDTO> messages();

}
