/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.media;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.ProofOfIncomeDocumentBlob;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;

public interface ProofOfIncomeDocumentFile extends IHasFile<ProofOfIncomeDocumentBlob> {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    CustomerScreeningIncome owner();

    @Override
    @EmbeddedEntity
    @NotNull
    IFile<ProofOfIncomeDocumentBlob> file();

    @ToString(index = 0)
    IPrimitive<String> description();
}
