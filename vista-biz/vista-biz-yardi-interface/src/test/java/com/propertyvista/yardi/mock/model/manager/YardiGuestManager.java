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

import com.propertyvista.yardi.mock.model.domain.YardiGuestEvent;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TenantBuilder;

// addGuest to building
// addEvent to application
// addFee to application
public interface YardiGuestManager extends YardiMockManager {

    public interface ApplicationBuilder extends LeaseBuilder {

        ApplicationBuilder setUnit(String unitId);

        ApplicationBuilder addRentableItem(String itemId);

        GuestBuilder addGuest(String guestId, String name);

        GuestBuilder getGuest(String guestId);

        GuestEventBuilder addEvent(YardiGuestEvent.Type type);
    }

    public interface GuestBuilder extends TenantBuilder {

        @Override
        ApplicationBuilder done();
    }

    public interface GuestEventBuilder {
        GuestEventBuilder setEventId(String id);

        GuestEventBuilder setDate(String date);

        GuestEventBuilder setAgent(String name);

        GuestEventBuilder setQuote(String amount);

        ApplicationBuilder done();
    }

    ApplicationBuilder addApplication(String buildingId, String leaseId);

    ApplicationBuilder getApplication(String buildingId, String leaseId);
}
