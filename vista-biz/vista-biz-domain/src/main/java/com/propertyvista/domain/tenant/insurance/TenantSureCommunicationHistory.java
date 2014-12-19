/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2014
 * @author vlads
 */
package com.propertyvista.domain.tenant.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface TenantSureCommunicationHistory extends IEntity {

    public enum TenantSureMessageType {

        PaymentNotProcessed,

        NoticeOfCancellation,

        AutomaticRenewal,

        ExpiringCreaditCard,

        PaymentsResumed,
    }

    @Owner
    @JoinColumn
    @MemberColumn(notNull = true)
    @Indexed
    TenantSureInsurancePolicy insurance();

    IPrimitive<TenantSureMessageType> messageType();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<LogicalDate> created();

    IPrimitive<Boolean> sent();

    IPrimitive<String> messageDate();

    IPrimitive<String> messageId();
}
