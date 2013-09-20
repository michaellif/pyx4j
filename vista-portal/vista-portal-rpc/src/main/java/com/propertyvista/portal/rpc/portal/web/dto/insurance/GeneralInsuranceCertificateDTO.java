/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.media.ApplicationDocumentHolder;
import com.propertyvista.domain.media.InsuranceCertificateDocument;

@Transient
public interface GeneralInsuranceCertificateDTO extends ApplicationDocumentHolder<InsuranceCertificateDocument>, IEntity {

    IPrimitive<BigDecimal> minLiability();

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> insuranceProvider();

    @NotNull
    @ToString(index = 1)
    @Caption(name = "Certificate Number")
    IPrimitive<String> insuranceCertificateNumber();

    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    @ToString(index = 2)
    @NotNull
    IPrimitive<BigDecimal> liabilityCoverage();

    @NotNull
    IPrimitive<LogicalDate> inceptionDate();

    @NotNull
    @ToString(index = 3)
    IPrimitive<LogicalDate> expiryDate();
}
