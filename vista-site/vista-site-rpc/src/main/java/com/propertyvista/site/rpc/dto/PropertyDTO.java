/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author michaellif
 */
package com.propertyvista.site.rpc.dto;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.contact.InternationalAddress;

//VladS Notes:  consider having this a table for performance
@Transient
public interface PropertyDTO extends IEntity {

    @Caption(name = "Address")
    @Owned
    InternationalAddress address();

    @Caption(name = "Location")
    IPrimitive<GeoPoint> location();

    IPrimitive<String> propertyCode();

    IPrimitive<Key> mainMedia();

    IList<FloorplanPropertyDTO> floorplansProperty();

    @EmbeddedEntity
    RangeGroup price();

    IPrimitive<String> description();

    IList<AmenityDTO> amenities();

    /**
     * Set on front-end by calling service that gets PropertyAvailabilityDTO
     */
    @RpcTransient
    IPrimitive<LogicalDate> availableForRent();
}
