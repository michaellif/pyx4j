/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.DatastoreReadOnlyRuntimeException;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.shared.VistaSystemIdentification;

public abstract class VistaAuthenticationServicesImpl<U extends AbstractUser, E extends AbstractUserCredential<U>> extends
        com.pyx4j.security.server.AuthenticationServiceImpl {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    private static final I18n i18n = I18n.get(VistaAuthenticationServicesImpl.class);

    // Used to confuse hacker and void giving exact reason why we failed.
    protected final String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    protected final Class<U> userClass;

    protected final Class<E> credentialClass;

    protected VistaAuthenticationServicesImpl(Class<U> userClass, Class<E> credentialClass) {
        this.userClass = userClass;
        this.credentialClass = credentialClass;
    }

    protected abstract VistaBasicBehavior getApplicationBehavior();

    protected abstract Behavior getPasswordChangeRequiredBehavior();

    protected abstract void sendPasswordRetrievalToken(U user);

    protected Set<Behavior> getBehaviors(E userCredential) {
        return Collections.emptySet();
    }

    protected boolean honorSystemState() {
        return true;
    }

    protected boolean isDynamicBehaviours() {
        return false;
    }

    protected boolean isSessionValid() {
        return SecurityController.checkAnyBehavior(getApplicationBehavior(), getPasswordChangeRequiredBehavior());
    }

    @Override
    public void obtainRecaptchaPublicKey(AsyncCallback<String> callback) {
        callback.onSuccess(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken) {
        if (!isSessionValid()) {
            Lifecycle.endSession();
        }
        super.authenticate(callback, clientSystemInfo, sessionToken);
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request) {
        assertClientSystemInfo(clientSystemInfo);
        // Try to begin Session
        String sessionToken = beginSession(request);
        if (!isSessionValid()) {
            Lifecycle.endSession();
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    @Override
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken) {
        if (honorSystemState()) {
            switch (SystemMaintenance.getState()) {
            case Unavailable:
            case ReadOnly:
                throw new UserRuntimeException(SystemMaintenance.getApplicationMaintenanceMessage());
            }
        }
        AccessKey.TokenParser token = new AccessKey.TokenParser(accessToken);
        if (!validEmailAddress(token.email)) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = PasswordEncryptor.normalizeEmailAddress(token.email);
        AbstractAntiBot.assertLogin(LoginType.accessToken, email, null);

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.accessToken, email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        U user = users.get(0);

        boolean credentialsOk = false;
        try {

            E cr = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
            if (cr == null) {
                throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
            }
            if (!cr.enabled().isBooleanTrue()) {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }

            if (!token.accessKey.equals(cr.accessKey().getValue())) {
                AbstractAntiBot.authenticationFailed(LoginType.accessToken, token.email);
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }

            if ((new Date().after(cr.accessKeyExpire().getValue()))) {
                throw new UserRuntimeException(i18n.tr("Token Has Expired"));
            }
            credentialsOk = true;
        } finally {
            if (!credentialsOk) {
                ServerSideFactory.create(AuditFacade.class).loginFailed(user);
            }
        }

        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(getPasswordChangeRequiredBehavior());
        UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        visit.setEmail(user.email().getValue());
        log.info("authenticated {} as {}", user.email().getValue(), behaviors);

        String sessionToken = Lifecycle.beginSession(visit, behaviors);
        VistaContext.setCurrentUser(user);
        ServerSideFactory.create(AuditFacade.class).login();
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    protected String beginSession(AuthenticationRequest request) {
        if (honorSystemState()) {
            switch (SystemMaintenance.getState()) {
            case Unavailable:
                throw new UserRuntimeException(SystemMaintenance.getApplicationMaintenanceMessage());
            default:
                break;
            }
        }

        if (CommonsStringUtils.isEmpty(request.email().getValue()) || !validEmailAddress(request.email().getValue())
                || CommonsStringUtils.isEmpty(request.password().getValue())) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = PasswordEncryptor.normalizeEmailAddress(request.email().getValue());
        AbstractAntiBot.assertLogin(LoginType.userLogin, email, request.captcha().getValue());

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        U user = users.get(0);

        boolean credentialsOk = false;
        E cr;
        try {
            cr = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
            if (cr == null) {
                throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
            }
            if (!cr.enabled().isBooleanTrue()) {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
                log.info("Invalid password for user {}", email);
                if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                    throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
                } else {
                    throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            credentialsOk = true;
        } finally {
            if (!credentialsOk) {
                ServerSideFactory.create(AuditFacade.class).loginFailed(user);
            }
        }

        if (!cr.accessKey().isNull()) {
            cr.accessKey().setValue(null);
            Persistence.service().persist(cr);
            Persistence.service().commit();
        }
        if (cr.requiredPasswordChangeOnNextLogIn().isBooleanTrue()) {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.add(getPasswordChangeRequiredBehavior());
            UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
            visit.setEmail(user.email().getValue());
            String token = Lifecycle.beginSession(visit, behaviors);
            VistaContext.setCurrentUser(user);
            ServerSideFactory.create(AuditFacade.class).login();
            return token;
        } else {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.addAll(getBehaviors(cr));
            behaviors.add(getApplicationBehavior());
            return beginSession(user, cr, behaviors, null);
        }
    }

    public final Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours) {
        E userCredential = Persistence.service().retrieve(credentialClass, principalPrimaryKey);
        if ((userCredential == null) || (!userCredential.enabled().isBooleanTrue())) {
            return null;
        } else if (isDynamicBehaviours()) {
            return currentBehaviours;
        } else {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.addAll(getBehaviors(userCredential));
            behaviors.add(getApplicationBehavior());
            return behaviors;
        }
    }

    public final AuthenticationResponse authenticate(E credentials, IEntity additionalConditions) {
        U user = Persistence.service().retrieve(userClass, credentials.getPrimaryKey());
        // Try to begin Session
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(getBehaviors(credentials));
        behaviors.add(getApplicationBehavior());
        String sessionToken = beginSession(user, credentials, behaviors, additionalConditions);
        if (!isSessionValid()) {
            Lifecycle.endSession();
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        return createAuthenticationResponse(sessionToken);
    }

    public final AuthenticationResponse reAuthenticate(IEntity additionalConditions) {
        E credentials = Persistence.service().retrieve(credentialClass, VistaContext.getCurrentUserPrimaryKey());
        return authenticate(credentials, additionalConditions);
    }

    protected String beginSession(U user, E credentials, Set<Behavior> behaviors, IEntity additionalConditions) {
        // Only default ApplicationBehavior assigned is error. User have no roles
        if (behaviors.isEmpty() || ((behaviors.size() == 1) && (behaviors.contains(getApplicationBehavior())))) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        UserVisit visit = new UserVisit(user.getPrimaryKey(), user.name().getValue());
        visit.setEmail(user.email().getValue());
        log.info("authenticated {} as {}", user.email().getValue(), behaviors);
        String token = Lifecycle.beginSession(visit, behaviors);
        VistaContext.setCurrentUser(user);
        try {
            ServerSideFactory.create(AuditFacade.class).login();
        } catch (DatastoreReadOnlyRuntimeException readOnly) {
            if (honorSystemState()) {
                throw readOnly;
            }
        }
        return token;
    }

    @Override
    @IgnoreSessionToken
    public final void logout(AsyncCallback<AuthenticationResponse> callback) {
        Lifecycle.endSession();
        callback.onSuccess(createAuthenticationResponse(null));
    }

    @Override
    public final void requestPasswordReset(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request) {

        if (!validEmailAddress(request.email().getValue())) {
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }
        AbstractAntiBot.assertCaptcha(request.captcha().getValue());

        String email = PasswordEncryptor.normalizeEmailAddress(request.email().getValue());

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid PasswordReset {} rs {}", email, users.size());
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }
        U user = users.get(0);

        sendPasswordRetrievalToken(user);
        log.debug("pwd change token is sent to {}", user.email().getValue());
        callback.onSuccess(new VoidSerializable());
    }

    @Override
    public final AuthenticationResponse createAuthenticationResponse(String sessionToken) {
        AuthenticationResponse ar = super.createAuthenticationResponse(sessionToken);

        String baseUrl = VistaDeployment.getBaseApplicationURL(getApplicationBehavior(), true);
        String requestUrl = Context.getRequest().getRequestURL().toString();

        SystemWallMessage systemWallMessage = null;
        VistaSystemIdentification systemId = VistaDeployment.getSystemIdentification();
        switch (systemId) {
        case production:
            if (!requestUrl.startsWith(baseUrl)) {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
            break;
        case staging:
            if (requestUrl.startsWith(baseUrl)) {
                systemWallMessage = new SystemWallMessage(i18n.tr("This is internal staging environment!"), true);
            } else {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
            break;
        case demo:
            systemWallMessage = new SystemWallMessage("Demo Environment", true);
            break;
        default:
            if (ApplicationMode.isDevelopment()) {
                systemWallMessage = new SystemWallMessage("This is development and tests System", true);
            } else {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
        }
        switch (SystemMaintenance.getState()) {
        case ReadOnly:
        case Unavailable:
            if (systemWallMessage == null) {
                systemWallMessage = new SystemWallMessage();
            }
            systemWallMessage.setMessage(CommonsStringUtils.nvl_concat(systemWallMessage.getMessage(), SystemMaintenance.getApplicationMaintenanceMessage(),
                    ".\n"));
        }

        ar.setSystemWallMessage(systemWallMessage);

        if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport)
                && ((systemId == VistaSystemIdentification.production) || (systemId == VistaSystemIdentification.demo))) {
            setVistaEmployeeCookie();
        }

        if (systemId != VistaSystemIdentification.staging) {
            ar.setGoogleAnalyticsKey(((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getGoogleAnalyticsKey());
        }

        return ar;
    }

    public static void setVistaEmployeeCookie() {
        Cookie sessionCookie = new Cookie(DeploymentConsts.vistaEmployeeCookie, "true");
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(Long.valueOf(180 * Consts.DAY2MSEC).intValue());

        String host = Context.getRequestServerName();
        List<String> hostParts = new ArrayList<String>(Arrays.asList(host.split("\\.")));
        Collections.reverse(hostParts);
        if (hostParts.size() >= 2) {
            String domain = "." + hostParts.get(1) + "." + hostParts.get(0);
            sessionCookie.setDomain(domain);
        }
        Context.getResponse().addCookie(sessionCookie);
    }
}
