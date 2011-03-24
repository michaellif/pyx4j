/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-03-24
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.selenium.tests;

import java.net.InetAddress;
import java.util.Enumeration;

import org.openqa.selenium.networkutils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;

public class VerifyNetwork {

    private final static Logger log = LoggerFactory.getLogger(VerifyNetwork.class);

    public static void main(String[] args) throws Exception {
        debugTime();
        boolean testSelenium = true;
        if (testSelenium) {
            long start = System.currentTimeMillis();
            NetworkUtils networkUtils = new NetworkUtils();
            log.info("Initialization time {}", TimeUtils.secSince(start));
            log.info("NonLoopbackAddressOfThisMachine: {}", networkUtils.getNonLoopbackAddressOfThisMachine());
            //System.out.println(NetworkUtils.getNetWorkDiags());
        }
    }

    static void debugTime() throws Exception {
        long start = System.currentTimeMillis();
        Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            java.net.NetworkInterface ni = interfaces.nextElement();
            log.info("{} {}", TimeUtils.secSince(start), ni.getDisplayName());
            Enumeration<InetAddress> en = ni.getInetAddresses();
            while (en.hasMoreElements()) {
                InetAddress inetAddress = en.nextElement();
                log.info("  {} {}", TimeUtils.secSince(start), inetAddress.getHostName());
            }
        }
    }
}
