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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.interfaces.importer.model.AddressIO;

public class AddressConverter extends EntityBinder<AddressStructured, AddressIO> {

    public AddressConverter() {
        super(AddressStructured.class, AddressIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.unitNumber(), boProto.suiteNumber());
        bind(toProto.streetNumber(), boProto.streetNumber());
        bind(toProto.streetNumberSuffix(), boProto.streetNumberSuffix());
        bind(toProto.streetName(), boProto.streetName());
        bind(toProto.streetType(), boProto.streetType());
        bind(toProto.streetDirection(), boProto.streetDirection());
        bind(toProto.city(), boProto.city());
        bind(toProto.county(), boProto.county());
        bind(toProto.provinceCode(), boProto.province().code());
        bind(toProto.countryName(), boProto.country().name());
        bind(toProto.postalCode(), boProto.postalCode());
    }

    @Override
    protected void onUpdateBOmember(AddressIO dto, AddressStructured dbo, IObject<?> dboM) {
        if (dboM == dbo.country().name()) {
            dbo.country().setPrimaryKey(null);
        } else if (dboM == dbo.province().code()) {
            dbo.province().setPrimaryKey(null);
            dbo.province().name().setValue(null);
        }
    }
}
