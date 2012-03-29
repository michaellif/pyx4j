/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;

public interface IdAssignmentItem extends IEntity {

    @I18n
    public enum IdTarget {

        @Translate("Building property code")
        propertyCode,

        lease,

        application,

        tenant,

        lead;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum IdAssignmentType {

        userEditable,

        userAssigned,

        generatedNumber,

        generatedAlphaNumeric;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @ReadOnly
    @Detached(level = AttachLevel.Detached)
    @JoinColumn
    IdAssignmentPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

    @ReadOnly
    IPrimitive<IdTarget> target();

    @NotNull
    @MemberColumn(name = "tp")
    IPrimitive<IdAssignmentType> type();

}
