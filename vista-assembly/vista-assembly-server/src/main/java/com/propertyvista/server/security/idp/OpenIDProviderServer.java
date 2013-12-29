/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.idp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.DirectError;
import org.openid4java.message.IndirectError;
import org.openid4java.message.Message;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.openid4java.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.crm.rpc.dto.account.GlobalLoginResponseDTO;

public class OpenIDProviderServer {

    private static final Logger log = LoggerFactory.getLogger(OpenIDProviderServer.class);

    private static final I18n i18n = I18n.get(OpenIDProviderServer.class);

    private enum OpenIDMode {
        associate, checkid_setup, checkid_immediate, check_authentication, unknown
    };

    // instantiate a ServerManager object
    private final ServerManager manager = new ServerManager();

    static String getLocalId() {
        String domain = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdProviderDomain();
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdDomainIdentifier(domain);
    }

    static String getOPEndpointUrl() {
        return getLocalId().replace("/idp", "/endpoint");
    }

    public OpenIDProviderServer() {
        manager.setOPEndpointUrl(getOPEndpointUrl());
        // for a working demo, not enforcing RP realm discovery
        // since this new feature is not deployed
        manager.getRealmVerifier().setEnforceRpId(false);
    }

    public String processRequest(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException, OpenIDException, ServletException {
        // extract the parameters from the request
        ParameterList request = new ParameterList(httpReq.getParameterMap());

        String modeStr = request.hasParameter("openid.mode") ? request.getParameterValue("openid.mode") : null;

        if (modeStr == null) {
            httpResp.setContentType("application/xrds+xml");
            String id = request.getParameterValue("id");
            directResponse(httpResp, IdpXrdsServlet.xrdsXml(DiscoveryInformation.OPENID2, OpenIDProviderServer.getOPEndpointUrl()));
            return null;
        }

        OpenIDMode mode = OpenIDMode.unknown;
        try {
            mode = OpenIDMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
        }

        log.info("** IDP requested [{}]", mode);

        Message response;
        String responseText = null;

        switch (mode) {
        case associate:
            // --- process an association request ---
            response = manager.associationResponse(request);
            responseText = response.keyValueFormEncoding();
            directResponse(httpResp, response.keyValueFormEncoding());
            break;
        case checkid_setup:
        case checkid_immediate:
            // interact with the user and obtain data needed to continue
            UserData userData = userInteraction(httpReq, request);
            if (userData == null) {
                log.info("** IDP show login");
                showLoginForm(httpReq, httpResp);
                return null;
            }

            // --- process an authentication request ---
            AuthRequest authReq = AuthRequest.createAuthRequest(request, manager.getRealmVerifier());

            String opLocalId = getOPEndpointUrl();
            String userSelectedClaimedId = getOPEndpointUrl();

            response = manager.authResponse(request, opLocalId, userSelectedClaimedId, userData.authenticatedAndApproved, false); // Sign after we added extensions.

            if (response instanceof DirectError) {
                log.info("** IDP return error [{}]", ((DirectError) response).getErrorMsg());
                return directResponse(httpResp, response.keyValueFormEncoding());
            } else if (response instanceof IndirectError) {
                log.info("** IDP send error redirect [{}]", response.getDestinationUrl(true));
                httpResp.sendRedirect(response.getDestinationUrl(true));
            } else {
                if (authReq.hasExtension(AxMessage.OPENID_NS_AX)) {
                    MessageExtension ext = authReq.getExtension(AxMessage.OPENID_NS_AX);
                    if (ext instanceof FetchRequest) {
                        FetchRequest fetchReq = (FetchRequest) ext;
                        Map required = fetchReq.getAttributes(true);
                        //Map optional = fetchReq.getAttributes(false);
                        if (required.containsKey("email")) {
                            Map userDataExt = new HashMap();
                            //userDataExt.put("email", userData.get(3));

                            FetchResponse fetchResp = FetchResponse.createFetchResponse(fetchReq, userDataExt);
                            // (alternatively) manually add attribute values
                            fetchResp.addAttribute("email", "http://schema.openid.net/contact/email", userData.email);
                            response.addExtension(fetchResp);
                        }
                    } else //if (ext instanceof StoreRequest)
                    {
                        throw new UnsupportedOperationException("TODO");
                    }
                }
                if (authReq.hasExtension(SRegMessage.OPENID_NS_SREG)) {
                    MessageExtension ext = authReq.getExtension(SRegMessage.OPENID_NS_SREG);
                    if (ext instanceof SRegRequest) {
                        SRegRequest sregReq = (SRegRequest) ext;
                        List required = sregReq.getAttributes(true);
                        //List optional = sregReq.getAttributes(false);
                        if (required.contains("email")) {
                            // data released by the user
                            Map userDataSReg = new HashMap();
                            //userData.put("email", "user@example.com");

                            SRegResponse sregResp = SRegResponse.createSRegResponse(sregReq, userDataSReg);
                            // (alternatively) manually add attribute values
                            sregResp.addAttribute("email", userData.email);
                            response.addExtension(sregResp);
                        }
                    } else {
                        throw new UnsupportedOperationException("TODO");
                    }
                }

                // Sign the auth success message.
                // This is required as AuthSuccess.buildSignedList has a `todo' tag now.
                manager.sign((AuthSuccess) response);

                // caller will need to decide which of the following to use:

                // option1: GET HTTP-redirect to the return_to URL
                //return response.getDestinationUrl(true);

                log.info("** IDP sendRedirect [{}]", response.getDestinationUrl(true));
                httpResp.sendRedirect(response.getDestinationUrl(true));

                // option2: HTML FORM Redirection
                //RequestDispatcher dispatcher =
                //        getServletContext().getRequestDispatcher("formredirection.jsp");
                //httpReq.setAttribute("prameterMap", response.getParameterMap());
                //httpReq.setAttribute("destinationUrl", response.getDestinationUrl(false));
                //dispatcher.forward(request, response);
                //return null;
            }
            break;
        case check_authentication:
            // --- processing a verification request ---
            response = manager.verify(request);
            //responseText = response.keyValueFormEncoding();
            directResponse(httpResp, response.keyValueFormEncoding());
            break;
        default:
            // --- error response ---
            response = DirectError.createDirectError("Unknown request");
            //responseText = response.keyValueFormEncoding();
            directResponse(httpResp, response.keyValueFormEncoding());
        }

        // return the result to the user
        return responseText;
    }

    private void showLoginForm(HttpServletRequest httpReq, HttpServletResponse response) throws IOException {
        String body = IOUtils.getTextResource("login.html", OpenIDProviderServer.class);

        body = body.replace("${message}", (String) httpReq.getAttribute("message"));

        response.setContentType("text/html");
        response.setContentLength(body.length());

        ServletOutputStream os = response.getOutputStream();
        os.write(body.getBytes());
        os.close();
    }

    protected UserData userInteraction(HttpServletRequest httpReq, ParameterList request) throws OpenIDException {
        httpReq.setAttribute("message", "");
        if (!httpReq.getMethod().equals("POST")) {
            return null;

        }
        GlobalLoginResponseDTO userInfo = ServerSideFactory.create(UserManagementFacade.class).globalFindAndVerifyCrmUser(httpReq.getParameter("j_username"),
                httpReq.getParameter("j_password"));

        if (userInfo != null) {
            UserData userData = new UserData();
            userData.authenticatedAndApproved = true;
            userData.email = userInfo.user().email().getValue();
            return userData;
        } else {
            return null;
        }
    }

    private String directResponse(HttpServletResponse httpResp, String response) throws IOException {
        ServletOutputStream os = httpResp.getOutputStream();
        os.write(response.getBytes());
        os.close();
        return null;
    }
}
