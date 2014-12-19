/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author vlads
 */
package com.propertyvista.ob.rpc.dto;

import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.shared.VistaUserVisit;

@SuppressWarnings("serial")
public class OnboardingUserVisit extends VistaUserVisit<OnboardingUser> {

    public OnboardingApplicationStatus status;

    public String pmcNamespace;

    public String accountCreationDeferredCorrelationId;

    // to make it GWT Serializable ?
    public OnboardingUserVisit() {
        super();
    }

    public OnboardingUserVisit(VistaApplication application) {
        super(application);
    }

    public void setStatus(OnboardingApplicationStatus status) {
        this.status = status;
        setChanged();
    }

    public void setPmcNamespace(String pmcNamespace) {
        this.pmcNamespace = pmcNamespace;
        setChanged();
    }

    public void setAccountCreationDeferredCorrelationId(String deferredCorrelationId) {
        this.accountCreationDeferredCorrelationId = deferredCorrelationId;
        setChanged();
    }

    @Override
    public String toString() {
        return "Onboarding " + super.toString();
    }
}
