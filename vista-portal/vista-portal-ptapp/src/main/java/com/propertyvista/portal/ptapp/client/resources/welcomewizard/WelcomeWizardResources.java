/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.resources.welcomewizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface WelcomeWizardResources extends ClientBundleWithLookup {

    WelcomeWizardResources INSTANCE = GWT.create(WelcomeWizardResources.class);

    @Source("you-have-been-approved.html")
    TextResource youHaveBeenApproved();

    @Source("functionality-of-the-move-in-guide.html")
    TextResource functionalityOfTheMoveInGuide();

    @Source("do-not-worry.html")
    TextResource doNotWorry();

    @Source("insurance-reason-explanation.html")
    TextResource insuranceReasonExplanation();
}
