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
 */
package com.propertyvista.biz.tenant;

import java.util.Collection;
import java.util.List;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.ProspectData;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationConfirmationTerm;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;

public interface OnlineApplicationFacade {

    /*
     * Creates new Customer and new Application.
     */
    Lease prospectSignUp(ProspectData request);

    /*
     * Creates new Application with existing Customer.
     */
    Lease prospectLogIn(ProspectData request);

    void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication, Building building, Floorplan floorplan);

    void approveMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication);

    void cancelMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication);

    void submitOnlineApplication(OnlineApplication application);

    void resendInvitationEmail(LeaseTermParticipant<?> leaseParticipant);

    List<OnlineApplication> getOnlineApplications(CustomerUser customerUser);

    OnlineApplication getOnlineApplication(CustomerUser customerUser, Lease lease);

    Collection<PortalProspectBehavior> getOnlineApplicationBehavior(OnlineApplication applicationId);

    List<SignedOnlineApplicationLegalTerm> getOnlineApplicationLegalTerms(OnlineApplication application);

    List<SignedOnlineApplicationConfirmationTerm> getOnlineApplicationConfirmationTerms(OnlineApplication application);

    Building getOnlineApplicationPolicyNode(OnlineApplication app);

    MasterOnlineApplicationStatus calculateOnlineApplicationStatus(MasterOnlineApplication masterOnlineApplication);

    void initOnlineApplicationFeeData(MasterOnlineApplication masterOnlineApplication);
}
