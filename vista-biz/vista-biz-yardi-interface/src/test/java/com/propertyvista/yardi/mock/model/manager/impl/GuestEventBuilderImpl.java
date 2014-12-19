/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiGuestEvent;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.ApplicationBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.GuestEventBuilder;

public class GuestEventBuilderImpl implements GuestEventBuilder {

    private final YardiGuestEvent event;

    private final ApplicationBuilder parent;

    GuestEventBuilderImpl(YardiGuestEvent event, ApplicationBuilder parent) {
        this.event = event;
        this.parent = parent;
    }

    @Override
    public GuestEventBuilder setEventId(String id) {
        event.eventId().setValue(id);
        return this;
    }

    @Override
    public GuestEventBuilder setDate(String date) {
        event.date().setValue(YardiMockModelUtils.toDate(date));
        return this;
    }

    @Override
    public GuestEventBuilder setAgent(String name) {
        event.agentName().setValue(name);
        return this;
    }

    @Override
    public GuestEventBuilder setQuote(String amount) {
        event.rentQuote().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public ApplicationBuilder done() {
        return parent;
    }
}
