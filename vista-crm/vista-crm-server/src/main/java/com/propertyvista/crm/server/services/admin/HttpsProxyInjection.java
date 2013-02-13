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
package com.propertyvista.crm.server.services.admin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class HttpsProxyInjection {

    public static String injectionPortalHttps(String html) {
        Document doc = Jsoup.parse(html);

        String baseUrl = VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.portalInectionProxy;

        Elements imports = doc.select("link[href]");
        for (Element link : imports) {
            link.attr("href", correction(baseUrl, link.attr("abs:href")));
        }

        Elements media = doc.select("[src]");
        for (Element src : media) {
            src.attr("src", correction(baseUrl, src.attr("abs:src")));
        }
        DataDump.dump("injected-html", doc.html());
        return doc.html();
    }

    private static String correction(String baseUrl, String href) {
        if (href.startsWith("http://")) {
            return DeploymentConsts.portalInectionProxy + href.substring(7);
        }
        return href;
    }
}
