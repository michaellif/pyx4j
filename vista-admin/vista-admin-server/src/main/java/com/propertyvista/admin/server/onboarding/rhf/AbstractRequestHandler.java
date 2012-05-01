/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rhf;

import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.onboarding.OnboardingRole;
import com.propertyvista.onboarding.RequestIO;

public abstract class AbstractRequestHandler<E extends RequestIO> implements RequestHandler<E> {

    protected final Class<E> requestClass;

    public AbstractRequestHandler(Class<E> requestClass) {
        this.requestClass = requestClass;
    }

    @Override
    public Class<E> getRequestClass() {
        return requestClass;
    }

    protected OnboardingRole convertRole(VistaOnboardingBehavior behavior) {
        if (behavior == null) {
            return null;
        }
        switch (behavior) {
        case OnboardingAdministrator:
            return OnboardingRole.OnboardingAdministrator;
        case Caledon:
            return OnboardingRole.Caledon;
        case Equifax:
            return OnboardingRole.Equifax;
        case Client:
            return OnboardingRole.Client;
        case ProspectiveClient:
            return OnboardingRole.ProspectiveClient;
        default:
            throw new IllegalArgumentException();
        }
    }
}
