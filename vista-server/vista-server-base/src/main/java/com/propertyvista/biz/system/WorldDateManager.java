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
 */
package com.propertyvista.biz.system;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.net.time.TimeUDPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

/**
 * This is rdate in java
 */
public class WorldDateManager {

    private static final Logger log = LoggerFactory.getLogger(WorldDateManager.class);

    private static long timedelta = 0;

    private static long remoteTimeEol = 0;

    private final static Lock lock = new ReentrantLock();

    public static Date getWorldTime() {
        syncIfRequired();
        return new Date(System.currentTimeMillis() + timedelta);
    }

    public static Date toWorldTime(Date date) {
        syncIfRequired();
        return new Date(date.getTime() + timedelta);
    }

    /**
     * Only one thread will locked
     */
    static void syncIfRequired() {
        if ((ApplicationMode.offlineDevelopment) || remoteTimeEol <= System.currentTimeMillis()) {
            if (lock.tryLock()) {
                try {
                    getRemoteTime();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static Date getRemoteTime() {
        Date remoteTime = null;
        long start = System.currentTimeMillis();
        TimeUDPClient client = new TimeUDPClient();
        String rdateServer = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).rdateServer();
        if (CommonsStringUtils.isEmpty(rdateServer)) {
            return new Date();
        }
        log.debug("connecting rdateServer {}", rdateServer);
        try {
            // We want to timeout if a response takes longer than 60 seconds
            client.setDefaultTimeout(5 * Consts.SEC2MSEC);
            client.open();
            remoteTime = client.getDate(InetAddress.getByName(rdateServer));
            timedelta = remoteTime.getTime() - System.currentTimeMillis();
            log.debug("RemoteTime {}, timeDelta {} msec, sync {}", remoteTime, timedelta, TimeUtils.secSince(start));
        } catch (Throwable e) {
            log.error("Error getting remote time from {}", rdateServer, e);
        } finally {
            client.close();
        }
        remoteTimeEol = System.currentTimeMillis() + 5 * Consts.MIN2MSEC;
        return remoteTime;
    }
}
