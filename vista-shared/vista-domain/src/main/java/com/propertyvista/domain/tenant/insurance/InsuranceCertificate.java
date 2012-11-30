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
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.media.ApplicationDocumentHolder;
import com.propertyvista.domain.media.InsuranceCertificateDocument;
import com.propertyvista.domain.tenant.lease.Tenant;

@DiscriminatorValue("InsuranceCertificate")
public interface InsuranceCertificate extends ApplicationDocumentHolder<InsuranceCertificateDocument> {

    @Caption(name = "Owned By")
    @Owner
    @JoinColumn
    Tenant tenant();

    /** <code>true</code> for certificates create via Property Vista integrated systems */
    IPrimitive<Boolean> isPropertyVistaIntegratedProvider();

    @NotNull
    IPrimitive<String> insuranceProvider();

    @NotNull
    IPrimitive<String> insuranceCertificateNumber();

    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    @NotNull
    IPrimitive<BigDecimal> liabilityCoverage();

    @NotNull
    IPrimitive<LogicalDate> inceptionDate();

    @NotNull
    IPrimitive<LogicalDate> expiryDate();

}
