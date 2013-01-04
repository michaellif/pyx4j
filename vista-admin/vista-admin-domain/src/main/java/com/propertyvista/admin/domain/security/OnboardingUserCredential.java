/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.security;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.domain.security.common.AbstractUserCredential;

@RpcBlacklist
@GwtBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = OnboardingUser.class, namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface OnboardingUserCredential extends AbstractUserCredential<OnboardingUser> {

    IPrimitive<VistaOnboardingBehavior> behavior();

    @NotNull
    IPrimitive<String> onboardingAccountId();

    /**
     * Set to "o" + user.getPrimaryKey() when creating the User from Onboarding.
     * Or the value is copied from CrmUserCredential when creating this user base on CrmUser. Use UserAccessUtils.getCrmUserInterfaceUid()
     */
    IPrimitive<String> interfaceUid();

    @ReadOnly(allowOverrideNull = true)
    Pmc pmc();

    IPrimitive<Key> crmUser();

}
