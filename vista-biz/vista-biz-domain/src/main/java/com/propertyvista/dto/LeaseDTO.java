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
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsBO
public interface LeaseDTO extends Lease {

    TransactionHistoryDTO transactionHistory();

    // -----------------------------------------------------
    // temporary runtime data:

    BillDTO billingPreview();

    IPrimitive<String> unitMoveOutNote();

    IPrimitive<Boolean> preauthorizedPaymentPresent();

    IPrimitive<Boolean> historyPresent();

    IPrimitive<Boolean> isMoveOutWithinNextBillingCycle();

    IList<InsuranceCertificate> tenantInsuranceCertificates();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Initial Balance")
    IPrimitive<BigDecimal> carryforwardBalance();

    IList<LegalLetter> letters();

    IPrimitive<String> currentLegalStatus();

    IList<LegalStatusDTO> legalStatusHistory();

    @Editor(type = EditorType.label)
    IPrimitive<Boolean> isUnitReserved();

    @Editor(type = EditorType.label)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> reservedUntil();
}
