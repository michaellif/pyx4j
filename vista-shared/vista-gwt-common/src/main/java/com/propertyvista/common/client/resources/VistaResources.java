/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 29, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface VistaResources extends ClientBundleWithLookup {

    VistaResources INSTANCE = GWT.create(VistaResources.class);

    @Source("Pre-AuthorizationLegalCC.html")
    TextResource paymentPreauthorisedCC();

    @Source("Pre-AuthorizationLegalPAD.html")
    TextResource paymentPreauthorisedPAD();

    @Source("LT_BillingAndRefundPolicy.html")
    TextResource billingAndRefundPolicy();

    @Source("LT_PrivacyPolicy.html")
    TextResource privacyPolicy();

    @Source("LT_TermsAndConditions.html")
    TextResource termsAndConditions();

    @Source("DirectBankingInstruction.html")
    TextResource directBankingInstruction();

    @Source("DirectBankingDescription.html")
    TextResource directBankingDescription();
}
