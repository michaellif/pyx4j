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
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.common.AbstractUserCredential;

@RpcBlacklist(generateMetadata = false)
@RpcTransient
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = CustomerUser.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CustomerUserCredential extends AbstractUserCredential<CustomerUser> {

}
