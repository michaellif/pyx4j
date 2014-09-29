/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.ResidentSelfRegistration;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionType;

public interface CustomerFacade {

    void persistCustomer(Customer customer);

    List<Lease> getActiveLeasesId(CustomerUser customerUserId);

    Collection<PortalResidentBehavior> getLeaseBehavior(CustomerUser customerUser, Lease leaseId);

    boolean hasToAcceptTerms(CustomerUser customerUser);

    void onVistaTermsAccepted(Key customerUserKey, Key vistaTermsKey, boolean accepted);

    void residentSelfRegistration(ResidentSelfRegistration request);

    void setCustomerPassword(Customer customer, String password);

    void setCustomerPasswordHash(Customer customer, String passwordHash);

    Collection<LeaseParticipantMoveInAction> getActiveMoveInActions(LeaseParticipant<?> leaseParticipant);

    void skipMoveInAction(LeaseParticipant<?> leaseParticipant, MoveInActionType moveInActionType);

}
