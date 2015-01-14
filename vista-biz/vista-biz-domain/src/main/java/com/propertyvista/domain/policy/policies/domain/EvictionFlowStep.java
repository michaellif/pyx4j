/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;

public interface EvictionFlowStep extends IEntity {

    public enum OntarioFlow {
        N4,

        L1,

        HearingDate,

        Order(true),

        Sheriff(true),

        SetAside(true),

        RequestToReviewOrder(true),

        StayOrder(true);

        public final boolean flexible;

        private OntarioFlow() {
            this(false);
        }

        private OntarioFlow(boolean flexible) {
            this.flexible = flexible;
        }
    }

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @ReadOnly
    @Detached
    @Indexed(group = { "n,1" })
    EvictionFlowPolicy policy();

    @ToString
    @Indexed(group = { "n,2" })
    IPrimitive<String> name();

    IPrimitive<String> description();

    IPrimitive<Boolean> isFlexible();
}
