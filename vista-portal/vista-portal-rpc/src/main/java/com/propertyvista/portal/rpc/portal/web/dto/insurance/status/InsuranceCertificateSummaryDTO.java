/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.insurance.status;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
@Transient
public interface InsuranceCertificateSummaryDTO extends IEntity {

    IPrimitive<String> insuranceProvider();

    @NotNull
    @ToString(index = 1)
    @Caption(name = "Certificate Number")
    IPrimitive<String> insuranceCertificateNumber();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Personal Liability")
    IPrimitive<BigDecimal> liabilityCoverage();

    IPrimitive<LogicalDate> inceptionDate();

    IPrimitive<LogicalDate> expiryDate();

    /**
     * <code>true</code> when the tenant in the context is the owner of the insurance policy, <code>false</code> when tenant is covered by insurance certificate
     * provided by roommate
     */
    IPrimitive<Boolean> isOwner();
}
