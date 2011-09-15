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

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.portal.domain.dto.AmenityDTO;

public class BuildingAmenityAmenityDTOConverter extends EntityDtoBinder<BuildingAmenity, AmenityDTO> {

    public BuildingAmenityAmenityDTOConverter() {
        super(BuildingAmenity.class, AmenityDTO.class, false);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.name());
    }

}
