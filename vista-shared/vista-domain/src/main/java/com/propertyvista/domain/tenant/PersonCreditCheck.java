/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;

/**
 * This object is created once and never updated.
 */
public interface PersonCreditCheck extends IEntity {

    //Suggested Decision
    @I18n
    public enum CreditCheckResult {

        Accept,

        SoftDecline,

        Decline;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @Detached
    @Versioned
    @Owner
    @JoinColumn
    PersonScreening screening();

    @Format("MM/dd/yyyy")
    // TimeStamp ?
    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> creditCheckDate();

    /**
     * TODO make a reference to versioned policy: remove @Owned add @Versioned
     */
    @Owned
    BackgroundCheckPolicy.BackgroundCheckPolicyV backgroundCheckPolicy();

    @Format("#0.00")
    @ReadOnly
    IPrimitive<BigDecimal> amountCheked();

    // --- Results from Equifax  ---

    @Format("#0.00")
    @ReadOnly
    IPrimitive<BigDecimal> amountApproved();

    // TODO pointerTo fullReportSored in second special schema...
    IPrimitive<Key> creditCheckReport();

    IPrimitive<CreditCheckResult> creditCheckResult();

    IPrimitive<String> declineReason();

}
