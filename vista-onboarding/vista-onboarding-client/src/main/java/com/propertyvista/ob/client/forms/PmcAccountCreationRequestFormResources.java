/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-13
 * @author ArtyomB
 */
package com.propertyvista.ob.client.forms;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface PmcAccountCreationRequestFormResources extends ClientBundleWithLookup {

    public static PmcAccountCreationRequestFormResources INSTANCE = GWT.create(PmcAccountCreationRequestFormResources.class);

    @Source("url-field-note.html")
    TextResource urlFieldNote();
}
