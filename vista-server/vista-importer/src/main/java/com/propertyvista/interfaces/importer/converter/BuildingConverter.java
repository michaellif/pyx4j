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

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class BuildingConverter extends EntityBinder<Building, BuildingIO> {

    public BuildingConverter() {
        super(Building.class, BuildingIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.propertyCode(), boProto.propertyCode());
        bind(toProto.externalId(), boProto.externalId());
        bind(toProto.legalName(), boProto.info().name());
        bind(toProto.totalStoreys(), boProto.info().totalStoreys());
        bind(toProto.residentialStoreys(), boProto.info().residentialStoreys());
        bind(toProto.type(), boProto.info().type());
        bind(toProto.shape(), boProto.info().shape());
        bind(toProto.structureType(), boProto.info().structureType());
        bind(toProto.structureBuildYear(), boProto.info().structureBuildYear());
        bind(toProto.constructionType(), boProto.info().constructionType());
        bind(toProto.foundationType(), boProto.info().foundationType());
        bind(toProto.floorType(), boProto.info().floorType());
        bind(toProto.landArea(), boProto.info().landArea());
        bind(toProto.waterSupply(), boProto.info().waterSupply());
        bind(toProto.centralAir(), boProto.info().centralAir());
        bind(toProto.centralHeat(), boProto.info().centralHeat());

        bind(toProto.addressCoordinates(), boProto.info().location());
        bind(toProto.website(), boProto.contacts().website());

        bind(toProto.propertyManager(), boProto.propertyManager().name());

        bind(toProto.complexName(), boProto.complex().name());
        bind(toProto.complexPrimary(), boProto.complexPrimary());

        bind(toProto.address(), boProto.info().address(), new AddressConverter());

        bind(toProto.phones(), boProto.contacts().propertyContacts(), new PropertyContactConverter());
        bind(toProto.contacts(), boProto.contacts().organizationContacts(), new OrganizationContactConverter());

        bind(toProto.marketing().visibility(), boProto.marketing().visibility());
        bind(toProto.marketing().name(), boProto.marketing().name());
        bind(toProto.marketing().description(), boProto.marketing().description());
    }
}
