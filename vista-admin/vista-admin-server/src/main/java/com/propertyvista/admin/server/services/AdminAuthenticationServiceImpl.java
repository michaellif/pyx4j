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
package com.propertyvista.admin.server.services;

import java.util.Set;

import com.pyx4j.security.shared.Behavior;

import com.propertyvista.admin.rpc.services.AdminAuthenticationService;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.server.common.security.VistaAuthenticationServicesImpl;
import com.propertyvista.server.domain.security.AdminUserCredential;

public class AdminAuthenticationServiceImpl extends VistaAuthenticationServicesImpl<AdminUser, AdminUserCredential> implements AdminAuthenticationService {

    public AdminAuthenticationServiceImpl() {
        super(AdminUser.class, AdminUserCredential.class);
    }

    @Override
    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.Admin;
    }

    @Override
    protected Behavior getPasswordChangeRequiredBehavior() {
        return VistaBasicBehavior.AdminPasswordChangeRequired;
    }

    @Override
    protected void addBehaviors(AdminUserCredential userCredential, Set<Behavior> behaviors) {
    }

}
