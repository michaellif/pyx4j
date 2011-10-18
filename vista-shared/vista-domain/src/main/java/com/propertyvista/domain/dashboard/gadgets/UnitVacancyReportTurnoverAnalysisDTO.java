/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 14, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

/**
 * Contains analysis of for a single interval for all events between {@link #fromDate()} until (exclusively) {@link #toDate()}.
 * 
 * @author ArtyomB
 * 
 */
@Transient
public interface UnitVacancyReportTurnoverAnalysisDTO extends IEntity {
    public enum AnalysisResolution {
        Month, Year;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }

        public static AnalysisResolution representationToValue(String representation) {
            AnalysisResolution[] values = AnalysisResolution.values();
            for (AnalysisResolution val : values) {
                if (val.toString().equals(representation)) {
                    return val;
                }
            }
            return null;
        }

    }

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> fromDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> toDate();

    /**
     * @return number of units turned over during time interval <code>[{@link #fromDate()}, {@link #toDate()})</code>.
     */
    IPrimitive<Integer> unitsTurnedOverAbs();

    /**
     * @return percentage of units turned over during time interval <code>[{@link #fromDate()}, {@link #toDate()})</code> relative to overall number of units
     *         turned over during the time interval specified in a query.
     */
    IPrimitive<Double> unitsTurnedOverPct();

}
