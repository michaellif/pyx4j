/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;

public class ApplicationWizardServiceImpl extends AbstractCrudServiceDtoImpl<OnlineApplication, OnlineApplicationDTO> implements ApplicationWizardService {

    public ApplicationWizardServiceImpl() {
        super(OnlineApplication.class, OnlineApplicationDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected OnlineApplicationDTO init(InitializationData initializationData) {

        OnlineApplicationDTO dto = EntityFactory.create(OnlineApplicationDTO.class);
        copyBOtoTO(ProspectPortalContext.getOnlineApplication(), dto);

        return dto;
    }
}
