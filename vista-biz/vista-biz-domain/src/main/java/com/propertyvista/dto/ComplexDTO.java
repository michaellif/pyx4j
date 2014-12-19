/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
@ExtendsBO
@SecurityEnabled
public interface ComplexDTO extends Complex {

    /** Contact info of complex's primary building */
    Building primaryBuilding();

//    BuildingContactInfo contactInfo();

    /** Address of complex's primary building */
    InternationalAddress address();

    // TODO additional properties:
    // * propertyManager/anyOtherKindOfManager - fetch from primary building
    // * some kind of financial information

    IList<DashboardMetadata> dashboards();
}
