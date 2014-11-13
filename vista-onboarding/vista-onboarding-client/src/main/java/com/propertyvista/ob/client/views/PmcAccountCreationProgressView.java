/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import java.util.List;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;

public interface PmcAccountCreationProgressView extends IPrimePaneView {

    void init(List<String> stepNames);

    void setStepStatus(String stepName, StepStatus status);

    void setCrmSiteUrl(OnboardingCrmURL url);

}
