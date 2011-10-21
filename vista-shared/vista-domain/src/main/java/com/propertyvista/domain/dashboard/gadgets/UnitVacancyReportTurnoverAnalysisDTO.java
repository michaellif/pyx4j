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

import java.sql.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;
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
        Day {
            @Override
            public long addTo(long time) {
                return time + 1000L * 60L * 60L * 24L;
            }

            @Override
            public Date addTo(Date date) {
                return new Date(addTo(date.getTime()));
            }

            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return TimeUtils.simpleFormat(start, "MMM-dd");
            }
        },
        Week {
            @Override
            public long addTo(long time) {
                return time + 1000L * 60L * 60L * 24L * 7L;
            }

            @Override
            public Date addTo(Date date) {
                return new Date(addTo(date.getTime()));
            }

            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return "(" + TimeUtils.simpleFormat(start, "MM/dd") + ", " + TimeUtils.simpleFormat(end, "MM/dd") + ")";
            }
        },
        Month {

            @Override
            public long addTo(long time) {
                return addToTimeAndReturnDate(time).getTime();
            }

            @Override
            public Date addTo(Date date) {
                return addToTimeAndReturnDate(date.getTime());
            }

            @SuppressWarnings("deprecation")
            private Date addToTimeAndReturnDate(long time) {
                Date updatedDate = new Date(time);
                int updatedMonth = updatedDate.getMonth() + 1;
                updatedDate.setMonth(updatedMonth % 12);
                updatedDate.setYear(updatedDate.getYear() + updatedMonth / 12);
                return updatedDate;
            }

            @SuppressWarnings("deprecation")
            @Override
            public String intervalLabelFormat(Date start, Date end) {
                I18n i18n = I18n.get(UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution.class);
                return i18n.tr(TimeUtils.MONTH_NAMES_SHORT[end.getMonth()]) + "-" + Integer.toString(1900 + end.getYear());
            }
        },
        Year {
            @Override
            public long addTo(long time) {
                return addToTimeAndReturnDate(time).getTime();
            }

            @Override
            public Date addTo(Date date) {
                return addToTimeAndReturnDate(date.getTime());
            }

            @SuppressWarnings("deprecation")
            private Date addToTimeAndReturnDate(long time) {
                Date date = new Date(time);
                date.setYear(date.getYear() + 1);
                return date;
            }

            @SuppressWarnings("deprecation")
            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return Integer.toString(1900 + end.getYear());
            }
        };

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

        /**
         * Adds this interval to given time.
         * 
         * @param time
         *            time that is to be increased by this interval
         * @return
         */
        public abstract long addTo(long time);

        public abstract Date addTo(Date date);

        public String intervalLabelFormat(Date start, Date end) {
            return "(" + start.toString() + ", " + end.toString() + ")";
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
