/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.insurancemockup.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.resources.client.TextResource;

public interface InsuranceMockupResources extends ClientBundleWithLookup {

    public static InsuranceMockupResources INSTANCE = GWT.create(InsuranceMockupResources.class);

    @Source("you-must-obtain-insurance-message.html")
    TextResource youMustObtainInsuranceMessage();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("logo-TenantSure-transparent-small.png")
    ImageResource logoTenantSure();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("logo-Highcourt-small.png")
    ImageResource logoHighcourt();

}
