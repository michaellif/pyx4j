/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.applicationdocumentation;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation.ApplicationDocumentationPolicyListerView;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;

public class ApplicationDocumentationPolicyListerActivity extends AbstractPrimeListerActivity<ApplicationDocumentationPolicyDTO> {

    public ApplicationDocumentationPolicyListerActivity(AppPlace place) {
        super(ApplicationDocumentationPolicyDTO.class, place, CrmSite.getViewFactory().getView(ApplicationDocumentationPolicyListerView.class));
    }

}
