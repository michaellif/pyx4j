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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
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

    IPrimitive<String> bedrooms();

    IPrimitive<String> bathrooms();

    // Unit data:
    AddressStructured address();

    Media picture();

    // Service Catalogue stuff:
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
    IPrimitive<Double> unitRent();
}
