/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.property.asset.Floorplan;

public class SelectFloorplanListServiceImpl extends AbstractListServiceImpl<Floorplan> implements SelectFloorplanListService {

    public SelectFloorplanListServiceImpl() {
        super(Floorplan.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    protected void enhanceListRetrieved(Floorplan entity, Floorplan dto) {
        Persistence.service().retrieve(dto.building());
        Persistence.service().retrieve(dto.building().complex());
    }
}
