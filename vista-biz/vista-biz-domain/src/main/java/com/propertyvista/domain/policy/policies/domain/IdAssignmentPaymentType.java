/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;

/**
 * Enabled only in Yardi Mode
 */
public interface IdAssignmentPaymentType extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    IdAssignmentPolicy policy();

    IPrimitive<String> cashPrefix();

    IPrimitive<String> acceptedCheckPrefix();

    IPrimitive<String> echeckPrefix();

    IPrimitive<String> directBankingPrefix();

    IPrimitive<String> creditCardVisaPrefix();

    IPrimitive<String> creditCardMasterCardPrefix();

    IPrimitive<String> visaDebitPrefix();

    IPrimitive<String> autopayPrefix();

    IPrimitive<String> oneTimePrefix();

}
