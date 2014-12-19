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
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
@ToStringFormat("{0}, #: {1}, Coverage: {2,choice,null#empty|!null#${2}}{3,choice,null#|!null#, Expiry: {3}}")
public interface InsuranceCertificate<INSURANCE_POLICY extends InsurancePolicy<?>> extends IEntity {

    @Owner
    @JoinColumn
    @ReadOnly
    @Detached(level = AttachLevel.IdOnly)
    INSURANCE_POLICY insurancePolicy();

    /** <code>true</code> for certificates that have been uploaded / created by tenant's initiative */
    IPrimitive<Boolean> isManagedByTenant();

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> insuranceProvider();

    @NotNull
    @ToString(index = 1)
    @Caption(name = "Certificate Number")
    IPrimitive<String> insuranceCertificateNumber();

    @NotNull
    @ToString(index = 2)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> liabilityCoverage();

    @NotNull
    IPrimitive<LogicalDate> inceptionDate();

    @NotNull
    @ToString(index = 3)
    IPrimitive<LogicalDate> expiryDate();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<InsuranceCertificateScan> certificateDocs();
}
