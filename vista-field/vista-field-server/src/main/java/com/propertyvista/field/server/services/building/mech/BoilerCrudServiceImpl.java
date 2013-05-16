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
package com.propertyvista.field.server.services.building.mech;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.field.rpc.services.building.mech.BoilerCrudService;

public class BoilerCrudServiceImpl extends AbstractCrudServiceDtoImpl<Boiler, BoilerDTO> implements BoilerCrudService {

    public BoilerCrudServiceImpl() {
        super(Boiler.class, BoilerDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }
}
