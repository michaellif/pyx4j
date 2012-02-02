/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates.converter;

import com.pyx4j.entity.shared.utils.EntityDtoBinder;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.mail.templates.model.BuildingT;

public class BuildingTConvertor extends EntityDtoBinder<Building, BuildingT> {

    public BuildingTConvertor() {
        super(Building.class, BuildingT.class, false);
    }

    @Override
    protected void bind() {
        bind(dtoProto.propertyCode(), dboProto.propertyCode());
        //TODO all other
    }

}
