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
package com.propertyvista.server.domain.security;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;

@RpcBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = OnboardingUser.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface OnboardingUserCredential extends AbstractUserCredential<OnboardingUser> {

    @Override
    @Detached
    @MemberColumn(name = "usr")
    @ReadOnly
    OnboardingUser user();

    IPrimitive<VistaOnboardingBehavior> behavior();
}
