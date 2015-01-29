/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2015
 * @author michaellif
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.BroadcastTemplateCrudService;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateCrudServiceImpl extends AbstractCrudServiceImpl<BroadcastTemplate> implements BroadcastTemplateCrudService {

    public BroadcastTemplateCrudServiceImpl() {
        super(BroadcastTemplate.class);
    }

    @Override
    protected BroadcastTemplate init(InitializationData initializationData) {

        BroadcastTemplate result = super.init(initializationData);
        result.name().setValue(((BroadcastTemplateCrudService.BroadcastTemplateInitializationData) initializationData).name().getValue());
        result.audienceType().setValue(((BroadcastTemplateCrudService.BroadcastTemplateInitializationData) initializationData).audienceType().getValue());
        result.messageType().setValue(((BroadcastTemplateCrudService.BroadcastTemplateInitializationData) initializationData).messageType().getValue());

        return result;

    }

}
