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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

@RpcBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = CrmUser.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CrmUserCredential extends AbstractUserCredential<CrmUser> {

    IPrimitive<Boolean> accessAllBuildings();

    @MemberColumn(name = "rls")
    ISet<CrmRole> roles();

    IPrimitive<Key> onboardingUser();

    /**
     * The value is copied from onboardingUser when creating this user base on onboardingUser
     */
    IPrimitive<String> interfaceUid();
}
