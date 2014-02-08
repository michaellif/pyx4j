/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security.common;

import com.pyx4j.security.shared.Behavior;

public enum VistaBasicBehavior implements Behavior {

    Onboarding,

    System,

    CRM,

    CRMPasswordChangeRequired,

    /** CRM should force user with this behavior to change it's account recovery options, i.e. redirect to account recovery options place */
    CRMSetupAccountRecoveryOptionsRequired,

    CRMPasswordChangeRequiresSecurityQuestion,

    ProspectPortal,

    ProspectPortalPasswordChangeRequired,

    ResidentPortal,

    ResidentPortalPasswordChangeRequired,

    Operations,

    OperationsPasswordChangeRequired,

    VistaTermsAcceptanceRequired;

}
