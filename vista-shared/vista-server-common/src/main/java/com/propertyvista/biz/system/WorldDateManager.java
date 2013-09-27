/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.net.InetAddress;
import java.util.Date;

import org.apache.commons.net.time.TimeUDPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

/**
 * This is rdate in java
 */
public class WorldDateManager {

    private static final Logger log = LoggerFactory.getLogger(WorldDateManager.class);

    private static long timedelta = 0;

    private static long remoteTimeEol = 0;

    public static Date getWorldTime() {
        long now = System.currentTimeMillis();
        if (remoteTimeEol <= now) {
            getRemoteTime();
            return new Date(System.currentTimeMillis() + timedelta);
        } else {
            return new Date(now + timedelta);
        }
    }

    public static Date getRemoteTime() {
        Date remoteTime = null;
        long start = System.currentTimeMillis();
        TimeUDPClient client = new TimeUDPClient();
        try {
            // We want to timeout if a response takes longer than 60 seconds
            client.setDefaultTimeout(60 * Consts.SEC2MSEC);
            client.open();
            remoteTime = client.getDate(InetAddress.getByName(ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).rdateServer()));
            timedelta = remoteTime.getTime() - System.currentTimeMillis();
            log.debug("RemoteTime {}, timeDelta {} msec, sync {}", remoteTime, timedelta, TimeUtils.secSince(start));
        } catch (Throwable e) {
            log.error("Error getting remote time", e);
        } finally {
            client.close();
        }
        remoteTimeEol = System.currentTimeMillis() + 5 * Consts.MIN2MSEC;
        return remoteTime;
    }
}
