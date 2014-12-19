/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 */
package com.propertyvista.ob.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface StepStatusResources extends ClientBundleWithLookup {

    StepStatusResources INSTANCE = GWT.<StepStatusResources> create(StepStatusResources.class);

    @Source("progress-step-in-progress.png")
    ImageResource inProgress();

    @Source("progress-step-complete.png")
    ImageResource complete();

    @Source("progress-step-incomplete.png")
    ImageResource incomplete();
}
