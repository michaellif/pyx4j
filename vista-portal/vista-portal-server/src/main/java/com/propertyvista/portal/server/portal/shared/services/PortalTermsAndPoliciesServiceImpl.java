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
import java.util.Vector;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsPolicyItem;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalTermsAndPoliciesService;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.shared.rpc.LegalTermTO;

public class PortalTermsAndPoliciesServiceImpl implements PortalTermsAndPoliciesService {

    @Override
    public void getTerm(AsyncCallback<LegalTermTO> callback, TermsAndPoliciesType type) {
        switch (type) {
        case PMCResidentPortalTermsAndConditions:
            getPMCTerm(callback, getPMCResidentPortalTermsAndConditionsPolicyItem());
            break;
        case PMCResidentPortalPrivacyPolicy:
            getPMCTerm(callback, getPMCResidentPortalPrivacyPolicyPolicyItem());
            break;
        case PMCProspectPortalTermsAndConditions:
            getPMCTerm(callback, getPMCProspectPortalTermsAndConditionsPolicyItem());
            break;
        case PMCProspectPortalPrivacyPolicy:
            getPMCTerm(callback, getPMCProspectPortalPrivacyPolicyPolicyItem());
            break;
        case PVProspectPortalPrivacyPolicy:
            getVistaTerm(callback, VistaTerms.Target.ProspectPortalPrivacyPolicy);
            break;
        case PVProspectPortalTermsAndConditions:
            getVistaTerm(callback, VistaTerms.Target.ProspectPortalTermsAndConditions);
            break;
        case PVResidentPortalPrivacyPolicy:
            getVistaTerm(callback, VistaTerms.Target.ResidentPortalPrivacyPolicy);
            break;
        case PVResidentPortalTermsAndConditions:
            getVistaTerm(callback, VistaTerms.Target.ResidentPortalTermsAndConditions);
            break;
        case ResidentPortalWebPaymentFeeTerms:
            getVistaTerm(callback, VistaTerms.Target.TenantPaymentWebPaymentFeeTerms);
            break;
        case TenantBillingTerms:
            getVistaTerm(callback, VistaTerms.Target.TenantBillingTerms);
            break;
        case TenantPreauthorizedPaymentCardTerms:
            getVistaTerm(callback, VistaTerms.Target.TenantPreAuthorizedPaymentCardTerms);
            break;
        case TenantPreauthorizedPaymentECheckTerms:
            getVistaTerm(callback, VistaTerms.Target.TenantPreAuthorizedPaymentECheckTerms);
            break;
        case TenantSurePreAuthorizedPaymentsAgreement:
            getVistaTerm(callback, VistaTerms.Target.TenantSurePreAuthorizedPaymentsAgreement);
            break;
        }
    }

    @Override
    public void getTermCaptions(AsyncCallback<Vector<String>> callback, Vector<TermsAndPoliciesType> types) {
        Vector<String> captions = new Vector<String>();
        for (TermsAndPoliciesType type : types) {
            captions.add(getTermCaption(type));
        }
        callback.onSuccess(captions);
    }

