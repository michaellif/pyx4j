/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.moveinwizardmockup;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

@DiscriminatorValue(value = "ExistingInsurance")
@Transient
public interface ExistingInsurance extends IEntity {

    IPrimitive<String> insuranceProvider();

    IPrimitive<String> insuranceCertificateNumber();

    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> personalLiability();

    IPrimitive<LogicalDate> insuranceStartDate();

    IPrimitive<LogicalDate> insuranceExpirationDate();

    InsuranceCertificate incuranceCertificate();

}
