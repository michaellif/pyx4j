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
package com.propertyvista.crm.rpc.services.policies.emailtemplates;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.policy.dto.emailtemplates.EmailTemplateTypesDTO;

public interface EmailTemplateManagerService extends IService {
    void getTemplateDataObjects(AsyncCallback<EmailTemplateTypesDTO> callback);
}
