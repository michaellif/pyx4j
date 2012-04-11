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

import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseTermsPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LeaseTermsPolicyEditorActivity extends PolicyEditorActivityBase<LeaseTermsPolicyDTO> implements LeaseTermsPolicyEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LeaseTermsPolicyEditorActivity(CrudAppPlace place) {
        super(place, PolicyViewFactory.instance(LeaseTermsPolicyEditorView.class), (AbstractPolicyCrudService<LeaseTermsPolicyDTO>) GWT
                .create(LeaseTermsPolicyCrudService.class), LeaseTermsPolicyDTO.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<LeaseTermsPolicyDTO> callback) {
        super.createNewEntity(new DefaultAsyncCallback<LeaseTermsPolicyDTO>() {

            @Override
            public void onSuccess(LeaseTermsPolicyDTO policy) {
                try {
                    policy.tenantSummaryTerms().add(createNewLegalTerms());
                    policy.oneTimePaymentTerms().set(createNewLegalTerms());
                    policy.recurrentPaymentTerms().set(createNewLegalTerms());
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
