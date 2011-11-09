/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;

import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;

/**
 * This class is for conversion of actual {@link IBuildingGadget.FilterData} to demo mode.
 * Basically it uses fake buildings instead of real ones, and normalizes the incoming dates.
 * 
 * @author ArtyomB
 * 
 */
public class FilterDataDemoAdapter {
    private static final long DEFAULT_MAX_INTERVALS = 100;

    public Set<String> buildings;

    public LogicalDate fromDate;

    public LogicalDate toDate;

    public final boolean isEnabled;

    @SuppressWarnings("deprecation")
    public FilterDataDemoAdapter(IBuildingGadget.FilterData filterData) {
        Set<String> fake = fakeBuildings(filterData.buildings);
        if (fake != null) {
            buildings = Collections.unmodifiableSet(fake);
            isEnabled = true;
        } else {
            isEnabled = false;
        }

        if (filterData.toDate != null) {
            toDate = new LogicalDate(filterData.toDate);
        } else {
            toDate = new LogicalDate();
        }
        // we get non inclusive range [,) from the UI, but we think it's more intuitive to make it inclusive
        // hence we fix the 'to date' so points to end of the next day        
        toDate = new LogicalDate(TimeUtils.dayEnd(toDate));

        if (filterData.fromDate != null) {
            fromDate = new LogicalDate(filterData.fromDate.getTime());
        } else {
            long interval = TurnoverAnalysisGraphGadget.DEFAULT_TURNOVER_ANALYSIS_RESOLUTION_MAX.addTo(toDate.getTime()) - toDate.getTime();
            fromDate = new LogicalDate(toDate.getTime() - DEFAULT_MAX_INTERVALS * interval);
            // round up to the beginning of the year:
            // no need to zero hours and seconds since this is java.sql.Date that doesn't have this information
            fromDate.setMonth(0);
            fromDate.setDate(1);
        }
    }

    private HashSet<String> fakeBuildings(List<Key> buildings) {
        HashSet<String> fakeBuildings = new HashSet<String>();
        if (buildings == null || buildings.size() == 0) {
            // empty list should denote all buildings
        }
        if (buildings.size() == 1) {
            if (buildings.get(0).asLong() == -1d) {
                fakeBuildings = null;
            } else {
                fakeBuildings.add("jean0200");
            }
        } else if (buildings.size() > 1) {
            fakeBuildings.add("bath1650");
            fakeBuildings.add("com0164");
            fakeBuildings.add("chel3126");
        }
        return fakeBuildings;
    }

    public Set<String> getBuildingsFilteringCriteria() {
        return buildings;
    }

    public LogicalDate getFrom() {
        return fromDate;
    }

    public LogicalDate getTo() {
        return toDate;
    };

}
