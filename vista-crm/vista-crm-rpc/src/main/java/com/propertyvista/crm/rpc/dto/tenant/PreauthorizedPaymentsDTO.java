/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

@Transient
public interface PreauthorizedPaymentsDTO extends IEntity {

    Tenant tenant();

    @Transient
    @ToStringFormat("{0}, {1}")
    interface TenantInfo extends IEntity {

        @ToString(index = 0)
        Name name();

        @ToString(index = 2)
        IPrimitive<Role> role();
    }

    TenantInfo tenantInfo();

    @Caption(name = "Pre-Authorized Payments")
    IList<PreauthorizedPaymentDTO> preauthorizedPayments();

    IList<LeasePaymentMethod> availablePaymentMethods();

    IPrimitive<LogicalDate> nextScheduledPaymentDate();

    IPrimitive<LogicalDate> paymentCutOffDate();
}
