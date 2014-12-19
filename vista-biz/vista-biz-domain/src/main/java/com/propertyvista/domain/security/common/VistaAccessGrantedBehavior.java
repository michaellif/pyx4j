/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 28, 2014
 * @author vlads
 */
package com.propertyvista.domain.security.common;

import com.pyx4j.security.shared.Behavior;

/**
 * Each application become working
 *
 * passed the password change stage and other mandatory stuff like Lease selection and Move In Wizard
 *
 */
public enum VistaAccessGrantedBehavior implements Behavior {

    CRM,

    Onboarding,

    Operations,

    ProspectPortal,

    ResidentPortal;

}
