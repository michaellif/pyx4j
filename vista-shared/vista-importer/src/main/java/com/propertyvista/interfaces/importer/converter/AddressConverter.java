/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.contact.Address;
import com.propertyvista.interfaces.importer.model.AddressIO;

public class AddressConverter extends EntityDtoBinder<Address, AddressIO> {

    public AddressConverter() {
        super(Address.class, AddressIO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.unitNumber(), dboProto.unitNumber());
        bind(dtoProto.streetNumber(), dboProto.streetNumber());
        bind(dtoProto.streetNumberSuffix(), dboProto.streetNumberSuffix());
        bind(dtoProto.streetName(), dboProto.streetName());
        bind(dtoProto.streetType(), dboProto.streetType());
        bind(dtoProto.streetDirection(), dboProto.streetDirection());
        bind(dtoProto.city(), dboProto.city());
        bind(dtoProto.county(), dboProto.county());
        bind(dtoProto.provinceCode(), dboProto.province().code());
        bind(dtoProto.countryName(), dboProto.country().name());
        bind(dtoProto.postalCode(), dboProto.postalCode());
    }

}
