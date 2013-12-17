/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-06
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.tenant.CustomerScreening.CustomerScreeningV;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;

@DiscriminatorValue("CustomerScreening")
public interface CustomerScreening extends IVersionedEntity<CustomerScreeningV> {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    Customer screene();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<CustomerCreditCheck> creditChecks();

    public interface CustomerScreeningV extends IVersionData<CustomerScreening> {

        @Timestamp(Update.Created)
        IPrimitive<LogicalDate> createDate();

        @ToString(index = 0)
        @Timestamp(Update.Updated)
        IPrimitive<LogicalDate> updateDate();

        /**
         * TODO I think that it is better to have a list here since some forms may ask for
         * more than one previous address
         */
        @EmbeddedEntity
        PriorAddress currentAddress();

        @EmbeddedEntity
        PriorAddress previousAddress();

        @Owned
        @Caption(name = "General Questions")
        CustomerScreeningLegalQuestions legalQuestions();

        //=============== Financial =============//

        @Owned
        @Detached
        @Length(3)
        @Caption(name = "Income")
        IList<CustomerScreeningIncome> incomes();

        @Owned
        @Detached
        @Length(3)
        IList<CustomerScreeningPersonalAsset> assets();

        @Owned
        @OrderBy(PrimaryKey.class)
        IList<IdentificationDocumentFolder> documents();
    }
}
