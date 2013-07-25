/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.applicationdocumentation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyEdtiorView;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.ApplicationDocumentationPolicyCrudService;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;

public class ApplicationDocumentationPolicyEditorActivity extends PolicyEditorActivityBase<ApplicationDocumentationPolicyDTO> implements
        ApplicationDocumentationPolicyEdtiorView.Presenter {

    public ApplicationDocumentationPolicyEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(ApplicationDocumentationPolicyEdtiorView.class), GWT
                .<AbstractPolicyCrudService<ApplicationDocumentationPolicyDTO>> create(ApplicationDocumentationPolicyCrudService.class),
                ApplicationDocumentationPolicyDTO.class);
    }

}
