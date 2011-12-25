/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceCrudServiceImpl extends GenericCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements MaintenanceCrudService {

    public MaintenanceCrudServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    protected void enhanceDTO(MaintenanceRequest in, MaintenanceRequestDTO dto, boolean fromList) {
        Persistence.service().retrieve(dto.tenant());
        Persistence.service().retrieve(dto.issueClassification());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject().issueElement());
    }
}
