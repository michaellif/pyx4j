/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.pmc;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.AbstractMerchantAccount;

@Table(prefix = "admin", namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface OboardingMerchantAccount extends AbstractMerchantAccount {

    @ReadOnly
    @Owner
    @JoinColumn
    Pmc pmc();

    @NotNull
    IPrimitive<String> onboardingAccountId();

    // aka external id for updates from onboarding
    IPrimitive<String> onboardingBankAccountId();

    IPrimitive<Key> merchantAccountKey();

}
