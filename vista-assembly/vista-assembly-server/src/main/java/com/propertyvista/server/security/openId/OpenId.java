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
 */
package com.propertyvista.server.security.openId;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.UrlIdentifier;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.pape.PapeRequest;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.openid4java.server.RealmVerifierFactory;
import org.openid4java.util.HttpClientFactory;
import org.openid4java.util.HttpFetcherFactory;
import org.openid4java.util.ProxyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.server.contexts.DevSession;

import com.propertyvista.biz.system.dev.TimeShiftX509TrustManager;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;

public class OpenId {

    private static final Logger log = LoggerFactory.getLogger(OpenId.class);

    static ConsumerManager manager;

    private static String DISCOVERED_ATTRIBUTE = "openid-disc";

    static String returnServletPath = OpenIdServlet.MAPPING.substring(1);

    static boolean requestEmailAttributes = true;

    static boolean requestNameAttributes = false;

    @SuppressWarnings("unchecked")
    public static synchronized String getDestinationUrl(String userDomain, String mainApplicationURL) {
        try {
            if (manager == null) {
                ProxyConfig proxy = SystemConfig.instance().getProxyConfig();
                if (proxy != null) {
                    ProxyProperties proxyProps = new ProxyProperties();
                    proxyProps.setProxyHostName(proxy.getHost());
                    proxyProps.setProxyPort(proxy.getPort());
                    HttpClientFactory.setProxyProperties(proxyProps);
                }
                HttpFetcherFactory httpFetcherFactory = new HttpFetcherFactory(TimeShiftX509TrustManager.createTimeShiftSSLContext());
                Discovery discovery = new Discovery(new HtmlResolver(httpFetcherFactory), new YadisResolver(httpFetcherFactory), Discovery.getXriResolver());
                manager = new ConsumerManager(new RealmVerifierFactory(new YadisResolver(httpFetcherFactory)), discovery, httpFetcherFactory);
                manager.setNonceVerifier(new TimeShiftInMemoryNonceVerifier());
            }

            String identifier = getDomainIdentifier(userDomain);

//            List<DiscoveryInformation> discoveries;
//            if (isCrowdIdentification(identifier)) {
//                // Create our own discovery information point when working with production and problem with http - https
//                // since server response with 301 ERROR, resouce moved to https
//                org.openid4java.discovery.DiscoveryInformation discovery = new DiscoveryInformation(new URL("https://crowd-test.devpv.com/openidserver/op"));
//                org.openid4java.discovery.DiscoveryInformation discovery = new DiscoveryInformation(new URL("http://localhost:8095/openidserver/op"));
//                org.openid4java.discovery.DiscoveryInformation discovery = new DiscoveryInformation(new URL(identifier));
//                discoveries = new ArrayList<DiscoveryInformation>();
//                discoveries.add(discovery);
//                printDiscoveryList(discoveries);
//
//                discoveries = manager.discover(identifier);
//                printDiscoveryList(discoveries);
//
//            } else {
//                // obtain a AuthRequest message to be sent to the OpenID provider
//                discoveries = manager.discover(identifier);
//            }

            List<DiscoveryInformation> discoveries = manager.discover(identifier);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);
            log.debug("discovered {}", discovered);

            // store the discovery information in the user's session for later use
            // leave out for stateless operation / if there is no session
            DevSession.getSession().setAttribute(DISCOVERED_ATTRIBUTE, discovered);

            if (mainApplicationURL == null) {
                mainApplicationURL = ServerSideConfiguration.instance().getMainApplicationURL();
            }
            if (!mainApplicationURL.endsWith("/")) {
                mainApplicationURL += "/";
            }
            AuthRequest authReq = manager.authenticate(discovered, mainApplicationURL + returnServletPath);

            if (requestNameAttributes || requestEmailAttributes) {
                if (isCrowdIdentification(identifier)) {
                    SRegRequest sregReq = SRegRequest.createFetchRequest();

                    if (requestEmailAttributes) {
                        sregReq.addAttribute("email", true);
                    }

                    if (requestNameAttributes) {
                        sregReq.addAttribute("firstname", true);
                        sregReq.addAttribute("lastname", true);
                    }

                    if (!sregReq.getAttributes().isEmpty()) {
                        log.info("Adding SREG attributes to the authentication request");
                        authReq.addExtension(sregReq);
                    }

                } else {
                    FetchRequest fetch = FetchRequest.createFetchRequest();

                    if (requestEmailAttributes) {
                        fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
                        fetch.addAttribute("email", true);
                    }

                    if (requestNameAttributes) {
                        fetch.addAttribute("firstname", "http://axschema.org/namePerson/first", true);
                        fetch.addAttribute("lastname", "http://axschema.org/namePerson/last", true);
                    }

                    authReq.addExtension(fetch);
                }
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
            if (DevSession.getSession() == null) {
                log.debug("session is missing");
                return null;
            }
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList responsePrams = new ParameterList(request.getParameterMap());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = (DiscoveryInformation) DevSession.getSession().getAttribute(DISCOVERED_ATTRIBUTE);
            if (discovered == null) {
                log.debug("Session terminated");
                return null;
            }
            log.info("has discovered {}", discovered);

            // extract the receiving URL from the HTTP request
            String receivingURL = request.getRequestURL().toString();

            // If contextLessDeployment, remove context from receivingURL
            if (ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isAppsContextlessDepoyment()
                    && !request.getContextPath().equalsIgnoreCase("")) {
                receivingURL = receivingURL.replace(request.getContextPath() + "/" + returnServletPath, "/" + returnServletPath);
            }

            String query = request.getQueryString();

            if (query != null && query.length() > 0) {
                receivingURL += "?" + query;
            }
            log.info("verify the response {}", receivingURL);

            String claimed_id = responsePrams.getParameterValue("openid.claimed_id");
            log.info("verify openid.claimed_id {}", claimed_id);

            // Hack for extended discovery
            String identifier = getDomainIdentifier(userDomain);

            if ((claimed_id != null) //
                    && (claimed_id.startsWith("http://" + userDomain + "/openid?id=")//
                            || claimed_id.startsWith("http://" + userDomain + "/openidserver") //
                    || claimed_id.startsWith("https://" + userDomain + "/openidserver"))) {
                discovered = new DiscoveryInformation(discovered.getOPEndpoint(), new UrlIdentifier(claimed_id));
            }

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(receivingURL, responsePrams, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                if (requestNameAttributes || requestEmailAttributes) {
                    if (isCrowdIdentification(identifier)) {
                        if (authSuccess.hasExtension("http://openid.net/sreg/1.0")) {
                            SRegResponse sregResponse = (SRegResponse) authSuccess.getExtension("http://openid.net/sreg/1.0");
                            OpenIdResponse openIdResponse = new OpenIdResponse();
                            Map<String, String> attributes = sregResponse.getAttributes();
                            if (attributes.containsKey("email")) {
                                openIdResponse.email = attributes.get("email");
                            }

                            if (attributes.containsKey("firstname") || attributes.containsKey("lastname")) {
                                openIdResponse.name = CommonsStringUtils.nvl_concat(attributes.get("firstname"), attributes.get("lastname"), " ");
                            }
                            // success
                            return openIdResponse;
                        } else {
                            return null;
                        }
                    } else {
                        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                            FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
                            OpenIdResponse openIdResponse = new OpenIdResponse();
                            openIdResponse.email = fetchResp.getAttributeValue("email");
                            openIdResponse.name = CommonsStringUtils.nvl_concat(fetchResp.getAttributeValue("firstname"),
                                    fetchResp.getAttributeValue("lastname"), " ");
                            // success
                            return openIdResponse;
                        } else {
                            return null;
                        }
                    }

                } else {
                    return new OpenIdResponse();
                }
            } else {
                log.warn("** verification failed");
                return null;
            }
        } catch (OpenIDException e) {
            log.error("Error", e);
            return null;
        }
    }

    private static boolean isCrowdIdentification(String identifier) {
        if (identifier.contains("crowd") || identifier.contains("localhost")) { // localhost to try it locally
            return true;
        }
        return false;
    }

    private static String getDomainIdentifier(String userDomain) {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdDomainIdentifier(userDomain);
    }

}
