/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;

/**
 * to Tenant/Applicant (Lease)
 * to Guarantor (Lease)
 * to Lead(Guest) (Building)
 */
public interface CommunicationFacade {

    void sendAdminPasswordRetrievalToken(AdminUser user);

    void sendOnboardingPasswordRetrievalToken(OnboardingUser user, String onboardingSystemBaseUrl);

    void sendCrmPasswordRetrievalToken(CrmUser user);

    /**
     * customer may be Guarantor as well
     */
    void sendProspectPasswordRetrievalToken(Customer customer);

    void sendTenantPasswordRetrievalToken(Customer customer);

    void sendApplicantApplicationInvitation(Tenant tenant);

    void sendCoApplicantApplicationInvitation(Tenant tenant);

    void sendGuarantorApplicationInvitation(Guarantor guarantor);

    void sendApplicationStatus(Tenant tenant);

    void sendTenantInvitation(Tenant tenant);

    //void sendCustomerMessage(CustomerCustomMessageTemplate customMessageTemplate, Customer customer);

    //void sendEmployeeMessage(EmployeeMessageType employeeMessageType, Employee employee);

}
