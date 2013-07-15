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
 * @version $Id$
 */
package com.propertyvista.ob.rpc.dto;

import com.pyx4j.security.shared.UserVisit;

@SuppressWarnings("serial")
public class OnboardingUserVisit extends UserVisit {

    public OnboardingApplicationStatus status;

    public String pmcNamespace;

    public String accountCreationDeferredCorrelationId;

    public void setStatus(OnboardingApplicationStatus status) {
        this.status = status;
        this.changed = true;
    }

    public void setPmcNamespace(String pmcNamespace) {
        this.pmcNamespace = pmcNamespace;
        this.changed = true;
    }

    public void setAccountCreationDeferredCorrelationId(String deferredCorrelationId) {
        this.accountCreationDeferredCorrelationId = deferredCorrelationId;
        this.changed = true;
    }
}
