/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 21, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.util.Date;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.gwt.server.DateUtils;

public class SysDateManager {

    public static void setSysDate(Date date) {
        SystemDateManager.setDate(date);
    }

    public static void setSysDate(String dateStr) {
        setSysDate(DateUtils.detectDateformat(dateStr));
    }

    public static Date getSysDate() {
        return SystemDateManager.getDate();
    }

}
