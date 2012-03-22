/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import com.pyx4j.essentials.server.xml.XMLEntityFactoryStrict;

import com.propertyvista.interfaces.importer.model.AddressIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;
import com.propertyvista.onboarding.AccountInfoIO;
import com.propertyvista.onboarding.ActivatePMCRequestIO;
import com.propertyvista.onboarding.CheckAvailabilityRequestIO;
import com.propertyvista.onboarding.CreateOnboardingUserRequestIO;
import com.propertyvista.onboarding.CreatePMCRequestIO;
import com.propertyvista.onboarding.GetAccountInfoRequestIO;
import com.propertyvista.onboarding.GetUsageRequestIO;
import com.propertyvista.onboarding.OnboardingUserAuthenticationRequestIO;
import com.propertyvista.onboarding.OnboardingUserPasswordChangeRequestIO;
import com.propertyvista.onboarding.OnboardingUserPasswordReminderRequestIO;
import com.propertyvista.onboarding.ProvisionPMCRequestIO;
import com.propertyvista.onboarding.RequestIO;
import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.UpdateAccountInfoRequestIO;

public class OnboardingRequestXMLEntityFactory extends XMLEntityFactoryStrict {

    public OnboardingRequestXMLEntityFactory() {
        super(new ImportXMLEntityNamingConvention());
    }

    @Override
    protected void bind() {
        bind(AccountInfoIO.class);
        bind(ActivatePMCRequestIO.class);
        bind(AddressIO.class);
        bind(CheckAvailabilityRequestIO.class);
        bind(CreatePMCRequestIO.class);
        bind(CreateOnboardingUserRequestIO.class);
        bind(OnboardingUserAuthenticationRequestIO.class);
        bind(OnboardingUserPasswordChangeRequestIO.class);
        bind(OnboardingUserPasswordReminderRequestIO.class);
        bind(GetAccountInfoRequestIO.class);
        bind(GetUsageRequestIO.class);
        bind(ProvisionPMCRequestIO.class);
        bind(RequestIO.class);
        bind(RequestMessageIO.class);
        bind(UpdateAccountInfoRequestIO.class);
    }
}
