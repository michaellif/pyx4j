/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2015
 * @author stanp
 */
package com.propertyvista.biz.communication.template.model;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeaseApplicationNotificationT extends IEntity {

    IPrimitive<String> buildingName();

    IPrimitive<String> buildingAddress();

    IPrimitive<String> tenantName();

    IPrimitive<String> unitNo();

    IPrimitive<String> leaseFrom();

    IPrimitive<String> leaseTo();

    IPrimitive<String> rentPrice();

    IPrimitive<String> leaseAppNo();

    IPrimitive<String> leaseUrl();

}
