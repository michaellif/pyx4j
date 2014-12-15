/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.ILocalizedEntity;
import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;

@DiscriminatorValue("LegalQuestionsPolicy")
@LowestApplicableNode(value = OrganizationPoliciesNode.class)
public interface LegalQuestionsPolicy extends Policy {

    public interface LegalQuestionPolicyItem extends ILocalizedEntity {
        @Owner
        @NotNull
        @MemberColumn(notNull = true)
        @ReadOnly
        @Detached
        @Indexed
        @JoinColumn
        LegalQuestionsPolicy policy();

        @OrderColumn
        IPrimitive<Integer> orderInPolicy();

        @ToString
        IPrimitive<String> question();
    }

    IPrimitive<Boolean> enabled();

    @Owned
    IList<LegalQuestionPolicyItem> questions();
}
