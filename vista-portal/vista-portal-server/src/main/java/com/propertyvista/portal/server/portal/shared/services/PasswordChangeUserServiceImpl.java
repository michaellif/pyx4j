/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services;

import com.propertyvista.portal.rpc.portal.shared.services.PasswordChangeUserService;
import com.propertyvista.server.common.security.VistaUserSelfPasswordChangeServiceImpl;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class PasswordChangeUserServiceImpl extends VistaUserSelfPasswordChangeServiceImpl<CustomerUserCredential> implements
        PasswordChangeUserService {

    public PasswordChangeUserServiceImpl() {
        super(CustomerUserCredential.class);
    }

}
