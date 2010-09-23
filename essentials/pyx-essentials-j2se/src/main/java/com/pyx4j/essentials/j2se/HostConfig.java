/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Aug 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Locale;

public abstract class HostConfig {

    public void configure() {
        configure(getLocalHostName());
    }

    public abstract void configure(String hostName);

    public String getLocalHostName() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostName().toLowerCase(Locale.ENGLISH);
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    public static String getHardwareAddress() {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuilder macAddress = new StringBuilder();
            for (byte b : mac) {
                macAddress.append(String.valueOf(b));
            }
            return macAddress.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void setProxy(String proxyHost, String proxyPort) {
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        System.setProperty("http.noProxyHosts", "localhost");
    }
}
