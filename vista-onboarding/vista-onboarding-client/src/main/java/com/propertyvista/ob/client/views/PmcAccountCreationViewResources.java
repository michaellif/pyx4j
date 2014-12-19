/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-21
 * @author ArtyomB
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface PmcAccountCreationViewResources extends ClientBundleWithLookup {

    public static PmcAccountCreationViewResources INSTANCE = GWT.create(PmcAccountCreationViewResources.class);

    @Source("sign-up-screen-text.html")
    TextResource singUpText();
}
