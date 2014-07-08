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

import com.pyx4j.entity.shared.utils.SimpleEntityBinder;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.interfaces.importer.model.FloorplanIO;

public class FloorplanConverter extends SimpleEntityBinder<Floorplan, FloorplanIO> {

    public FloorplanConverter() {
        super(Floorplan.class, FloorplanIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.name(), boProto.name());
        bind(toProto.marketingName(), boProto.marketingName());
        bind(toProto.description(), boProto.description());
        bind(toProto.floorCount(), boProto.floorCount());
        bind(toProto.bedrooms(), boProto.bedrooms());
        bind(toProto.dens(), boProto.dens());
        bind(toProto.bathrooms(), boProto.bathrooms());
        bind(toProto.halfBath(), boProto.halfBath());
    }

}
