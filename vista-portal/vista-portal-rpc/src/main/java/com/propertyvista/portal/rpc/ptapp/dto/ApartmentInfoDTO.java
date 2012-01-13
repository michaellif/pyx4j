/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 8, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.media.Media;

/**
 * For primary - Unit for lease info + price + term
 * For secondary/guarantor - Unit for lease info + price + term + all tenants + split info, add-ons
 * 
 */
@Transient
public interface ApartmentInfoDTO extends IEntity {

    // Floorplan data:
    IPrimitive<String> floorplan();

    @Caption(name = "Bedrooms")
    IPrimitive<String> bedroomsAndDens();

    IPrimitive<String> bedrooms();

    IPrimitive<String> dens();

    IPrimitive<String> bathrooms();

    // Unit data:
    AddressSimple address();

    IPrimitive<String> landlordName();

    // We store the unit number separately from address because we use address in its simple incarnation
    IPrimitive<String> suiteNumber();

    Media picture();

    // Service Catalog stuff:
    IList<ServiceItemType> includedUtilities();

    IList<ServiceItemType> externalUtilities();

    IList<ChargeItem> agreedUtilities();

    IList<ServiceItem> availableUtilities();

    IList<ChargeItem> agreedPets();

    IList<ServiceItem> availablePets();

    IList<ChargeItem> agreedParking();

    IList<ServiceItem> availableParking();

    IList<ChargeItem> agreedStorage();

    IList<ServiceItem> availableStorage();

    IList<ChargeItem> agreedOther();

    IList<ServiceItem> availableOther();

    IList<Concession> concessions();

    // Lease:
    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<Double> unitRent();

    // limits:
    IPrimitive<Integer> maxParkingSpots();

    IPrimitive<Integer> maxPets();
}
