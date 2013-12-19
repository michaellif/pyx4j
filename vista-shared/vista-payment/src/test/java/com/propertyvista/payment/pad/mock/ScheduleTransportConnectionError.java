/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad.mock;

import com.propertyvista.test.mock.MockEvent;

public class ScheduleTransportConnectionError extends MockEvent<ScheduleTransportConnectionError.Handler> {

    public final boolean connectionErrorEnabled;

    public interface Handler {

        void scheduleTransportConnectionError(ScheduleTransportConnectionError event);

    }

    public ScheduleTransportConnectionError(boolean connectionErrorEnabled) {
        super();
        this.connectionErrorEnabled = connectionErrorEnabled;
    }

    @Override
    protected final void dispatch(Handler handler) {
        handler.scheduleTransportConnectionError(this);
    }

}
