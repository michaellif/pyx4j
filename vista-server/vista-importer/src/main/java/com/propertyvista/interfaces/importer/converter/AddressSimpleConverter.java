/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2014
 * @author vlads
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.server.CrudEntityBinder;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.interfaces.importer.model.AddressSimpleIO;

public class AddressSimpleConverter extends CrudEntityBinder<InternationalAddress, AddressSimpleIO> {

    public AddressSimpleConverter() {
        super(InternationalAddress.class, AddressSimpleIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.streetNumber(), boProto.streetNumber());
        bind(toProto.streetName(), boProto.streetName());
        bind(toProto.unitNumber(), boProto.suiteNumber());
        bind(toProto.city(), boProto.city());
        bind(toProto.provinceName(), boProto.province());
        bind(toProto.postalCode(), boProto.postalCode());
    }

    @Override
    public void copyBOtoTO(InternationalAddress dbo, AddressSimpleIO dto) {
        super.copyBOtoTO(dbo, dto);

        if (!dbo.country().isNull()) {
            dto.country().setValue(dbo.country().getValue().name());
        }
    }

    @Override
    public void copyTOtoBO(AddressSimpleIO dto, InternationalAddress dbo) {
        super.copyTOtoBO(dto, dbo);

        if (!dto.country().isNull()) {
            try {
                dbo.country().setValue(ISOCountry.forName(dto.country().getValue()));
            } catch (IllegalArgumentException ignore) {
            }
        }
    }
}
