/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
@SecurityEnabled
@ExtendsBO(Tenant.class)
public interface TenantDTO extends LeaseParticipantDTO<LeaseTermTenant> {

    IPrimitive<Role> role();

    IPrimitive<Boolean> isPotentialTenant();

    @Format("#,##0.00")
    @Editor(type = Editor.EditorType.moneylabel)
    @Caption(description = "This is the minimum liability that was set by tenant insurance policy")
    IPrimitive<BigDecimal> minimumRequiredLiability();

    IList<InsuranceCertificate<?>> insuranceCertificates();

    @Caption(name = "Pre-Authorized Payments")
    IList<PreauthorizedPaymentDTO> preauthorizedPayments();

    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> nextScheduledPaymentDate();

    IPrimitive<String> nextAutopayApplicabilityMessage();

    IPrimitive<Boolean> isMoveOutWithinNextBillingCycle();

    // Restrictions policy:
    IPrimitive<Boolean> emergencyContactsIsMandatory();

    IPrimitive<Integer> emergencyContactsAmount();
}
