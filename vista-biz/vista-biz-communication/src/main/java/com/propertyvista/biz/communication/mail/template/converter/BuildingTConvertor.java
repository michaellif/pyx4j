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
package com.propertyvista.biz.communication.mail.template.converter;

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingTConvertor extends EntityBinder<Building, BuildingT> {

    public BuildingTConvertor() {
        super(Building.class, BuildingT.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.PropertyCode(), boProto.propertyCode());
        //TODO all other
    }

}
