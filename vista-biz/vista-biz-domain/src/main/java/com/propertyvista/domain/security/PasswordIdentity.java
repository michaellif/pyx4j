/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IPersonalIdentity;

@EmbeddedEntity
public interface PasswordIdentity extends IPersonalIdentity {

    @Override
    @RpcTransient
    @LogTransient
    @MemberColumn(name = "password")
    IPrimitive<String> number();

    @RpcTransient
    @LogTransient
    @Length(1024)
    IPrimitive<String> encrypted();

    @Override
    @Transient
    IPrimitive<String> obfuscatedNumber();

}
