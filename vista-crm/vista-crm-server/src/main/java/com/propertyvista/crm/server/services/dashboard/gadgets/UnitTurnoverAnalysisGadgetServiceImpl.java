/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisManager;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerInterval;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.common.TimeIntervalMonth;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitTurnoverAnalysisGadgetServiceImpl implements UnitTurnoverAnalysisGadgetService {

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>> callback, Vector<Building> buildingsFilter, LogicalDate reportDate) {
        GregorianCalendar twelveMonthsAgo = new GregorianCalendar();
        twelveMonthsAgo.setTime(reportDate);
        twelveMonthsAgo.add(Calendar.MONTH, -12);
        twelveMonthsAgo.set(Calendar.DAY_OF_MONTH, twelveMonthsAgo.getActualMinimum(Calendar.DAY_OF_MONTH));

        GregorianCalendar endOfThisMonth = new GregorianCalendar();
        endOfThisMonth.setTime(reportDate);
        endOfThisMonth.add(Calendar.DAY_OF_MONTH, endOfThisMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        UnitTurnoverAnalysisManager manager = ServerSideFactory.create(UnitTurnoverAnalysisManager.class);
        List<UnitTurnoversPerInterval> turnovers = manager.turnovers(new TimeIntervalMonth(), new LogicalDate(twelveMonthsAgo.getTime()), new LogicalDate(
                endOfThisMonth.getTime()), buildingsFilter);

        int totalTurnovers = 0;
        for (UnitTurnoversPerInterval turnoversPerInterval : turnovers) {
            totalTurnovers += turnoversPerInterval.getTurnovers();
        }

        Vector<UnitTurnoversPerIntervalDTO> result = new Vector<UnitTurnoversPerIntervalDTO>(12);
        for (UnitTurnoversPerInterval turnoversPerInterval : turnovers) {
            UnitTurnoversPerIntervalDTO dto = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
            dto.intervalSize().setValue(AnalysisResolution.Month);
            dto.intervalValue().setValue(new LogicalDate(turnoversPerInterval.getInterval().getTo()));
            dto.unitsTurnedOverAbs().setValue(turnoversPerInterval.getTurnovers());
            dto.unitsTurnedOverPct().setValue(turnoversPerInterval.getTurnovers() / (double) totalTurnovers * 100);
            result.add(dto);
        }
        callback.onSuccess(result);
    }
}
