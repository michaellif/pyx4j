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
import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;

/**
 * This object is created once and never updated.
 */
public interface CustomerCreditCheck extends IEntity {

    @I18n
    public enum CreditCheckResult {

        Accept,

        @Translate("Review")
        ReviewNoInformationAvalable,

        Review,

        Decline,

        Error;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    @Detached
    @Versioned
    @Owner
    @JoinColumn
    CustomerScreening screening();

    /**
     * TimeStamp used for selecting the last one and recording actual event
     */
    @Format("MM/dd/yyyy")
    @Timestamp(Update.Created)
    IPrimitive<Date> creditCheckDate();

    Employee createdBy();

    /**
     * TODO make a reference to versioned policy: remove @Owned add @Versioned
     */
    @Owned
    BackgroundCheckPolicy.BackgroundCheckPolicyV backgroundCheckPolicy();

    @ReadOnly
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amountChecked();

    //Link to CustomerCreditCheckTransaction
    IPrimitive<Key> transactionId();

    // --- Results from Equifax  ---

    IPrimitive<String> riskCode();

    @ToString(index = 0)
    IPrimitive<CreditCheckResult> creditCheckResult();

    @ReadOnly
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amountApproved();

    // Pointer To fullReport (CustomerCreditCheckReport) stored in second special schema...
    @Caption(name = "Full Report")
    IPrimitive<Key> creditCheckReport();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> reason();
}
