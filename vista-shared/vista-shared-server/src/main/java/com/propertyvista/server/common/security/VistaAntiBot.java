/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.ReCaptchaAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;

public class VistaAntiBot extends ReCaptchaAntiBot {

    private static final I18n i18n = I18n.get(VistaAntiBot.class);

    public static String REQUEST_IP_REQUEST_ATR = "com.propertyvista.api.RequestRemoteAddr";

    public static String API_TARGET_REQUEST_ATR = "com.propertyvista.api.ApiRequestTarget";

    private final static Logger log = LoggerFactory.getLogger(VistaAntiBot.class);

    @Override
    public String loginFailedMessage() {
        return i18n.tr("The email or password you entered is incorrect.");
    }

    @Override
    public void assertCaptcha(String challenge, String response) {
        if (ServerSideConfiguration.instance().isDevelopmentBehavior() && "x".equals(response)) {
            log.debug("Development CAPTCHA Ok");
        } else {
            super.assertCaptcha(challenge, response);
        }
    }

    @Override
    protected String getRequestRemoteAddr() {
        Object ip = ServerContext.getRequest().getAttribute(REQUEST_IP_REQUEST_ATR);
        if (ip != null) {
            return ip.toString();
        } else {
            return ServerContext.getRequestRemoteAddr();
        }
    }

    public static void setApiRequestDnsNameTarget(DnsNameTarget target) {
        ServerContext.getRequest().setAttribute(API_TARGET_REQUEST_ATR, target);
    }

    public static String getRequestServerNameBase() {
        DnsNameTarget target = (DnsNameTarget) ServerContext.getRequest().getAttribute(API_TARGET_REQUEST_ATR);
        if (target == null) {
            String host = ServerContext.getRequestServerName();
            String[] parts = host.split("\\.");
            if (parts.length >= 3) {
                return parts[parts.length - 2] + "." + parts[parts.length - 1];
            } else {
                return "birchwoodsoftwaregroup.com";
            }
        } else {
            if (!VistaDeployment.isVistaProduction()) {
                return "birchwoodsoftwaregroup.com";
            } else {
                switch (target) {
                case crm:
                    return "propertyvista.com";
                case portal:
                    return "my-community.co";
                default:
                    return "propertyvista.com";
                }
            }
        }
    }
}
