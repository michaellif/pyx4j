/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.emailtemplates;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.communication.CommunicationTemplateFacade;
import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.policies.emailtemplates.EmailTemplateTypeDTO;
import com.propertyvista.domain.policy.policies.emailtemplates.EmailTemplateTypesDTO;

public class EmailTemplateManagerServiceImpl implements EmailTemplateManagerService {

    @Override
    public void getTemplateDataObjects(AsyncCallback<EmailTemplateTypesDTO> callback) {
        EmailTemplateTypesDTO dto = EntityFactory.create(EmailTemplateTypesDTO.class);
        for (EmailTemplateType tplType : EmailTemplateType.values()) {
            EmailTemplateTypeDTO typeDto = EntityFactory.create(EmailTemplateTypeDTO.class);
            typeDto.type().setValue(tplType);
            typeDto.objectNames().setValue(ServerSideFactory.create(CommunicationTemplateFacade.class).getTemplateDataObjectSelection(tplType));
            dto.types().add(typeDto);
        }
        callback.onSuccess(dto);
    }

}
