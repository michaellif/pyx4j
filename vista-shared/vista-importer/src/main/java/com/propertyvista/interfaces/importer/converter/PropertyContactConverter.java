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

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.interfaces.importer.model.PropertyPhoneIO;

public class PropertyContactConverter extends EntityDtoBinder<PropertyContact, PropertyPhoneIO> {

    public PropertyContactConverter() {
        super(PropertyContact.class, PropertyPhoneIO.class, false);
    }

    @Override
    protected void bind() {
        // TODO PrimitiveConvertor
        //bind(dtoProto.type(), dboProto.type(), new TODOPrimitiveConvertor());
        bind(dtoProto.name(), dboProto.name());
        bind(dtoProto.description(), dboProto.description());
        bind(dtoProto.number(), dboProto.phone());
        bind(dtoProto.email(), dboProto.email());
        bind(dtoProto.visibility(), dboProto.visibility());
        bind(dtoProto.description(), dboProto.description());
    }

    @Override
    public void copyDBOtoDTO(PropertyContact dbo, PropertyPhoneIO dto) {
        super.copyDBOtoDTO(dbo, dto);

        // TODO PrimitiveConvertor
        if (!dbo.type().isNull()) {
            dto.type().setValue(dbo.type().getValue().name());
        }
    }

    @Override
    public void copyDTOtoDBO(PropertyPhoneIO dto, PropertyContact dbo) {
        super.copyDTOtoDBO(dto, dbo);

        // TODO PrimitiveConvertor
        if (!dto.type().isNull()) {
            try {
                dbo.type().setValue(PropertyContactType.valueOf(dto.type().getValue()));
            } catch (IllegalArgumentException ignore) {
            }
        }
    }
}
