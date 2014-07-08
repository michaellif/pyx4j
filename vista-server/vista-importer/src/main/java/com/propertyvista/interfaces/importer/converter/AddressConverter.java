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

import com.pyx4j.entity.server.CrudEntityBinder;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.interfaces.importer.model.AddressIO;

public class AddressConverter extends CrudEntityBinder<InternationalAddress, AddressIO> {

    public AddressConverter() {
        super(InternationalAddress.class, AddressIO.class, false);
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
    public void copyBOtoTO(InternationalAddress dbo, AddressIO dto) {
        super.copyBOtoTO(dbo, dto);

        if (!dbo.country().isNull()) {
            dto.countryName().setValue(dbo.country().getValue().name());
        }
    }

    @Override
    public void copyTOtoBO(AddressIO dto, InternationalAddress dbo) {
        super.copyTOtoBO(dto, dbo);

        if (!dto.countryName().isNull()) {
            try {
                dbo.country().setValue(ISOCountry.forName(dto.countryName().getValue()));
            } catch (IllegalArgumentException ignore) {
            }
        }
    }
}
