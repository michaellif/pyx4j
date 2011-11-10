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
package com.propertyvista.domain.dashboard.gadgets.vacancyreport;

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
 * Contains analysis results of for a single interval for all events from {@link #fromDate()} until (exclusively) {@link #toDate()}.
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

            @Override
            public long intervalStart(long time) {
                return new Date(time).getTime();
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalEnd(long time) {
                Date date = new Date(time);
                date.setDate(date.getDate() + 1);
                return date.getTime();
            }
        },
        Week {
            private static final long DAY_TIMESPAN = 1000L * 60L * 60L * 24L;

            @Override
            public long addTo(long time) {
                return time + DAY_TIMESPAN * 7L;
            }

            @Override
            public Date addTo(Date date) {
                return new Date(addTo(date.getTime()));
            }

            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return "(" + TimeUtils.simpleFormat(start, "MM/dd") + ", " + TimeUtils.simpleFormat(end, "MM/dd") + ")";
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                return date.getTime() - date.getDay() * DAY_TIMESPAN;
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalEnd(long time) {
                Date date = new Date(time);
                int daysToAdd = 7 - date.getDay();
                return date.getTime() + daysToAdd * DAY_TIMESPAN;
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
                return i18n.tr(TimeUtils.MONTH_NAMES_SHORT[start.getMonth()]) + "-" + Integer.toString(1900 + start.getYear());
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                date.setDate(1);
                return date.getTime();
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalEnd(long time) {
                Date date = new Date(time);
                int month;
                do {
                    month = date.getMonth();
                    date.setDate(date.getDate() + 1);
                } while (date.getMonth() == month);
                return date.getTime();
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
                return Integer.toString(1900 + start.getYear());
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                date.setMonth(0);
                date.setDate(1);
                return date.getTime();
            }

            @SuppressWarnings("deprecation")
            @Override
            public long intervalEnd(long time) {
                Date date = new Date(time);
                date.setYear(date.getYear() + 1);
                date.setMonth(0);
                date.setDate(1);
                return date.getTime();
            }
        };

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        /**
         * Replacement of the usual <code>valueOf()</code> that can deal with translated strings.
         * 
         * @param representation
         *            translated representation of the string in the current locale.
         * @return value of the representation or <code>null</code> representation does not belong to a value.
         */
        public static AnalysisResolution representationToValue(String representation) {
            AnalysisResolution[] values = AnalysisResolution.values();
            for (AnalysisResolution val : values) {
                if (val.toString().equals(representation)) {
                    return val;
                }
            }
            return null;
        }

        public abstract long intervalStart(long time);

        public abstract long intervalEnd(long time);

        /**
         * Add this interval to given time.
         * 
         * @param time
         *            time that is to be increased by this interval.
         * @return
         */
        public abstract long addTo(long time);

        /**
         * Same as {@link #addTo(long)}.
         * 
         * @param date
         * @return
         */
        public abstract Date addTo(Date date);

        /**
         * Create a 'caption' or 'label' for given interval.
         * 
         * @param start
         *            denotes the beginning of the interval (not <code>null</code>).
         * @param end
         *            denotes the end of the interval (not <code>null</code>).
         * @return
         */
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
