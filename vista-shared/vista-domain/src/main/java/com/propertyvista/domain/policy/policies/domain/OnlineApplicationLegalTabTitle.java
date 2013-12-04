/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 3, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.ILocalizedEntity;
import com.propertyvista.domain.policy.policies.OnlineApplicationPolicy;

public interface OnlineApplicationLegalTabTitle extends ILocalizedEntity {
    @Detached
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    OnlineApplicationPolicy policy();

    IPrimitive<String> title();

}
