/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.mail;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.rpc.pt.ActivationServices;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.shared.meta.NavigNode;

public class MessageTemplates {

    private static I18n i18n = I18nFactory.getI18n();

    private static final Logger log = LoggerFactory.getLogger(MessageTemplates.class);

    public static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    public static String wrapHtml(String text) {
        try {
            String html = IOUtils.getTextResource("email/template-basic.html");
            return html.replace("{MESSAGE}", text);
        } catch (IOException e) {
            log.error("template error", e);
            return text;
        }
    }

    public static String createPasswordResetEmail(String name, String token) {
        return wrapHtml(i18n.tr(
                "Dear {0},<br/>\n" + "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n"
                        + "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n"
                        + "    <a style=\"color:#929733\" href=\"{1}\">Change Your Password</a>",
                name,

                absoluteUrl(ServerSideConfiguration.instance().getMainApplicationURL(), AppPlaceInfo.getPlaceId(SiteMap.RetrievePassword.class),
                        ActivationServices.PASSWORD_TOKEN, token)));
    }

    public static String absoluteUrl(String appUrl, String node, String... encodedComponentsNameValue) {
        StringBuilder b = new StringBuilder();
        b.append(appUrl);
        if (node != null) {
            b.append("#");
            b.append(node);
        }
        if (encodedComponentsNameValue != null) {
            boolean first = true;
            boolean name = true;
            for (String encodedComponent : encodedComponentsNameValue) {
                if (first) {
                    b.append(NavigNode.ARGS_GROUP_SEPARATOR);
                    first = false;
                } else if (name) {
                    b.append(NavigNode.ARGS_SEPARATOR);
                } else {
                    b.append(NavigNode.NAME_VALUE_SEPARATOR);
                }
                name = !name;
                b.append(encodedComponent);
            }
        }
        return b.toString();
    }

}
