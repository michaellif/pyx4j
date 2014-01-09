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
package com.propertyvista.crm.client.activity.policies.leaseterms;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalTermsPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LegalDocumentationPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalTermsPolicyDTO;

public class LegalTermsPolicyEditorActivity extends PolicyEditorActivityBase<LegalTermsPolicyDTO> implements
        LegalTermsPolicyEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LegalTermsPolicyEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LegalTermsPolicyEditorView.class),
                (AbstractPolicyCrudService<LegalTermsPolicyDTO>) GWT.create(LegalDocumentationPolicyCrudService.class),
                LegalTermsPolicyDTO.class);
    }
}
