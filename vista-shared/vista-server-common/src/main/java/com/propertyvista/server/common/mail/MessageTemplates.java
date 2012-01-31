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
package com.propertyvista.server.common.mail;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.policies.specials.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class MessageTemplates {

    private static final I18n i18n = I18n.get(MessageTemplates.class);

    private static final Logger log = LoggerFactory.getLogger(MessageTemplates.class);

    public static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    public static EmailTemplate getEmailTEmplate(EmailTemplateType type, Building building) {
        //TODO Use policy framework to get the HTML text
        return null;
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

    public static String createPasswordResetEmail(VistaBasicBehavior application, String name, String token) {
        String url;
        switch (application) {
        case CRM:
            url = ServerSideConfiguration.instance().getMainApplicationURL()
                    + AppPlaceInfo.absoluteUrl(DeploymentConsts.CRM_URL, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);
            break;
        case ProspectiveApp:
            url = ServerSideConfiguration.instance().getMainApplicationURL()
                    + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, PtSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token);
            break;
        default:
            throw new Error("TODO");
        }

        return wrapHtml(i18n.tr("Dear {0},<br/>\n"
                + "This email was sent to you in response to your request to modify your Property Vista account password.<br/>\n"
                + "Click the link below to go to the Property Vista site and create new password for your account:<br/>\n"
                + "    <a style=\"color:#929733\" href=\"{1}\">Change Your Password</a>", name, url));
    }

    public static String createMasterApplicationInvitationEmail(String name, String token) {
        String url = ServerSideConfiguration.instance().getMainApplicationURL()
                + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, PtSiteMap.PasswordReset.class, AuthenticationService.AUTH_TOKEN_ARG, token);

        return wrapHtml(i18n.tr("Dear {0},<br/>\n" + "This email was sent to you in response to your request to apply for Property Vista apartments.<br/>\n"
                + "Click the link below to go to the Property Vista site:<br/>\n" + "    <a style=\"color:#929733\" href=\"{1}\">Application</a>", name, url));
    }
}
