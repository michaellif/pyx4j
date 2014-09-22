/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager;

import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TenantBuilder;

// addGuest to building
// addEvent to application
// addFee to application
public interface YardiGuestManager extends YardiMockManager {

    interface GuestBuilder extends TenantBuilder {

    }

    GuestBuilder addGuest(String guestId, String name, String buildingId);

}
