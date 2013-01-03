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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface OnlinePaymentPricingResources extends ClientBundleWithLookup {

    public static final OnlinePaymentPricingResources INSTANCE = GWT.create(OnlinePaymentPricingResources.class);

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

    @Source("set-up-fee-pricing.txt")
    TextResource setUpFeePricingExplanation();

    @Source("marketing-text.txt")
    TextResource marketingText();
}