    private String getTermCaption(TermsAndPoliciesType type) {
        switch (type) {
        case PMCResidentPortalTermsAndConditions:
            return getPMCTermCaption(getPMCResidentPortalTermsAndConditionsPolicyItem());
        case PMCResidentPortalPrivacyPolicy:
            return getPMCTermCaption(getPMCResidentPortalPrivacyPolicyPolicyItem());
        case PMCProspectPortalTermsAndConditions:
            return getPMCTermCaption(getPMCProspectPortalTermsAndConditionsPolicyItem());
        case PMCProspectPortalPrivacyPolicy:
            return getPMCTermCaption(getPMCProspectPortalPrivacyPolicyPolicyItem());
        case PVProspectPortalPrivacyPolicy:
            return getVistaTermCaption(VistaTerms.Target.ProspectPortalPrivacyPolicy);
        case PVProspectPortalTermsAndConditions:
            return getVistaTermCaption(VistaTerms.Target.ProspectPortalTermsAndConditions);
        case PVResidentPortalPrivacyPolicy:
            return getVistaTermCaption(VistaTerms.Target.ResidentPortalPrivacyPolicy);
        case PVResidentPortalTermsAndConditions:
            return getVistaTermCaption(VistaTerms.Target.ResidentPortalTermsAndConditions);
        case ResidentPortalWebPaymentFeeTerms:
            return getVistaTermCaption(VistaTerms.Target.TenantPaymentWebPaymentFeeTerms);
        case TenantBillingTerms:
            return getVistaTermCaption(VistaTerms.Target.TenantBillingTerms);
        case TenantPreauthorizedPaymentCardTerms:
            return getVistaTermCaption(VistaTerms.Target.TenantPreAuthorizedPaymentCardTerms);
        case TenantPreauthorizedPaymentECheckTerms:
            return getVistaTermCaption(VistaTerms.Target.TenantPreAuthorizedPaymentECheckTerms);
        case TenantSurePreAuthorizedPaymentsAgreement:
            return getVistaTermCaption(VistaTerms.Target.TenantSurePreAuthorizedPaymentsAgreement);
        }
        return null;
    }

    private LegalTermsPolicy getLegalTermsPolicy() {
        return ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)), LegalTermsPolicy.class);
    }

    private void getPMCTerm(AsyncCallback<LegalTermTO> callback, LegalTermsPolicyItem policyItem) {
        LegalTermTO result = null;
        if (policyItem.enabled().isBooleanTrue()) {
            result = EntityFactory.create(LegalTermTO.class);
            result.caption().setValue(policyItem.caption().getValue());
            result.content().setValue(policyItem.content().getValue());
        }
        callback.onSuccess(result);
    }

    private String getPMCTermCaption(LegalTermsPolicyItem policyItem) {
        if (policyItem.enabled().isBooleanTrue()) {
            return policyItem.caption().getValue();
        }
        return null;
    }

    private LegalTermsPolicyItem getPMCProspectPortalTermsAndConditionsPolicyItem() {
        return getLegalTermsPolicy().prospectPortalTermsAndConditions();
    }

    private LegalTermsPolicyItem getPMCProspectPortalPrivacyPolicyPolicyItem() {
        return getLegalTermsPolicy().prospectPortalPrivacyPolicy();
    }

    private LegalTermsPolicyItem getPMCResidentPortalTermsAndConditionsPolicyItem() {
        return getLegalTermsPolicy().residentPortalTermsAndConditions();
    }

    private LegalTermsPolicyItem getPMCResidentPortalPrivacyPolicyPolicyItem() {
        return getLegalTermsPolicy().residentPortalPrivacyPolicy();
    }

    private void getVistaTerm(AsyncCallback<LegalTermTO> callback, final VistaTerms.Target target) {
        LegalTermTO terms = TaskRunner.runInOperationsNamespace(new Callable<LegalTermTO>() {
            @Override
            public LegalTermTO call() {
                LegalTermTO result = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                List<VistaTerms> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    VistaTerms terms = list.get(0);
                    for (LegalDocument doc : terms.version().document()) {
                        if (doc.locale().getValue().getLanguage().startsWith("en")) {
                            result = EntityFactory.create(LegalTermTO.class);
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

    private String getVistaTermCaption(final VistaTerms.Target target) {
        String caption = TaskRunner.runInOperationsNamespace(new Callable<String>() {
            @Override
            public String call() {
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                List<VistaTerms> list = Persistence.service().query(criteria);
                if (!list.isEmpty()) {
                    VistaTerms terms = list.get(0);
                    for (LegalDocument doc : terms.version().document()) {
                        if (doc.locale().getValue().getLanguage().startsWith("en")) {
                            return terms.version().caption().getValue();
                        }
                    }
                }
                return null;
            }
        });

        return caption;
    }
}
