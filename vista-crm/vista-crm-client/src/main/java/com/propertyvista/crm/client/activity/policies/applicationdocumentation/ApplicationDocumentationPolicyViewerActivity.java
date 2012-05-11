/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.applicationdocumentation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyViewerViewImpl;
import com.propertyvista.crm.rpc.services.policies.policy.ApplicationDocumentationPolicyCrudService;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;

public class ApplicationDocumentationPolicyViewerActivity extends CrmViewerActivity<ApplicationDocumentationPolicyDTO> implements
        ApplicationDocumentationPolicyViewerView.Presenter {

    public ApplicationDocumentationPolicyViewerActivity(CrudAppPlace place) {
        // TODO take the view from pool
        super(place, new ApplicationDocumentationPolicyViewerViewImpl(), (AbstractCrudService<ApplicationDocumentationPolicyDTO>) GWT
                .create(ApplicationDocumentationPolicyCrudService.class));
    }

}
