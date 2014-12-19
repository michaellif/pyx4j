/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

import com.propertyvista.crm.client.ui.wizard.creditcheck.components.ReportTypeDetailsResources;

public interface CreditCheckWizardResources extends ClientBundleWithLookup, ReportTypeDetailsResources {

    public static CreditCheckWizardResources INSTANCE = GWT.create(CreditCheckWizardResources.class);

    @Source("collection-of-business-information-explanation.html")
    TextResource collectionOfBusinessInformationExplanation();

    @Source("collection-of-personal-information-for-equifax-explanation.html")
    TextResource collectionOfPersonalInformationForEquifaxExplanation();

    public static String TERMS_OF_SERVICE_ANCHOR_ID = "CreditCheckSetupWizard-TermsOfServiceAnchor";

    public static String COMPANY_NAME_ID = "CreditCeckSetupWizard-CompanyName";

    /**
     * This hypertext contains some special elements with following id that have to be reprocessed before use:<br/>
     * 
     * <li><code>CreditCheckWizard-TermsOfServiceAnchor</code>: this is anchor that links to the terms of service, a on-click handler should be attached to it</li>
     * 
     * <li><code>CreditCheckWizard-CompanyNames</code>: this is a span that should hold the names of the companies</li>
     */
    @Source("collection-of-personal-information-service-agreement.html")
    TextResource collectionOfPersonalInformationServiceAgreement();

    @Source("confirmation-and-payment-text.html")
    TextResource confirmationAndPaymentText();

    @Source("confirmation-and-payment-service-agreement.html")
    TextResource confirmationAnPaymentServiceAgreement();

    @Override
    @Source("recommendation-report-description.html")
    public TextResource recommendationReportDescription();

    @Override
    @Source("full-credit-report-description.html")
    public TextResource fullCreditReportDescription();

    @Override
    @Source("equifax-logo.png")
    public ImageResource equifaxLogo();
}
