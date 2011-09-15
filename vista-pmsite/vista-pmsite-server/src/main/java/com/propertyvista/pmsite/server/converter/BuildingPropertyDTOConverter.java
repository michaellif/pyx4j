/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class BuildingPropertyDTOConverter extends EntityDtoBinder<Building, PropertyDTO> {

    public BuildingPropertyDTOConverter() {
        super(Building.class, PropertyDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.address().street1(), dboProto.info().address().streetName());
        bind(dtoProto.address().street2(), dboProto.info().address().streetNumber());
        bind(dtoProto.address().city(), dboProto.info().address().city());
        bind(dtoProto.address().province(), dboProto.info().address().province());
        bind(dtoProto.address().country(), dboProto.info().address().country());
        bind(dtoProto.address().postalCode(), dboProto.info().address().postalCode());
        bind(dtoProto.description(), dboProto.marketing().description());
        bind(dtoProto.location(), dboProto.info().address().location());
    }

}
