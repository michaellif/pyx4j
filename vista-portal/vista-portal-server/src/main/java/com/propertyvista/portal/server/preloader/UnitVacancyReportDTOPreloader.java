/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.sql.Date;
import java.util.HashMap;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus.RentReady;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus.VacancyStatus;

public class UnitVacancyReportDTOPreloader extends BaseVistaDevDataPreloader {

    public static final String DATA_SOURCE_FILE = "unit-vacancy-report.csv";

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(UnitVacancyStatus.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        final HashMap<String, Integer> memberToIndexMap = new HashMap<String, Integer>();
        StringBuilder creationReport = new StringBuilder();
        final int[] units = new int[] { 0 }; // if no boxing come to Muhammad, Muhammad comes to boxing... 

        try {
            //CSVLoad.loadFile(this.getClass().getResourceAsStream(DATA_SOURCE_FILE), new CSVReciver() {
            CSVLoad.loadFile(IOUtils.resourceFileName(DATA_SOURCE_FILE, UnitVacancyReportDTOPreloader.class), new CSVReciver() {
                int headersLength = 0;

                @Override
                public void onRow(String[] rawValues) {
                    String[] values = new String[headersLength];
                    // make all the fields exist (even if some of them are null
                    for (int i = 0; i < rawValues.length; ++i) {
                        values[i] = rawValues[i];
                    }
                    String strValue;

                    UnitVacancyStatus record = EntityFactory.create(UnitVacancyStatus.class);

                    record.propertyCode().setValue(values[memberToIndexMap.get("propertyCode")]);

                    record.buildingName().setValue(values[memberToIndexMap.get("buildingName")]);

                    record.address().setValue(values[memberToIndexMap.get("address")]);

                    record.region().setValue(values[memberToIndexMap.get("region")]);

                    record.owner().setValue(values[memberToIndexMap.get("ownership")]);

                    record.propertyManager().setValue(values[memberToIndexMap.get("pm")]);

                    record.complexName().setValue(values[memberToIndexMap.get("complexName")]);

                    record.unit().setValue(values[memberToIndexMap.get("unit")]);

                    record.floorplanName().setValue(values[memberToIndexMap.get("floorplan")]);

                    // TODO no in the data but appears in the description
                    //record.floorplanMarketingName().setValue(values[memberToIndexMap.get("propertyManager")]);

                    strValue = values[memberToIndexMap.get("notice/vacant")];
                    VacancyStatus vacancyStatus = "vacant".equals(strValue) ? VacancyStatus.Vacant : "notice".equals(strValue) ? VacancyStatus.Notice : null;
                    record.vacancyStatus().setValue(vacancyStatus);

                    strValue = values[memberToIndexMap.get("rented/not rented")];
                    RentedStatus rentedStatus = "rented".equals(strValue) ? RentedStatus.Rented : "not rented".equals(strValue) ? RentedStatus.Unrented
                            : "off market".equals(strValue) ? RentedStatus.OffMarket : null;
                    record.rentedStatus().setValue(rentedStatus);

                    strValue = values[memberToIndexMap.get("scoped/not scoped")];
                    Boolean isScoped = "scoped".equals(strValue) ? Boolean.TRUE : "notscoped".equals(strValue) ? Boolean.FALSE : null;
                    record.isScoped().setValue(isScoped);

                    UnitVacancyStatus.RentReady rentReady;
                    rentReady = "rentready".equals(values[memberToIndexMap.get("rentready")]) ? RentReady.RentReady : "reno in progress"
                            .equals(values[memberToIndexMap.get("rentready")]) ? RentReady.RenoInProgress : "needs repairs".equals(values[memberToIndexMap
                            .get("rentready")]) ? RentReady.NeedRepairs : null;
                    record.rentReady().setValue(rentReady);

                    record.unitRent().setValue(Double.parseDouble(values[memberToIndexMap.get("unit rent")]));
                    record.marketRent().setValue(Double.parseDouble(values[memberToIndexMap.get("market rent")])); // TODO take market rent from service catalogue

                    LogicalDate moveOut = toLogicalDate(values[memberToIndexMap.get("move out")]);
                    record.moveOutDay().setValue(moveOut);

                    LogicalDate moveIn = toLogicalDate(values[memberToIndexMap.get("move in")]);
                    record.moveInDay().setValue(moveIn);

                    LogicalDate rentedFromDate = toLogicalDate(values[memberToIndexMap.get("rent from")]);
                    record.rentedFromDate().setValue(rentedFromDate);

                    Persistence.service().persist(record);
                    ++units[0];
                }

                @Override
                public void onHeader(String[] headers) {
                    headersLength = headers.length;
                    for (int i = 0; i < headers.length; i++) {
                        memberToIndexMap.put(headers[i], i);
                    }
                }

                @Override
                public boolean canContuneLoad() {
                    return true;
                }
            });
        } catch (Exception e) {
            creationReport.append("failed to fill UnitVacancyReportDTO due to ").append(e).append("; ");
        } finally {
            creationReport.append("Created ").append(units[0]).append(" mockup units (for UnitVacancyReport)");
        }

        return creationReport.toString();
    }

    /** Convert "mm/dd/yyyy" format into yyyy-mm-dd" (correct input is expected) */
    public static String toSQLDate(String date) {
        final String IN_SEP = "/";
        final String OUT_SEP = "-";

        if (date == null)
            return null;

        String[] splitDate = date.split(IN_SEP);

        // enforce two digit format
        splitDate[0] = splitDate[0].length() == 1 ? "0" + splitDate[0] : splitDate[0];
        splitDate[1] = splitDate[1].length() == 1 ? "0" + splitDate[1] : splitDate[1];

        return splitDate[2] + OUT_SEP + splitDate[0] + OUT_SEP + splitDate[1];
    }

    /** Convert "mm/dd/yyyy" format into {@link LogicalDate} */
    public static LogicalDate toLogicalDate(String date) {

        return date != null ? new LogicalDate(Date.valueOf((toSQLDate(date)))) : null;
    }
}
