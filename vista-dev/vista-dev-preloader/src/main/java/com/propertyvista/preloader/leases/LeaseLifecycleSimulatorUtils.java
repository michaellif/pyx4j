/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-15
 * @author ArtyomB
 */
package com.propertyvista.preloader.leases;

import java.util.Random;

import com.pyx4j.commons.LogicalDate;

public class LeaseLifecycleSimulatorUtils {

    public static long rndBetween(Random random, long min, long max) {
        assert min <= max;
        if (max == min) {
            return min;
        } else {
            return min + Math.abs(random.nextLong()) % (max - min);
        }
    }

    public static LogicalDate rndBetween(Random random, LogicalDate min, LogicalDate max) {
        return new LogicalDate(rndBetween(random, min.getTime(), max.getTime()));
    }

}
