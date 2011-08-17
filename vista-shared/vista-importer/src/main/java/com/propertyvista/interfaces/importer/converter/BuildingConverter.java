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

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.BuildingIO;

public class BuildingConverter extends EntityDtoBinder<Building, BuildingIO> {

    public BuildingConverter() {
        super(Building.class, BuildingIO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.propertyCode(), dboProto.propertyCode());
        bind(dtoProto.legalName(), dboProto.info().name());
        bind(dtoProto.totalStoreys(), dboProto.info().totalStoreys());
        bind(dtoProto.residentialStoreys(), dboProto.info().residentialStoreys());
        bind(dtoProto.type(), dboProto.info().type());
        bind(dtoProto.shape(), dboProto.info().shape());
        bind(dtoProto.structureType(), dboProto.info().structureType());
        bind(dtoProto.structureBuildYear(), dboProto.info().structureBuildYear());
        bind(dtoProto.constructionType(), dboProto.info().constructionType());
        bind(dtoProto.foundationType(), dboProto.info().foundationType());
        bind(dtoProto.floorType(), dboProto.info().floorType());
        bind(dtoProto.landArea(), dboProto.info().landArea());
        bind(dtoProto.waterSupply(), dboProto.info().waterSupply());
        bind(dtoProto.centralAir(), dboProto.info().centralAir());
        bind(dtoProto.centralHeat(), dboProto.info().centralHeat());

        bind(dtoProto.addressCoordinates(), dboProto.info().address().location());

        bind(dtoProto.marketing().name(), dboProto.marketing().name());
        bind(dtoProto.marketing().description(), dboProto.marketing().description());
        //TODO  AdvertisingBlurb
        //bind(dtoProto.marketing().adBlurbs(), dboProto.marketing().adBlurbs(), new AdvertisingBlurbConverter());
    }
}
