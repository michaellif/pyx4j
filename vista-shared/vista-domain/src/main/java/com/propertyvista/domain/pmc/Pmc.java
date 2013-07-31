/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.fee.PmcEquifaxFee;
import com.propertyvista.domain.pmc.payment.CustomerCreditCheckTransaction;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.domain.settings.PmcYardiCredential;

@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
@Caption(name = "PMC")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface Pmc extends IEntity {

    public enum PmcStatus {
        Created, Activating, Active, Suspended, Cancelled, Terminated
    }

    @NotNull
    IPrimitive<PmcStatus> status();

    @NotNull
    @ToString
    @Caption(name = "Company name")
    IPrimitive<String> name();

    @NotNull
    @ReadOnly
    @Length(63)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> namespace();

    @NotNull
    @Length(63)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @Caption(name = "DNS name")
    IPrimitive<String> dnsName();

    @Owned
    IList<PmcDnsName> dnsNameAliases();

    @Owned
    PmcVistaFeatures features();

    @Owned
    @Detached(level = AttachLevel.Detached)
    PmcEquifaxInfo equifaxInfo();

    @Owned
    @Detached(level = AttachLevel.Detached)
    PmcEquifaxFee equifaxFee();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<CustomerCreditCheckTransaction> creditCheckTransaction();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<PmcMerchantAccountIndex> merchantAccounts();

    @Owned
    @Detached(level = AttachLevel.Detached)
    PmcPaymentTypeInfo paymentTypeInfo();

    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    @Owned
    IList<PmcYardiCredential> yardiCredentials();

    /**
     * Initialized from OnboardingUserCredential.interfaceUid
     */
    IPrimitive<String> interfaceUidBase();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    IPrimitive<Date> created();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    IPrimitive<Date> termination();

    /**
     * Used by DB upgrade process to ensure that schema is upgraded only once.
     * String "1.2.3" or "1.2.3.4"
     */
    IPrimitive<String> schemaVersion();

    /**
     * Executed UpgradeSteps for each major version.
     */
    IPrimitive<Integer> schemaDataUpgradeSteps();
}
