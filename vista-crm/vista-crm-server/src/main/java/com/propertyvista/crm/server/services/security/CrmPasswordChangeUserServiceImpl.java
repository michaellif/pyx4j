/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 23, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import com.propertyvista.crm.rpc.services.security.CrmPasswordChangeUserService;
import com.propertyvista.server.common.security.VistaUserSelfPasswordChangeServiceImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmPasswordChangeUserServiceImpl extends VistaUserSelfPasswordChangeServiceImpl<CrmUserCredential> implements CrmPasswordChangeUserService {

    public CrmPasswordChangeUserServiceImpl() {
        super(CrmUserCredential.class);
    }
}
