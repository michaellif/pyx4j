/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 3, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;

import com.propertyvista.domain.policy.policies.OnlineApplicationLegalPolicy;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@ToStringFormat("{0}")
public interface OnlineApplicationLegalTerm extends IEntity {

    public enum TargetRole {
        Applicant, Guarantor, All;

        public boolean matchesApplicationRole(LeaseTermParticipant.Role appRole) {
            if (this == TargetRole.All) {
                return true;
            }

            switch (appRole) {
            case Applicant:
            case CoApplicant:
                return this == TargetRole.Applicant;
            case Guarantor:
                return this == TargetRole.Guarantor;
            }
            return false;
        }
    }

    @Detached
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    OnlineApplicationLegalPolicy policy();

    @ToString(index = 0)
    IPrimitive<String> title();

    @NotNull
    @Editor(type = Editor.EditorType.richtextarea)
    @Length(48000)
    IPrimitive<String> body();

    @NotNull
    IPrimitive<SignatureFormat> signatureFormat();

    @NotNull
    IPrimitive<TargetRole> applyToRole();

    @OrderColumn
    IPrimitive<Integer> orderId();
}
