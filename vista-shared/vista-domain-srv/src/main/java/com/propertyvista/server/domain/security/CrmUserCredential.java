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
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;

@RpcBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = CrmUser.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CrmUserCredential extends AbstractUserCredential<CrmUser, VistaCrmBehavior> {

    @Override
    @Detached
    @MemberColumn(name = "usr")
    CrmUser user();

    @Override
    @Deprecated
    IPrimitiveSet<VistaCrmBehavior> behaviors();

    @MemberColumn(name = "rls")
    IList<CrmRole> roles();
}
