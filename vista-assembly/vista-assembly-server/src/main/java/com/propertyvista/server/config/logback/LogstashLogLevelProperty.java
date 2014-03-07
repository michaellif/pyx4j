/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.PropertyDefinerBase;

import com.pyx4j.config.server.ServerSideConfiguration;

public class LogstashLogLevelProperty extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            return Level.OFF.toString();
        } else {
            return Level.DEBUG.toString();
        }
    }

}
