/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalVistaTermsService;
import com.propertyvista.server.TaskRunner;

public class PortalVistaTermsServiceImpl implements PortalVistaTermsService {

    @Override
    public void getPortalTerms(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPortalTermsAndConditions);
    }

    @Override
    public void getTenantBillingTerms(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantBillingTerms);
    }

    @Override
    public void getTenantPreauthorizedPaymentTerms(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPreAuthorizedPaymentTerms);
    }

    @Override
    public void getResidentPortalCcPolicy(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPreAuthorizedPaymentCreditCardTerms);
    }

    @Override
    public void getResidentPortalConvenienceFeeTerms(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPaymentConvenienceFeeTerms);
    }

    @Override
    public void getProspectApplicantTerms(AsyncCallback<String> callback) {
        getVistaTerms(callback, VistaTerms.Target.ApplicantTermsAndConditions);
    }

    @Override
    public void getProspectRentalCriteriaGuidelines(AsyncCallback<String> callback) {
        // TODO use PmcTermsPolicy
        getVistaTerms(callback, VistaTerms.Target.ApplicantTermsAndConditions);
    }

    private void getVistaTerms(AsyncCallback<String> callback, final VistaTerms.Target target) {
        String terms = TaskRunner.runInOperationsNamespace(new Callable<String>() {
            @Override
            public String call() {
                String result = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                List<VistaTerms> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    VistaTerms terms = list.get(0);
                    for (LegalDocument doc : terms.version().document()) {
                        if (doc.locale().getValue().getLanguage().startsWith("en")) {
                            result = doc.content().getValue();
                            break;
                        }
                    }
                }
                return result;
            }
        });
        if (terms == null) {
            throw new RuntimeException("Terms not found");
        }
        callback.onSuccess(terms);
    }

}
