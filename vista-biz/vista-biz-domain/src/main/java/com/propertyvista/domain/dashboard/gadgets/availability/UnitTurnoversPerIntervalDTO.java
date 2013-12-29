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
package com.propertyvista.domain.dashboard.gadgets.availability;

import java.sql.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface UnitTurnoversPerIntervalDTO extends IEntity {

    @SuppressWarnings("deprecation")
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

            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                return date.getTime() - date.getDay() * DAY_TIMESPAN;
            }

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

            private Date addToTimeAndReturnDate(long time) {
                Date updatedDate = new Date(time);
                int updatedMonth = updatedDate.getMonth() + 1;
                updatedDate.setMonth(updatedMonth % 12);
                updatedDate.setYear(updatedDate.getYear() + updatedMonth / 12);
                return updatedDate;
            }

            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return TimeUtils.simpleFormat(start, "MMM yyyy");
            }

            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                date.setDate(1);
                return date.getTime();
            }

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

            private Date addToTimeAndReturnDate(long time) {
                Date date = new Date(time);
                date.setYear(date.getYear() + 1);
                return date;
            }

            @Override
            public String intervalLabelFormat(Date start, Date end) {
                return TimeUtils.simpleFormat(start, "yyyy");
            }

            @Override
            public long intervalStart(long time) {
                Date date = new Date(time);
                date.setMonth(0);
                date.setDate(1);
                return date.getTime();
            }

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
         * @return string representation of the given interval
         */
        public String intervalLabelFormat(Date start, Date end) {
            return "(" + start.toString() + ", " + end.toString() + ")";
        }
    }

    IPrimitive<AnalysisResolution> intervalSize();

    IPrimitive<LogicalDate> intervalValue();

    /**
     * @return number of units turned over during this time interval
     */
    @Caption(name = "Number Of Units Turned Over")
    IPrimitive<Integer> unitsTurnedOverAbs();

    /**
     * @return share of units turned over during this time interval
     */
    @Caption(name = "Percentage Of Units Turned Over")
    IPrimitive<Double> unitsTurnedOverPct();

}
