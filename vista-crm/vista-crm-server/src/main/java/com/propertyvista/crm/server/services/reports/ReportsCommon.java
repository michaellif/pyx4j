/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.text.SimpleDateFormat;

/**
 * 
 *
 */
public class ReportsCommon {

    public static ReportsCommon instance() {
        return new ReportsCommon();
    }

    private ReportsCommon() {
    }

    public SimpleDateFormat getAsOfDateFormat() {
        return new SimpleDateFormat("dd/MMM/yyyy");
    }
}
