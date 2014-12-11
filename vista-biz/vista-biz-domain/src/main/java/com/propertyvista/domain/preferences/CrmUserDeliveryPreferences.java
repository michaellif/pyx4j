/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.domain.preferences;

import java.io.Serializable;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface CrmUserDeliveryPreferences extends IEntity {

    public enum DeliveryType implements Serializable {

        Daily, Weekly, Individual, DoNotSend
    }

    @Owner
    @Detached
    @JoinColumn
    @ReadOnly
    CrmUserPreferences userPreferences();

    IPrimitive<DeliveryType> promotionalDelivery();

    IPrimitive<DeliveryType> informationalDelivery();
}
