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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalDocumentationPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LegalDocumentationPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LegalDocumentationPolicyEditorActivity extends PolicyEditorActivityBase<LegalDocumentationPolicyDTO> implements
        LegalDocumentationPolicyEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LegalDocumentationPolicyEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LegalDocumentationPolicyEditorView.class), (AbstractPolicyCrudService<LegalDocumentationPolicyDTO>) GWT
                .create(LegalDocumentationPolicyCrudService.class), LegalDocumentationPolicyDTO.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<LegalDocumentationPolicyDTO> callback) {
        super.createNewEntity(new DefaultAsyncCallback<LegalDocumentationPolicyDTO>() {
            @Override
            public void onSuccess(LegalDocumentationPolicyDTO policy) {
                try {
                    policy.mainApplication().add(createNewLegalTerms());
                    policy.coApplication().add(createNewLegalTerms());
                    policy.guarantorApplication().add(createNewLegalTerms());
                    policy.lease().add(createNewLegalTerms());
                    policy.paymentAuthorization().add(createNewLegalTerms());
                    callback.onSuccess(policy);
                } catch (Throwable caught) {
                    callback.onFailure(caught);
                }
            }

        });
    }

    /** Create new empty terms descriptor with a empty content */
    private LegalTermsDescriptor createNewLegalTerms() {
        LegalTermsDescriptor termsDescriptor = EntityFactory.create(LegalTermsDescriptor.class);

        termsDescriptor.content().add(EntityFactory.create(LegalTermsContent.class));

        return termsDescriptor;
    }
}
