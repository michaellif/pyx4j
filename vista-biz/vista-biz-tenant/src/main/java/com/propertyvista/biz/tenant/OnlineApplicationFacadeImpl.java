/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.List;

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

public class OnlineApplicationFacadeImpl implements OnlineApplicationFacade {

    @Override
    public void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<OnlineApplication> getOnlineApplications(TenantUser customerUser) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void submitOnlineApplication(OnlineApplication application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resendInvitationEmail(LeaseParticipant leaseParticipant) {
        // TODO Auto-generated method stub

    }

}
