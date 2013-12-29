/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 16, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface YardiInterfacePolicyChargeCodeIgnore extends IEntity {

    @Owner
    @ReadOnly
    @Detached
    @NotNull
    @JoinColumn
    @MemberColumn(notNull = true)
    YardiInterfacePolicy policy();

    // TODO maybe a link to yardi account

    @NotNull
    @ToString
    IPrimitive<String> yardiChargeCode();
}
