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

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.interfaces.importer.model.PropertyPhoneIO;

public class PropertyContactConverter extends EntityBinder<PropertyContact, PropertyPhoneIO> {

    public PropertyContactConverter() {
        super(PropertyContact.class, PropertyPhoneIO.class, false);
    }

    @Override
    protected void bind() {
        // TODO PrimitiveConvertor
        //bind(dtoProto.type(), dboProto.type(), new TODOPrimitiveConvertor());
        bind(toProto.name(), boProto.name());
        bind(toProto.description(), boProto.description());
        bind(toProto.number(), boProto.phone());
        bind(toProto.email(), boProto.email());
        bind(toProto.visibility(), boProto.visibility());
        bind(toProto.description(), boProto.description());
    }

    @Override
    public void copyBOtoTO(PropertyContact dbo, PropertyPhoneIO dto) {
        super.copyBOtoTO(dbo, dto);

        // TODO PrimitiveConvertor
        if (!dbo.type().isNull()) {
            dto.type().setValue(dbo.type().getValue().name());
        }
    }

    @Override
    public void copyTOtoBO(PropertyPhoneIO dto, PropertyContact dbo) {
        super.copyTOtoBO(dto, dbo);

        // TODO PrimitiveConvertor
        if (!dto.type().isNull()) {
            try {
                dbo.type().setValue(PropertyContactType.valueOf(dto.type().getValue()));
            } catch (IllegalArgumentException ignore) {
            }
        }
    }
}
