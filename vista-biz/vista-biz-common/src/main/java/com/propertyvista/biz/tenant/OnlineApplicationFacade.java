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
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.ProspectSignUp;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;

public interface OnlineApplicationFacade {

    void prospectSignUp(ProspectSignUp request);

    void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication);

    void approveMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication);

    List<OnlineApplication> getOnlineApplications(CustomerUser customerUser);

    PortalProspectBehavior getOnlineApplicationBehavior(OnlineApplication application);

    void submitOnlineApplication(OnlineApplication application);

    void resendInvitationEmail(LeaseTermParticipant leaseParticipant);

    MasterOnlineApplicationStatus calculateOnlineApplicationStatus(MasterOnlineApplication masterOnlineApplication);
}
