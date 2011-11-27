/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

/**
 * This class makes demo data for now
 * 
 */
public class TenantMaintenanceDAO {

    static List<MaintananceDTO> demoIssues(Object[][] maintanances) {
        List<MaintananceDTO> r = new Vector<MaintananceDTO>();

        Random random = new Random();
        random.setSeed(12);
        for (int i = 0; i < maintanances.length; i++) {
            MaintananceDTO m = EntityFactory.create(MaintananceDTO.class);
            m.status().setValue((MaintenanceRequestStatus) maintanances[i][0]);
            m.description().setValue((String) maintanances[i][1]);
            m.date().setValue(new LogicalDate((Date) maintanances[i][2]));
            m.satisfactionSurvey().rating().setValue(random.nextInt(6));
            r.add(m);
        }
        return r;
    }

    static List<MaintananceDTO> getRecentIssues() {

        return demoIssues(new Object[][] {

        { MaintenanceRequestStatus.Submitted, "Leacking Kitchen Tap", new GregorianCalendar(2011, 9, 28).getTime() },

        { MaintenanceRequestStatus.Scheduled, "Broken Blinds", new GregorianCalendar(2011, 9, 22).getTime() },

        { MaintenanceRequestStatus.Completed, "Door Lock is Broken", new GregorianCalendar(2011, 8, 28).getTime() }

        });

    }

    static List<MaintananceDTO> getOpenIssues() {

        return demoIssues(new Object[][] {

        { MaintenanceRequestStatus.Submitted, "Leacking Kitchen Tap", new GregorianCalendar(2011, 9, 28).getTime() },

        { MaintenanceRequestStatus.Scheduled, "Broken Blinds", new GregorianCalendar(2011, 9, 22).getTime() },

        { MaintenanceRequestStatus.Scheduled, "Door Lock is Broken", new GregorianCalendar(2011, 8, 28).getTime() }

        });

    }

    static List<MaintananceDTO> getHistoryIssues() {

        return demoIssues(new Object[][] {

        { MaintenanceRequestStatus.Completed, "Leacking Kitchen Tap", new GregorianCalendar(2011, 6, 28).getTime() },

        { MaintenanceRequestStatus.Completed, "Broken Blinds", new GregorianCalendar(2011, 5, 22).getTime() },

        { MaintenanceRequestStatus.Canceled, "Door Lock is Broken", new GregorianCalendar(2011, 4, 28).getTime() },

        { MaintenanceRequestStatus.Completed, "Broken Blinds", new GregorianCalendar(2011, 4, 3).getTime() },

        { MaintenanceRequestStatus.Completed, "Door Lock is Broken", new GregorianCalendar(2011, 3, 12).getTime() }

        });

    }
}
