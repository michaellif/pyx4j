/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.access.openId;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.UrlIdentifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.pape.PapeRequest;
import org.openid4java.util.HttpClientFactory;
import org.openid4java.util.ProxyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

public class OpenId {

    private static final Logger log = LoggerFactory.getLogger(OpenId.class);

    static ConsumerManager manager;

    private static String DISCOVERED_ATTRIBUTE = "openid-disc";

    static String returnServletPath = OpenIdServlet.MAPPING;

    static boolean requestAttributes = false;

    public static synchronized String getDestinationUrl(String userDomain) {
        try {
            if (manager == null) {
                String proxyHost = null;// AppConfiguration.getValue("openid", "proxyHost");
                int proxyPort = 0;// AppConfiguration.getValue("openid", "proxyPort", 8080);
                if (CommonsStringUtils.isStringSet(proxyHost)) {
                    ProxyProperties proxyProps = new ProxyProperties();
                    proxyProps.setProxyHostName(proxyHost);
                    proxyProps.setProxyPort(proxyPort);
                    HttpClientFactory.setProxyProperties(proxyProps);
                }
                manager = new ConsumerManager();
            }

            String identifier;

            if (CommonsStringUtils.isStringSet(userDomain)) {
                log.info("authenticate using user domain {}", userDomain);
                identifier = "https://www.google.com/accounts/o8/site-xrds?hd=" + userDomain;
            } else {
                identifier = "https://www.google.com/accounts/o8/id";
            }

            // obtain a AuthRequest message to be sent to the OpenID provider
            List<?> discoveries = manager.discover(identifier);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);
            log.debug("discovered {}", discovered);

            // store the discovery information in the user's session for later use
            // leave out for stateless operation / if there is no session
            Lifecycle.beginAnonymousSession();
            Context.getVisit().setAttribute(DISCOVERED_ATTRIBUTE, discovered);

            AuthRequest authReq = manager.authenticate(discovered, ServerSideConfiguration.instance().getMainApplicationURL() + returnServletPath);

            if (requestAttributes) {
                FetchRequest fetch = FetchRequest.createFetchRequest();
                fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
                fetch.addAttribute("firstname", "http://axschema.org/namePerson/first", true);
                fetch.addAttribute("lastname", "http://axschema.org/namePerson/last", true);
                authReq.addExtension(fetch);
            }

            PapeRequest pape = PapeRequest.createPapeRequest();
            pape.setMaxAuthAge(60 * 60 * 24);
            authReq.addExtension(pape);
            authReq.setImmediate(false);

            MessageExtension uiMessage = new MessageExtension() {

                @Override
                public ParameterList getParameters() {
                    ParameterList params = new ParameterList();
                    //params.set(new Parameter("mode", "popup"));
                    params.set(new Parameter("icon", "true"));
                    return params;
                }

                @Override
                public String getTypeUri() {
                    return "http://specs.openid.net/extensions/ui/1.0";
                }

                @Override
                public boolean providesIdentifier() {
                    return false;
                }

                @Override
                public void setParameters(ParameterList params) {
                }

                @Override
                public boolean signRequired() {
                    return false;
                }
            };

            authReq.addExtension(uiMessage);

            return authReq.getDestinationUrl(true);
        } catch (Throwable e) {
            log.error("Error", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Processing the authentication response
     * 
     * @param request
     * @return E-mail
     */
    public static OpenIdResponse readResponse(HttpServletRequest request, String userDomain) {
        try {
            if (Context.getVisit() == null) {
                log.debug("session is missing");
                return null;
            }
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList responsePrams = new ParameterList(request.getParameterMap());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = (DiscoveryInformation) Context.getVisit().getAttribute(DISCOVERED_ATTRIBUTE);
            if (discovered == null) {
                log.debug("Session terminated");
                return null;
            }
            log.info("has discovered {}", discovered);

            // extract the receiving URL from the HTTP request
            String receivingURL = ServletUtils.getActualRequestURL(request, true);
            log.info("verify the response {}", receivingURL);

            String claimed_id = responsePrams.getParameterValue("openid.claimed_id");
            log.info("verify openid.claimed_id {}", claimed_id);
            // Hack for extended discovery
            if (claimed_id.startsWith("http://" + userDomain + "/openid?id=")) {
                discovered = new DiscoveryInformation(discovered.getOPEndpoint(), new UrlIdentifier(claimed_id));
            }

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(receivingURL, responsePrams, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                if (requestAttributes) {
                    if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                        FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
                        OpenIdResponse openIdResponse = new OpenIdResponse();
                        openIdResponse.email = fetchResp.getAttributeValue("email");
                        openIdResponse.name = CommonsStringUtils.nvl_concat(fetchResp.getAttributeValue("firstname"), fetchResp.getAttributeValue("lastname"),
                                " ");
                        // success
                        return openIdResponse;
                    } else {
                        return null;
                    }
                } else {
                    return new OpenIdResponse();
                }
            } else {
                return null;
            }
        } catch (OpenIDException e) {
            log.error("Error", e);
            return null;
        }
    }
}
