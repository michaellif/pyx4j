/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import java.io.IOException;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.FinancialTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.FinancialTermsPolicyItem;

public class FinancialTermsPolicyPreloader extends AbstractPolicyPreloader<FinancialTermsPolicy> {

    public FinancialTermsPolicyPreloader() {
        super(FinancialTermsPolicy.class);
    }

    @Override
    protected FinancialTermsPolicy createPolicy(StringBuilder log) {
        FinancialTermsPolicy policy = EntityFactory.create(FinancialTermsPolicy.class);
        policy.tenantBillingTerms().set(createFinancialTermsPolicyItem("Billing and Refund Policy", "TenantBillingAndRefundPolicy.html"));
        policy.tenantPreauthorizedPaymentCardTerms().set(
                createFinancialTermsPolicyItem("Pre-Authorization Payment Agreement", "TenantPreAuthorizedPaymentCardTerms.html"));
        policy.tenantPreauthorizedPaymentECheckTerms().set(
                createFinancialTermsPolicyItem("Pre-Authorization Payment Agreement", "TenantPreAuthorizedPaymentECheckTerms.html"));
        return policy;
    }

    public FinancialTermsPolicyItem createFinancialTermsPolicyItem(String caption, String termsSourceFile) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, FinancialTermsPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found to create " + caption);
        }

        FinancialTermsPolicyItem legalDocument = EntityFactory.create(FinancialTermsPolicyItem.class);
        legalDocument.enabled().setValue(true);
        legalDocument.caption().setValue(caption);
        legalDocument.content().setValue(termsContent);
        return legalDocument;
    }

}
