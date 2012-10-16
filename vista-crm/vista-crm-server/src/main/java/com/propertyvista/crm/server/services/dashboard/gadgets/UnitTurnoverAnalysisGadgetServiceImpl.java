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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitTurnoverAnalysisGadgetService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerIntervalDTO.AnalysisResolution;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitTurnoverAnalysisGadgetServiceImpl implements UnitTurnoverAnalysisGadgetService {

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitTurnoversPerIntervalDTO>> callback, Vector<Building> buildingsFilter, LogicalDate reportDate) {
        buildingsFilter = Util.enforcePortfolio(buildingsFilter);

        Vector<UnitTurnoversPerIntervalDTO> result = new Vector<UnitTurnoversPerIntervalDTO>(12);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(reportDate);
        cal.add(Calendar.MONTH, -12);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        LogicalDate tweleveMonthsAgo = new LogicalDate(cal.getTime());

        LogicalDate endOfMonth = Util.endOfMonth(tweleveMonthsAgo);

        UnitTurnoverAnalysisFacade manager = ServerSideFactory.create(UnitTurnoverAnalysisFacade.class);

        Key[] buildingsArray = new Key[buildingsFilter.size()];
        int i = 0;
        for (Building b : buildingsFilter) {
            buildingsArray[i++] = b.getPrimaryKey();
        }

        int totalTurnovers = 0;

        while (endOfMonth.before(reportDate)) {
            UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
            intervalStats.intervalSize().setValue(AnalysisResolution.Month);
            intervalStats.intervalValue().setValue(new LogicalDate(endOfMonth));

            int turnovers = manager.turnoversSinceBeginningOfTheMonth(endOfMonth, buildingsArray);
            intervalStats.unitsTurnedOverAbs().setValue(turnovers);
            totalTurnovers += turnovers;
            result.add(intervalStats);

            endOfMonth = Util.endOfMonth(Util.beginningOfNextMonth(endOfMonth));
        }
        UnitTurnoversPerIntervalDTO intervalStats = EntityFactory.create(UnitTurnoversPerIntervalDTO.class);
        intervalStats.intervalSize().setValue(AnalysisResolution.Month);
        intervalStats.intervalValue().setValue(new LogicalDate(endOfMonth));
        intervalStats.unitsTurnedOverAbs().setValue(manager.turnoversSinceBeginningOfTheMonth(reportDate, buildingsArray));
        result.add(intervalStats);

        if (totalTurnovers != 0) {
            for (UnitTurnoversPerIntervalDTO stats : result) {
                stats.unitsTurnedOverPct().setValue((double) (stats.unitsTurnedOverAbs().getValue()) / totalTurnovers);
            }
        } else {
            for (UnitTurnoversPerIntervalDTO stats : result) {
                stats.unitsTurnedOverPct().setValue(0d);
            }
        }
        callback.onSuccess(result);
    }

}
