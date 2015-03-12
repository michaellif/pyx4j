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
 */
package com.propertyvista.portal.server.portal.shared.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.FinancialTermsPolicy;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.TermsPolicyItem;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.portal.rpc.portal.shared.services.PortalTermsAndPoliciesService;
import com.propertyvista.server.VistaTermsUtils;
import com.propertyvista.shared.rpc.LegalTermTO;

public class PortalTermsAndPoliciesServiceImpl implements PortalTermsAndPoliciesService {

    @Override
    public void getTerm(AsyncCallback<LegalTermTO> callback, TermsAndPoliciesType type) {
        switch (type) {
        case PMCResidentPortalTermsAndConditions:
            getPMCTerm(callback, getLegalTermsPolicy().residentPortalTermsAndConditions());
            break;
        case PMCResidentPortalPrivacyPolicy:
            getPMCTerm(callback, getLegalTermsPolicy().residentPortalPrivacyPolicy());
            break;
        case PMCProspectPortalTermsAndConditions:
            getPMCTerm(callback, getLegalTermsPolicy().prospectPortalTermsAndConditions());
            break;
        case PMCProspectPortalPrivacyPolicy:
            getPMCTerm(callback, getLegalTermsPolicy().prospectPortalPrivacyPolicy());
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
            getPMCTerm(callback, getFinancialTermsPolicy().billingTerms());
            break;
        case TenantPreauthorizedPaymentCardTerms:
            getPMCTerm(callback, getFinancialTermsPolicy().preauthorizedPaymentCardTerms());
            break;
        case TenantPreauthorizedPaymentECheckTerms:
            getPMCTerm(callback, getFinancialTermsPolicy().preauthorizedPaymentECheckTerms());
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
            return getPMCTermCaption(getLegalTermsPolicy().residentPortalTermsAndConditions());
        case PMCResidentPortalPrivacyPolicy:
            return getPMCTermCaption(getLegalTermsPolicy().residentPortalPrivacyPolicy());
        case PMCProspectPortalTermsAndConditions:
            return getPMCTermCaption(getLegalTermsPolicy().prospectPortalTermsAndConditions());
        case PMCProspectPortalPrivacyPolicy:
            return getPMCTermCaption(getLegalTermsPolicy().prospectPortalPrivacyPolicy());
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
            return getPMCTermCaption(getFinancialTermsPolicy().billingTerms());
        case TenantPreauthorizedPaymentCardTerms:
            return getPMCTermCaption(getFinancialTermsPolicy().preauthorizedPaymentCardTerms());
        case TenantPreauthorizedPaymentECheckTerms:
            return getPMCTermCaption(getFinancialTermsPolicy().preauthorizedPaymentECheckTerms());
        case TenantSurePreAuthorizedPaymentsAgreement:
            return getVistaTermCaption(VistaTerms.Target.TenantSurePreAuthorizedPaymentsAgreement);
        }
        return null;
    }

    private LegalTermsPolicy getLegalTermsPolicy() {
        return ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)), LegalTermsPolicy.class);
    }

    private FinancialTermsPolicy getFinancialTermsPolicy() {
        return ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)), FinancialTermsPolicy.class);
    }

    private void getPMCTerm(AsyncCallback<LegalTermTO> callback, TermsPolicyItem policyItem) {
        LegalTermTO term = EntityFactory.create(LegalTermTO.class);
        if (policyItem.enabled().getValue(false)) {
            term.caption().setValue(policyItem.caption().getValue());
            term.content().setValue(policyItem.content().getValue());
        } else {
            term.caption().setValue("Terms is disabled");
            term.content().setValue("Please contact Office for details");
        }
        callback.onSuccess(term);
    }

    private String getPMCTermCaption(TermsPolicyItem policyItem) {
        if (policyItem.enabled().getValue(false)) {
            return policyItem.caption().getValue();
        }
        return "Terms is disabled";
    }

    private void getVistaTerm(AsyncCallback<LegalTermTO> callback, final VistaTerms.Target target) {
        LegalTermTO result = null;

        VistaTerms terms = VistaTermsUtils.retrieveVistaTerms(target);
        for (LegalDocument doc : terms.version().document()) {
            if (doc.locale().getValue().getLanguage().startsWith("en")) {
                result = EntityFactory.create(LegalTermTO.class);
                result.caption().setValue(terms.version().caption().getValue());
                result.content().setValue(doc.content().getValue());
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException("Terms not found");
        }
        callback.onSuccess(result);
    }

    private String getVistaTermCaption(final VistaTerms.Target target) {
        VistaTerms terms = VistaTermsUtils.retrieveVistaTerms(target);
        for (LegalDocument doc : terms.version().document()) {
            if (doc.locale().getValue().getLanguage().startsWith("en")) {
                return terms.version().caption().getValue();
            }
        }
        return null;
    }
}
