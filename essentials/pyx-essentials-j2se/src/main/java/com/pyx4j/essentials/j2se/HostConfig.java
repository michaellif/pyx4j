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
 */
package com.pyx4j.essentials.j2se;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CompareHelper;

public abstract class HostConfig {

    private final static Logger log = LoggerFactory.getLogger(HostConfig.class);

    public static class ProxyConfig {

        private String host;

        private int port;

        private String user;

        private String password;

        private ProxyConfig() {
        }

        public ProxyConfig(String host, int port, String user, String password) {
            super();
            this.host = host;
            this.port = port;
            this.user = user;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

    }

    protected ProxyConfig proxy;

    protected void configure() {
        configure(getLocalHostName());
    }

    protected abstract void configure(String hostName);

    public static String getLocalHostName() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostName().toLowerCase(Locale.ENGLISH);
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    public static String getLocalHostIP() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostAddress();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    public static String getLocalHostHardwareAddress() {
        byte[] mac;
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface itf = NetworkInterface.getByInetAddress(address);
            mac = itf.getHardwareAddress();
        } catch (IOException e) {
            throw new Error("NetworkInterface not found", e);
        }
        StringBuilder macAddress = new StringBuilder();
        for (byte b : mac) {
            macAddress.append(String.valueOf(b));
        }
        return macAddress.toString();
    }

    public static String getHardwareAddress() {
        final boolean debug = false;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            TreeMap<String, byte[]> macByName = new TreeMap<String, byte[]>();
            while (en.hasMoreElements()) {
                NetworkInterface itf = en.nextElement();
                if (!isUp(itf) || itf.isLoopback() || itf.isVirtual() || (itf.getName() == null) || itf.isPointToPoint()) {
                    continue;
                }
                if ((!itf.getName().startsWith("eth") && !itf.getName().startsWith("bond")) || itf.getInterfaceAddresses().isEmpty()) {
                    continue;
                }
                if (debug) {
                    System.out.println("NetworkInterface: " + itf.getName() + " " + macToString(itf.getHardwareAddress()) + " " + itf.getDisplayName());
                    System.out.println("                  " + itf.getInterfaceAddresses());
                }
                byte[] mac = itf.getHardwareAddress();
                macByName.put(itf.getName(), mac);
            }
            if (macByName.isEmpty()) {
                throw new Error("NetworkInterface not found");
            } else {
                byte[] mac = macByName.firstEntry().getValue();
                StringBuilder macAddress = new StringBuilder();
                for (byte b : mac) {
                    macAddress.append(String.valueOf(b));
                }
                return macAddress.toString();
            }
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static String getNetworkInfo() {
        StringBuilder b = new StringBuilder();
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            List<NetworkInterface> interfaces = new ArrayList<>();
            while (en.hasMoreElements()) {
                NetworkInterface itf = en.nextElement();
                interfaces.add(itf);
            }
            Collections.sort(interfaces, new Comparator<NetworkInterface>() {
                @Override
                public int compare(NetworkInterface i1, NetworkInterface i2) {
                    int cmp = Boolean.valueOf(isUp(i2)).compareTo(Boolean.valueOf(isUp(i1)));
                    if (cmp != 0) {
                        return cmp;
                    } else {
                        return CompareHelper.compareTo(i1.getName(), i2.getName());
                    }
                }
            });

            for (NetworkInterface itf : interfaces) {
                b.append("\t").append(itf.getName()).append(" ")//
                        .append("Up: ").append(itf.isUp()).append(" \n\t\t");

                try {
                    b.append("Loopback: ").append(itf.isLoopback()).append(", ")//
                            .append("Virtual: ").append(itf.isVirtual()).append(", ")//
                            .append("PointToPoint: ").append(itf.isPointToPoint()).append(", ")//
                            .append("SupportsMulticast: ").append(itf.supportsMulticast()).append(" \n\t\t")//
                            .append("Index: ").append(itf.getIndex()).append(", ")//
                            .append("MTU: ").append(itf.getMTU()).append(" \n\t\t")//
                            .append("MAC: ").append(macToString(itf.getHardwareAddress())).append(", ").append(itf.getDisplayName());

                    b.append(" InterfaceAddresses ").append(itf.getInterfaceAddresses()).append("\n");
                } catch (Throwable e) {
                    log.error("Unable to read NetworkInfo", e);
                    b.append("\n");
                }
            }
        } catch (Throwable e) {
            log.error("Unable to read NetworkInfo ", e);
            b.append(e);
        }
        return b.toString();
    }

    static boolean isUp(NetworkInterface itf) {
        try {
            return itf.isUp();
        } catch (Throwable e) {
            return false;
        }
    }

    static String macToString(byte[] mac) {
        if (mac == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(18);
        for (byte b : mac) {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void setProxy(String proxyHost, String proxyPort) {
        setProxy(proxyHost, Integer.valueOf(proxyPort));
    }

    public void setProxy(String proxyHost, int proxyPort) {
        this.proxy = new ProxyConfig();
        this.proxy.host = proxyHost;
        this.proxy.port = proxyPort;
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", String.valueOf(proxyPort));
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", String.valueOf(proxyPort));
        System.setProperty("http.noProxyHosts", "localhost");
    }

    public void setProxy(String proxyHost, int proxyPort, String user, String password) {
        this.setProxy(proxyHost, proxyPort);
        this.proxy.user = user;
        this.proxy.password = password;
    }

    public void setProxy(ProxyConfig proxy) {
        this.proxy = proxy;
    }

    public ProxyConfig getProxyConfig() {
        return proxy;
    }

}
