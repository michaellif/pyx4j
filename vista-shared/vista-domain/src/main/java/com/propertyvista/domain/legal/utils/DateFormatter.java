/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;

import java.util.Date;

import com.pyx4j.commons.SimpleMessageFormat;

public class DateFormatter implements Formatter {

    @Override
    public String format(Object object) {
        Date value = (Date) object;
        return SimpleMessageFormat.format("{0,date,dd/MM/YYYY}", value);
    }

}
