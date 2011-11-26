/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */package com.propertyvista.portal.ptapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface PortalResources extends ClientBundleWithLookup {

    PortalResources INSTANCE = GWT.create(PortalResources.class);

    @Source("welcomeNotes.html")
    TextResource welcomeNotes();

    @Source("dont_worry.html")
    TextResource dontWorry();

    @Source("requirements.html")
    TextResource requirements();

    @Source("time.html")
    TextResource time();

    @Source("availabilityAndPricing.html")
    TextResource availabilityAndPricing();

    @Source("digitalSignature.html")
    TextResource digitalSignature();

    //PaymentViewForm

    @Source("paymentApprovalNotes.html")
    TextResource paymentApprovalNotes();

    @Source("paymentPreauthorisedNotes_MC.html")
    TextResource paymentPreauthorisedNotes_MC();

    @Source("paymentPreauthorisedNotes_VISA.html")
    TextResource paymentPreauthorisedNotes_VISA();

    @Source("paymentTermsNotes.html")
    TextResource paymentTermsNotes();

}
