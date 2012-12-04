/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.availability;

import java.io.Serializable;

import com.propertyvista.domain.dashboard.gadgets.common.TimeInterval;

public final class UnitTurnoversPerInterval implements Serializable {

    private static final long serialVersionUID = 664111817152832624L;

    private final int turnovers;

    private final TimeInterval interval;

    public UnitTurnoversPerInterval(TimeInterval interval, int turnovers) {
        this.interval = interval;
        this.turnovers = turnovers;
    }

    public int getTurnovers() {
        return turnovers;
    }

    public TimeInterval getInterval() {
        return interval;
    }

}
