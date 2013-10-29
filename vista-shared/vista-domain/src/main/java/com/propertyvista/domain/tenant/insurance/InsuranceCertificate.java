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
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
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
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
@DiscriminatorValue("InsuranceCertificate")
// TODO this format fails during update of new instances of InsuranceCertificate via BoxFolder
//@ToStringFormat("Provider: {0}, Certificate #: {1}, Liability Coverage: ${2,choice,null#|!null#,#,##0.00}, Expiry: {3}")
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

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<InsuranceCertificateDoc> certificateDocs();

}
