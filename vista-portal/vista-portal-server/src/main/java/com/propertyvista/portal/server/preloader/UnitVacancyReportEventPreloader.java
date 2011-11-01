/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.HashMap;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportEvent;

public class UnitVacancyReportEventPreloader extends BaseVistaDevDataPreloader {

    public static final String DATA_SOURCE_FILE = "unit-vacancy-report-events.csv";

    @Override
    public String create() {
        final StringBuilder creationReport = new StringBuilder();
        final HashMap<String, Integer> indexOf = new HashMap<String, Integer>();
        final int[] events = new int[] { 0 };

        try {
            CSVLoad.loadFile(IOUtils.resourceFileName(DATA_SOURCE_FILE, UnitVacancyReportDTOPreloader.class), new CSVReciver() {
                int headersLength = 0;

                @Override
                public void onRow(String[] rawValues) {
                    String[] values = new String[headersLength];
                    // make all the fields exist (even if some of them are null
                    int minLength = rawValues.length < headersLength ? rawValues.length : headersLength;
                    for (int i = 0; i < minLength; ++i) {
                        values[i] = rawValues[i];
                        if ("".equals(values[i])) {
                            values[i] = null;
                        }
                    }

                    UnitVacancyReportEvent event = EntityFactory.create(UnitVacancyReportEvent.class);

                    String strVal;

                    strVal = values[indexOf.get("date")];
                    event.eventDate().setValue(UnitVacancyReportDTOPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("propertyCode")];
                    event.propertyCode().setValue(strVal);

                    strVal = values[indexOf.get("unit#")];
                    event.unit().setValue(strVal);

                    strVal = values[indexOf.get("event")];
                    event.eventType().setValue(strVal);

                    strVal = values[indexOf.get("rentready")];
                    event.rentReady().setValue(strVal);

                    strVal = values[indexOf.get("move out date")];
                    event.moveOutDate().setValue(UnitVacancyReportDTOPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("move in date")];
                    event.moveInDate().setValue(UnitVacancyReportDTOPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("rent from date")];
                    event.moveInDate().setValue(UnitVacancyReportDTOPreloader.toLogicalDate(strVal));

                    Persistence.service().persist(event);
                    ++events[0];
                }

                @Override
                public void onHeader(String[] headers) {
                    headersLength = headers.length;
                    for (int i = 0; i < headers.length; i++) {
                        indexOf.put(headers[i], i);
                    }
                }

                @Override
                public boolean canContuneLoad() {
                    return true;
                }
            });

        } catch (Exception e) {
            creationReport.append("failed to fill UnitVacancyReportEvent due to ").append(e).append("; ");
        } finally {
            creationReport.append("Created ").append(events[0]).append(" mockup unit events (for UnitVacancyReportEvent)");
        }
        return creationReport.toString();
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(UnitVacancyReportEvent.class);
        } else {
            return "This is production";
        }
    }

}
