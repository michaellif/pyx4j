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
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;
import com.propertyvista.onboarding.AccountInfoIO;
import com.propertyvista.onboarding.AccountInfoResponseIO;
import com.propertyvista.onboarding.ActivatePMCRequestIO;
import com.propertyvista.onboarding.CheckAvailabilityRequestIO;
import com.propertyvista.onboarding.CreatePMCRequestIO;
import com.propertyvista.onboarding.CrmUserAuthenticationRequestIO;
import com.propertyvista.onboarding.CrmUserAuthenticationResponseIO;
import com.propertyvista.onboarding.CrmUserPasswordChangeRequestIO;
import com.propertyvista.onboarding.CrmUserPasswordReminderRequestIO;
import com.propertyvista.onboarding.GetAccountInfoRequestIO;
import com.propertyvista.onboarding.GetUsageRequestIO;
import com.propertyvista.onboarding.ProvisionPMCRequestIO;
import com.propertyvista.onboarding.RequestIO;
import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.ResponseMessageIO;
import com.propertyvista.onboarding.UpdateAccountInfoRequestIO;
import com.propertyvista.onboarding.UsageRecordIO;
import com.propertyvista.onboarding.UsageReportResponseIO;

public class OnboardingXMLEntityFactory extends XMLEntityFactoryStrict {

    public OnboardingXMLEntityFactory() {
        super(new ImportXMLEntityName());
    }

    @Override
    protected void bind() {
        bind(AccountInfoIO.class);
        bind(AccountInfoResponseIO.class);
        bind(ActivatePMCRequestIO.class);
        bind(AddressIO.class);
        bind(CheckAvailabilityRequestIO.class);
        bind(CreatePMCRequestIO.class);
        bind(CrmUserAuthenticationRequestIO.class);
        bind(CrmUserAuthenticationResponseIO.class);
        bind(CrmUserPasswordChangeRequestIO.class);
        bind(CrmUserPasswordReminderRequestIO.class);
        bind(GetAccountInfoRequestIO.class);
        bind(GetUsageRequestIO.class);
        bind(ProvisionPMCRequestIO.class);
        bind(RequestIO.class);
        bind(RequestMessageIO.class);
        bind(ResponseIO.class);
        bind(ResponseMessageIO.class);
        bind(UpdateAccountInfoRequestIO.class);
        bind(UsageRecordIO.class);
        bind(UsageReportResponseIO.class);
    }
}
