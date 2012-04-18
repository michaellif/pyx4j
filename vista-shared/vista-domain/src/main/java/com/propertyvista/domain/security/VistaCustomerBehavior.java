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

import com.pyx4j.security.shared.Behavior;

public enum VistaCustomerBehavior implements Behavior {

    ApplicationSelectionRequired,

    Prospective,

    ProspectiveApplicant,

    ProspectiveCoApplicant,

    Guarantor /* is as well Prospective see VistaPortalAccessControlList */,

    //-- Application Submitted 

    ProspectiveSubmitted,

    ProspectiveSubmittedApplicant,

    ProspectiveSubmittedCoApplicant,

    GuarantorSubmitted /* is as well ProspectiveSubmitted see VistaPortalAccessControlList */,

    // Application Approved - > Portal

    LeaseSelectionRequired,

    Tenant,

    TenantPrimary,

    TenantSecondary

}
