/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy.mk2;

import com.pyx4j.commons.LogicalDate;

public class UnitOccupancyConstants {

    /**
     * The minimum value for occupancy segment starting date (kind of negative infinity)
     */
    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    /**
     * The maximum value of occupancy segment end date (kind of positive infinity)
     */
    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-1-1

}
