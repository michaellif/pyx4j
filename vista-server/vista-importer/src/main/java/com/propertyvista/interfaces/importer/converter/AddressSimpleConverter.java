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
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.interfaces.importer.model.AddressSimpleIO;

public class AddressSimpleConverter extends EntityBinder<AddressSimple, AddressSimpleIO> {

    public AddressSimpleConverter() {
        super(AddressSimple.class, AddressSimpleIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.street1(), boProto.street1());
        bind(toProto.street2(), boProto.street2());
        bind(toProto.city(), boProto.city());
        bind(toProto.provinceCode(), boProto.province().code());
        bind(toProto.country(), boProto.country().name());
        bind(toProto.postalCode(), boProto.postalCode());
    }

    @Override
    protected void onUpdateBOmember(AddressSimpleIO dto, AddressSimple dbo, IObject<?> dboM) {
        if (dboM == dbo.country().name()) {
            dbo.country().setPrimaryKey(null);
        } else if (dboM == dbo.province().code()) {
            dbo.province().setPrimaryKey(null);
            dbo.province().name().setValue(null);
        }
    }
}
