/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.common.AbstractUserCredential;

/**
 * This entity only exists to build the joins with domain object that is stored in another maven module.
 */
@AbstractEntity(generateMetadata = false)
@RpcBlacklist
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CrmUserRolesLink extends AbstractUserCredential<CrmUser> {

    interface CrmRoleColumnId extends ColumnId {
    }

    ISet<CrmRole> roles();

}
