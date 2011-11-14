/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;

public class PtAuthenticationServiceImpl extends VistaAuthenticationServicesImpl implements PtAuthenticationService {

    @Override
    protected boolean hasRequiredSiteBehavior() {
        return SecurityController.checkAnyBehavior(VistaBehavior.PROSPECTIVE_TENANT, VistaBehavior.GUARANTOR);
    }

}
