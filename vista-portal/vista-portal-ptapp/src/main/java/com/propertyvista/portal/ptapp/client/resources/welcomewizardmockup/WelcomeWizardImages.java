/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.resources.welcomewizardmockup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface WelcomeWizardImages extends ClientBundle {

    public static final WelcomeWizardImages INSTANCE = GWT.create(WelcomeWizardImages.class);

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("clipboard-with-checkbox.png")
    ImageResource clipboardWithCheckbox();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("logo-TenantSure-transparent-small.png")
    ImageResource logoTenantSure();

    @ImageOptions(repeatStyle = RepeatStyle.Both, width = 101)
    @Source("logo-Highcourt.png")
    ImageResource logoHighcourt();

}
