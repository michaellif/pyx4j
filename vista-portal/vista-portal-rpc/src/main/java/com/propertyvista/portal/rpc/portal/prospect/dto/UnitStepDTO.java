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
package com.propertyvista.portal.rpc.portal.prospect.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;

@Transient
public interface UnitStepDTO extends IEntity {

    // Floorplan data:
    IPrimitive<String> floorplan();

    @Caption(name = "Bedrooms")
    IPrimitive<String> bedroomsAndDens();

    IPrimitive<String> bedrooms();

    IPrimitive<String> dens();

    IPrimitive<String> bathrooms();

    // Unit data:
    AddressSimple address();

    // We store the unit number separately from address because we use address in its simple incarnation
    IPrimitive<String> suiteNumber();

    MediaFile picture();

    // Service Catalog stuff:
    IList<BillableItem> agreedUtilities();

    IList<ProductItem> availableUtilities();

    IList<BillableItem> agreedPets();

    IList<ProductItem> availablePets();

    IList<BillableItem> agreedParking();

    IList<ProductItem> availableParking();

    IList<BillableItem> agreedStorage();

    IList<ProductItem> availableStorage();

    IList<BillableItem> agreedOther();

    IList<ProductItem> availableOther();

    IList<Concession> concessions();

    // Lease:
    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> unitRent();

    // Short/Id only version for reference only
    AptUnit unit();
}