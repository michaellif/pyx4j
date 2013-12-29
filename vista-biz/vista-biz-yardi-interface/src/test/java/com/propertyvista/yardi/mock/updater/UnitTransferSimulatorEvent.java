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
package com.propertyvista.yardi.mock.updater;

import com.propertyvista.test.mock.MockEvent;

public class UnitTransferSimulatorEvent extends MockEvent<UnitTransferSimulatorEvent.Handler> {

    public final UnitTransferSimulator updater;

    public interface Handler {

        void unitTransferSimulation(UnitTransferSimulatorEvent event);

    }

    public UnitTransferSimulatorEvent(UnitTransferSimulator updater) {
        super();
        this.updater = updater;
    }

    @Override
    protected final void dispatch(Handler handler) {
        handler.unitTransferSimulation(this);
    }

    public UnitTransferSimulator getUpdater() {
        return updater;
    }
}
