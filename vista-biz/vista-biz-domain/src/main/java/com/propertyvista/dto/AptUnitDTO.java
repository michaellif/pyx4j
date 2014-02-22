/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsBO
public interface AptUnitDTO extends AptUnit {

    AddressStructured buildingLegalAddress();

    IPrimitive<String> buildingCode();

    IList<AptUnitServicePriceDTO> marketPrices();

    Lease lease();

    @Editor(type = EditorType.label)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> reservedUntil();

    IPrimitive<Boolean> isPresentInCatalog();

    IPrimitive<Boolean> isAvailableForExistingLease();
}