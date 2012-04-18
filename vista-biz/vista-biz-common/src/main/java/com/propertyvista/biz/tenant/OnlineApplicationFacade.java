/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.List;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

public interface OnlineApplicationFacade {

    /**
     * Called by LeaseFacade
     */
    void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication);

    List<OnlineApplication> getOnlineApplications(CustomerUser customerUser);

    VistaCustomerBehavior getOnlineApplicationBehavior(OnlineApplication application);

    void submitOnlineApplication(OnlineApplication application);

    void resendInvitationEmail(LeaseParticipant leaseParticipant);
}
