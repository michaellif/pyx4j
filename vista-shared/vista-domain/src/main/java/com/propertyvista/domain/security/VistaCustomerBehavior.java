/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.security.shared.Behavior;

@I18n
public enum VistaCustomerBehavior implements Behavior {

    ApplicationSelectionRequired,

    HasMultipleApplications,

    Prospective,

    ProspectiveApplicant,

    ProspectiveCoApplicant,

    @Translate(value = "Guarantor")
    Guarantor /* is as well Prospective see VistaPortalAccessControlList */,

    //-- Application Submitted 
    @Translate(value = "Applicant")
    ProspectiveSubmitted,

    @Translate(value = "Applicant")
    ProspectiveSubmittedApplicant,

    @Translate(value = "Co Applicant")
    ProspectiveSubmittedCoApplicant,

    @Translate(value = "Guarantor")
    GuarantorSubmitted /* is as well ProspectiveSubmitted see VistaPortalAccessControlList */,

    // Application Approved - > Portal

    LeaseSelectionRequired,

    VistaTermsAcceptanceRequired,

    HasMultipleLeases,

    ElectronicPaymentsAllowed,

    @Translate(value = "Tenant")
    Tenant,

    @Translate(value = "Tenant")
    TenantPrimary,

    @Translate(value = "Tenant")
    TenantSecondary;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    };
}
