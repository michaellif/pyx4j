/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-28
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface OnlinePaymentWizardResources extends ClientBundleWithLookup {

    public static final OnlinePaymentWizardResources INSTANCE = GWT.create(OnlinePaymentWizardResources.class);

    @Source("caledon-logo.png")
    ImageResource caledonLogo();

    @Source("payment-pad-logo.png")
    ImageResource paymentPadLogo();

    @Source("visa-logo.png")
    ImageResource visaLogo();

    @Source("debit-logo.png")
    ImageResource debitLogo();

    @Source("master-card-logo.png")
    ImageResource masterCardLogo();

    @Source("echeque-logo.png")
    ImageResource echequeLogo();

    @Source("direct-banking-logo.png")
    ImageResource directBankingLogo();

    @Source("interac-logo.png")
    ImageResource interacLogo();

    @Source("visa-debit-logo.png")
    ImageResource visaDebitLogo();

    // TODO this is supposed to be a template that is filled with values of the prices
    @Source("set-up-fee-pricing.html")
    TextResource setUpFeePricingExplanation();

    @Source("marketing-text.html")
    TextResource marketingText();

    /** this is used as ID of a element of serviceAgreement() that is to be substituted with anchor to terms of service */
    public static String TERMS_OF_SERVICE_ANCHOR_ID = "OnlinePaymentSetupWizard-TermsOfService";

    /** this is used as ID of an element of serviceagreement() that supposed to hold company name */
    public static String COMPANY_NAME_ID = "OnlinePaymentSetupWizard-CompanyName";

    @Source("service-agreement.html")
    TextResource serviceAgreement();

    @Source("collection-of-business-information-explanation.html")
    TextResource collectionOfBusinessInformationExplanation();

    @Source("collection-of-personal-information-for-online-payments-explanation.html")
    TextResource collectionOfPersonalInformationForEquifaxExplanation();

    @Source("caledon-signature-text.html")
    TextResource caledonSignatureText();

    @Source("payment-pad-signature-text.html")
    TextResource paymentPadSignatureText();
}
