/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.proxy;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class HttpsProxyInjection {

    private static final String[] knownSecureHosts = new String[] {//@formatter:off
        ".google.com",
        ".googleapis.com",
        ".googlecode.com",
        ".addthis.com"
    };//@formatter:on

    public static String injectionPortalHttps(String html) {
        Document doc = Jsoup.parse(html);

        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            link.attr("href", correction(link.attr("abs:href")));
        }

        Elements media = doc.select("[src]");
        for (Element src : media) {
            src.attr("src", correction(src.attr("abs:src")));
        }
        DataDump.dump("injected-html", doc.html());
        return doc.html();
    }

    public static Collection<String> generateWhitelist(String html) {
        Document doc = Jsoup.parse(html);

        Set<String> whiteList = new HashSet<String>();

        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            addHost(link.attr("abs:href"), whiteList);
        }

        Elements media = doc.select("[src]");
        for (Element src : media) {
            addHost(src.attr("abs:src"), whiteList);
        }
        return whiteList;
    }

    private static String correction(String href) {
        if (href.startsWith("http://")) {
            if (hasSecureHost(href)) {
                return "https://" + href.substring(7);
            } else {
                return DeploymentConsts.portalInectionProxy + href.substring(7);
            }
        }
        return href;
    }

    private static boolean hasSecureHost(String href) {
        try {
            URL url = new URL(href);
            if (url != null && url.getHost() != null) {
                return isSecureHost(url.getHost());
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    private static boolean isSecureHost(String host) {
        for (String secure : knownSecureHosts) {
            if (host.endsWith(secure)) {
                return true;
            }
        }
        return false;
    }

    private static void addHost(String href, Set<String> list) {
        if (href.startsWith("http://")) {
            try {
                URL url = new URL(href);
                if (url != null && url.getHost() != null && !isSecureHost(url.getHost())) {
                    list.add(url.getHost().toLowerCase(Locale.ENGLISH));
                }
            } catch (Exception ignore) {
            }
        }
    }
}
