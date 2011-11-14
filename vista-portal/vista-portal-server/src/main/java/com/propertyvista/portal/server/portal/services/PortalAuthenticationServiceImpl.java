/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;

public class PortalAuthenticationServiceImpl extends VistaAuthenticationServicesImpl implements PortalAuthenticationService {

    @Override
    protected boolean hasRequiredSiteBehavior() {
        return SecurityController.checkAnyBehavior(VistaBehavior.TENANT, VistaBehavior.PROSPECTIVE_TENANT, VistaBehavior.GUARANTOR);
    }

}
