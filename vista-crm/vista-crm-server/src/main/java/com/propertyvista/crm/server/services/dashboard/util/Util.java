/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.util;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.building.Building;

public class Util {

    /**
     * @return building stubs that are in portfolio of the current user
     * @deprecated TODO remove this function. Each class should have its own security adapter
     */
    @Deprecated
    public static Vector<Building> enforcePortfolio(List<Building> buildingsFilter) {
        Vector<Building> enforcedBuildingsFilter = new Vector<Building>();

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        if (!buildingsFilter.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().id(), buildingsFilter));
        }
        enforcedBuildingsFilter.addAll(Persistence.secureQuery(criteria, AttachLevel.IdOnly));
        return enforcedBuildingsFilter;
    }

    public static LogicalDate dayOfCurrentTransaction() {
        return new LogicalDate(SystemDateManager.getDate());
    }

    public static LogicalDate beginningOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate endOfMonth(LogicalDate dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dayOfMonth);
        cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate beginningOfNextMonth(LogicalDate dayInMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginningOfMonth(dayInMonth));
        cal.add(GregorianCalendar.MONTH, 1);
        return new LogicalDate(cal.getTime());
    }

    public static LogicalDate addDays(LogicalDate day, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.add(GregorianCalendar.DAY_OF_YEAR, days);
        return new LogicalDate(cal.getTime());
    }

}
