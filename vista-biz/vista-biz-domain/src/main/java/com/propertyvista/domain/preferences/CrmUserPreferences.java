/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2014
 * @author smolka
 */
package com.propertyvista.domain.preferences;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.shared.IUserPreferences;

import com.propertyvista.domain.security.CrmUser;

public interface CrmUserPreferences extends IUserPreferences {

    @Owner
    @Detached
    @JoinColumn
    @ReadOnly
    CrmUser crmUser();

    @Owned
    @Detached(level = AttachLevel.Detached)
    CrmUserDeliveryPreferences deliveryPreferences();
}
