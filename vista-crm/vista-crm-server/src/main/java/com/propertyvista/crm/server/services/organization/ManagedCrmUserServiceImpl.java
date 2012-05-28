/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.server.common.security.VistaManagedPasswordChangeServiceImpl;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class ManagedCrmUserServiceImpl extends VistaManagedPasswordChangeServiceImpl<CrmUserCredential> implements ManagedCrmUserService {

    public ManagedCrmUserServiceImpl() {
        super(CrmUserCredential.class);
    }

}
