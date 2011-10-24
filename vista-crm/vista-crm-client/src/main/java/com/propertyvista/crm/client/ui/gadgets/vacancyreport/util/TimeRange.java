/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-24
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport.util;

/**
 * Time ranges in milliseconds (some of them, ie. year or month are approximate)
 * 
 * @author artyom
 * 
 */
public final class TimeRange {
    public static final long DAY = 24L * 60L * 60L * 1000L;

    public static final long WEEK = 7L * 24L * 60L * 60L * 1000L;

    /** Warning: 30 days. */
    public static final long MONTH = 30L * 24L * 60L * 60L * 1000L;

    /** Warning: 365 days */
    public static final long YEAR = 365L * 24L * 60L * 60L * 1000L;
}
