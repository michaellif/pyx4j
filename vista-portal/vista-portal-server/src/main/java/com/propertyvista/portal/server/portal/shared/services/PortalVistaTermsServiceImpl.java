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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalVistaTermsService;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.shared.rpc.LegalTermsTO;

public class PortalVistaTermsServiceImpl implements PortalVistaTermsService {

    @Override
    public void getPortalTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.VistaPortalTermsAndConditions);
    }

    @Override
    public void getTenantBillingTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantBillingTerms);
    }

    @Override
    public void getTenantPreauthorizedPaymentECheckTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPreAuthorizedPaymentECheckTerms);
    }

    @Override
    public void getTenantPreauthorizedPaymentCardTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPreAuthorizedPaymentCardTerms);
    }

    @Override
    public void getResidentPortalWebPaymentFeeTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantPaymentWebPaymentFeeTerms);
    }

    @Override
    public void getTenantSurePreAuthorizedPaymentsAgreement(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.TenantSurePreAuthorizedPaymentsAgreement);
    }

    @Override
    public void getProspectApplicantTerms(AsyncCallback<LegalTermsTO> callback) {
        getVistaTerms(callback, VistaTerms.Target.ApplicantTermsAndConditions);
    }

    @Override
    public void getProspectRentalCriteriaGuidelines(AsyncCallback<LegalTermsTO> callback) {
        LegalTermsPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)), LegalTermsPolicy.class);

        LegalTermsTO result = EntityFactory.create(LegalTermsTO.class);
        result.caption().setValue(policy.residentPortalTermsAndConditions().caption().getValue());
        result.content().setValue(policy.residentPortalTermsAndConditions().content().getValue());
        callback.onSuccess(result);
    }

    private void getVistaTerms(AsyncCallback<LegalTermsTO> callback, final VistaTerms.Target target) {
        LegalTermsTO terms = TaskRunner.runInOperationsNamespace(new Callable<LegalTermsTO>() {
            @Override
            public LegalTermsTO call() {
                LegalTermsTO result = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                List<VistaTerms> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    VistaTerms terms = list.get(0);
                    for (LegalDocument doc : terms.version().document()) {
                        if (doc.locale().getValue().getLanguage().startsWith("en")) {
                            result = EntityFactory.create(LegalTermsTO.class);
                            result.caption().setValue(terms.version().caption().getValue());
                            result.content().setValue(doc.content().getValue());
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
