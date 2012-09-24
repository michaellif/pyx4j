/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.domain.tenant.income;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.media.ApplicationDocumentHolder;
import com.propertyvista.domain.media.ProofOfEmploymentDocument;
import com.propertyvista.domain.tenant.PersonScreening;

@DiscriminatorValue("PersonalIncome")
public interface PersonalIncome extends IEntity, ApplicationDocumentHolder<ProofOfEmploymentDocument> {

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    PersonScreening owner();

    @ToString
    @NotNull
    IPrimitive<IncomeSource> incomeSource();

    @Owned
    IncomeInfo details();

    @OrderColumn
    IPrimitive<Integer> orderInOwner();
}
