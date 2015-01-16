/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author VladL
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.policies.ApplicationApprovalChecklistPolicy;

public interface ApplicationApprovalChecklistPolicyItem extends IEntity {

    public interface StatusSelectionPolicyItem extends IEntity {

        @Owner
        @NotNull
        @MemberColumn(notNull = true)
        @ReadOnly
        @Detached
        @Indexed
        @JoinColumn
        ApplicationApprovalChecklistPolicyItem checklistItem();

        @OrderColumn
        IPrimitive<Integer> orderInChecklistItem();

        @ToString
        IPrimitive<String> statusSelection();
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    ApplicationApprovalChecklistPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

    @ToString
    IPrimitive<String> itemToCheck();

    @Owned
    IList<StatusSelectionPolicyItem> statusesToSelect();
}