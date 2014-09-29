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
import java.util.Collection;
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
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.DatastoreReadOnlyRuntimeException;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthorizationUpdatedSystemNotification;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.security.server.AclRevalidator;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Context;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.shared.VistaSystemIdentification;
import com.propertyvista.shared.VistaUserVisit;
import com.propertyvista.shared.exceptions.LoginTokenExpiredUserRuntimeException;

public abstract class VistaAuthenticationServicesImpl<U extends AbstractUser, V extends VistaUserVisit<U>, E extends AbstractUserCredential<U>> extends
        com.pyx4j.security.server.AuthenticationServiceImpl implements AclRevalidator {

    private final static Logger log = LoggerFactory.getLogger(VistaAuthenticationServicesImpl.class);

    private static final I18n i18n = I18n.get(VistaAuthenticationServicesImpl.class);

    // Used to confuse hacker and void giving exact reason why we failed.
    protected final String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    protected final Class<U> userClass;

    protected final Class<E> credentialClass;

    protected final Class<V> userVisitClass;

    protected VistaAuthenticationServicesImpl(Class<U> userClass, Class<V> userVisitClass, Class<E> credentialClass) {
        this.userClass = userClass;
        this.userVisitClass = userVisitClass;
        this.credentialClass = credentialClass;
    }

    protected abstract VistaApplication getVistaApplication();

    protected abstract VistaAccessGrantedBehavior getApplicationAccessGrantedBehavior();

    protected abstract V createUserVisit(U user);

    protected abstract Behavior getPasswordChangeRequiredBehavior();

    protected Collection<Behavior> getAccountSetupRequiredBehaviors() {
        return Arrays.asList(new Behavior[] { getPasswordChangeRequiredBehavior() });
    }

    protected abstract void sendPasswordRetrievalToken(U user);

    protected Set<Behavior> getBehaviors(E userCredential, V visit) {
        return Collections.emptySet();
    }

    protected boolean honorSystemState() {
        return true;
    }

    protected boolean isSessionValid() {
        boolean sessionValid = SecurityController.check(getVistaApplication())
                && (SecurityController.check(getApplicationAccessGrantedBehavior()) || SecurityController.check(getAccountSetupRequiredBehaviors()));
        if ((!sessionValid) && (ServerContext.getSession() != null)) {
            log.warn("sessionInvalid: {} {}", getVistaApplication(), SecurityController.check(getVistaApplication()));
            log.warn("sessionInvalid: {} {}", getApplicationAccessGrantedBehavior(), SecurityController.check(getApplicationAccessGrantedBehavior()));
            log.warn("sessionInvalid: {} {}", getAccountSetupRequiredBehaviors(), SecurityController.check(getAccountSetupRequiredBehaviors()));
        }
        return sessionValid;
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
        log.info("authenticated {}; UserAgent {}", ServerContext.getVisit().getUserVisit().getEmail(), clientSystemInfo.getUserAgent());
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    @Override
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken) {
        if (honorSystemState()) {
            switch (SystemMaintenance.getState()) {
            case Unavailable:
            case ReadOnly:
                throw new UserRuntimeException(true, SystemMaintenance.getApplicationMaintenanceMessage());
            default:
                break;
            }
        }
        AccessKey.TokenParser token = new AccessKey.TokenParser(accessToken);
        if (!EmailValidator.isValid(token.email)) {
            throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = EmailValidator.normalizeEmailAddress(token.email);
        AbstractAntiBot.assertLogin(LoginType.accessToken, email, null);

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.accessToken, email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        U user = users.get(0);

        boolean credentialsOk = false;
        E cr;
        try {
            cr = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
            if (cr == null) {
                throw new UserRuntimeException(true, i18n.tr("Invalid User Account. Please Contact Support"));
            }
            if (!cr.enabled().getValue(false)) {
                log.warn("Invalid log-in attempt {} : disabled user", email);
                throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }

            if (!token.accessKey.equals(cr.accessKey().getValue())) {
                AbstractAntiBot.authenticationFailed(LoginType.accessToken, token.email);
                throw new LoginTokenExpiredUserRuntimeException(i18n.tr("The URL you have used is either incorrect or expired."));
            }

            if ((new Date().after(cr.accessKeyExpire().getValue()))) {
                throw new LoginTokenExpiredUserRuntimeException(i18n.tr("The URL you have used is either incorrect or expired."));
            }
            credentialsOk = true;
        } finally {
            if (!credentialsOk) {
                ServerSideFactory.create(AuditFacade.class).loginFailed(getVistaApplication(), user);
            }
        }

        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(getVistaApplication());
        if (token.behaviorPasswordChangeRequired) {
            behaviors.add(getPasswordChangeRequiredBehavior());
        } else {
            behaviors.addAll(getBehaviors(cr, null));
            behaviors.add(getApplicationAccessGrantedBehavior());
            // This is one time login, reset token
            cr.accessKey().setValue(null);
            Persistence.service().persist(cr);
            Persistence.service().commit();
        }
        V visit = createUserVisit(user);
        log.info("authenticated {} as {}", user.email().getValue(), behaviors);

        String sessionToken = Lifecycle.beginSession(visit, behaviors);
        ServerSideFactory.create(AuditFacade.class).login(getVistaApplication());
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    protected String beginSession(AuthenticationRequest request) {
        if (honorSystemState()) {
            switch (SystemMaintenance.getState()) {
            case Unavailable:
                throw new UserRuntimeException(true, SystemMaintenance.getApplicationMaintenanceMessage());
            default:
                break;
            }
        }

        if (CommonsStringUtils.isEmpty(request.email().getValue()) || !EmailValidator.isValid(request.email().getValue())
                || CommonsStringUtils.isEmpty(request.password().getValue())) {
            throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        String email = EmailValidator.normalizeEmailAddress(request.email().getValue());
        if (VistaDeployment.isVistaStaging()) {
            if (!email.startsWith("s!")) {
                log.warn("Staging env protection (s!${email}) triggered for user {}", email);
                throw new UserRuntimeException(true, i18n.tr("Application is Unavailable due to short maintenance.\nPlease try again in one hour"));
            } else {
                email = email.substring(2, email.length());
            }
        }
        AbstractAntiBot.assertLogin(LoginType.userLogin, email, request.captcha().getValue());

        EntityQueryCriteria<U> criteria = new EntityQueryCriteria<U>(userClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<U> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
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
            if (!cr.enabled().getValue(false)) {
                log.warn("Invalid log-in attempt {} : disabled user", email);
                throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
            if (!checkPassword(user, cr, email, request.password().getValue(), cr.credential().getValue())) {
                log.info("Invalid password for user {}", email);
                if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                    throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
                } else {
                    throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
            credentialsOk = true;
        } finally {
            if (!credentialsOk) {
                ServerSideFactory.create(AuditFacade.class).loginFailed(getVistaApplication(), user);
            }
        }

        if (!cr.accessKey().isNull()) {
            cr.accessKey().setValue(null);
            Persistence.service().persist(cr);
            Persistence.service().commit();
        }
        V visit = createUserVisit(user);
        if (cr.requiredPasswordChangeOnNextLogIn().getValue(false)) {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.add(getVistaApplication());
            behaviors.add(getPasswordChangeRequiredBehavior());
            String token = Lifecycle.beginSession(visit, behaviors);
            ServerSideFactory.create(AuditFacade.class).login(getVistaApplication());
            return token;
        } else {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.addAll(getBehaviors(cr, visit));
            behaviors.add(getVistaApplication());
            return beginApplicationSession(visit, cr, behaviors, null);
        }
    }

    protected boolean checkPassword(U user, E credentials, String email, String inputPassword, String encryptedPassword) {
        return ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(inputPassword, encryptedPassword);
    }

    public static <T extends Behavior> boolean containsAnyBehavior(Collection<T> list, Collection<T> subSet) {
        for (Behavior behavior : list) {
            if (subSet.contains(behavior)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        E userCredential = Persistence.service().retrieve(credentialClass, principalPrimaryKey);
        if ((userCredential == null) || (!userCredential.enabled().getValue(false))) {
            return null;
        }
        if (userCredential.requiredPasswordChangeOnNextLogIn().getValue(false)) {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.add(getVistaApplication());
            behaviors.add(getPasswordChangeRequiredBehavior());
            return behaviors;
        } else {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.add(getVistaApplication());
            behaviors.addAll(getBehaviors(userCredential, Context.visit(userVisitClass)));
            return behaviors;
        }
    }

    @Override
    public final void reAuthorizeCurrentVisit(Set<Behavior> behaviours) {
        // TODO its impl does not belong here.
        Visit visit = ServerContext.getVisit();
        String token = Lifecycle.beginSession(visit.getUserVisit(), behaviours);
        visit.setAclChanged(true);
        ServerContext.addResponseSystemNotification(new AuthorizationUpdatedSystemNotification(createAuthenticationResponse(token)));
    }

    //TODO  Change the implementation to use Authorization functions
    protected String beginApplicationSession(V visit, E credentials, Set<Behavior> behaviors, IEntity additionalConditions) {
        // Only default ApplicationBehavior assigned is error. User have no roles
        if (behaviors.isEmpty() || ((behaviors.size() == 1) && (behaviors.contains(getVistaApplication())))) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        log.info("authenticated {} as {}", visit.getEmail(), behaviors);
        String token = Lifecycle.beginSession(visit, behaviors);
        try {
            ServerSideFactory.create(AuditFacade.class).login(getVistaApplication());
        } catch (DatastoreReadOnlyRuntimeException readOnly) {
            //TODO remove this when we have second Audit connection
            if (honorSystemState()) {
                throw readOnly;
            }
        }
        return token;
    }

    @Override
    @IgnoreSessionToken
    public final void logout(AsyncCallback<AuthenticationResponse> callback) {
        try {
            ServerSideFactory.create(AuditFacade.class).logout(getVistaApplication());
        } catch (DatastoreReadOnlyRuntimeException readOnly) {
            //TODO remove this when we have second Audit connection
            // ignore logout
        }
        super.logout(callback);
    }

    @Override
    public final void requestPasswordReset(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request) {

        if (!EmailValidator.isValid(request.email().getValue())) {
            throw new UserRuntimeException(i18n.tr(GENERIC_FAILED_MESSAGE));
        }
        AbstractAntiBot.assertCaptcha(request.captcha().getValue());

        String email = EmailValidator.normalizeEmailAddress(request.email().getValue());

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

        String baseUrl = VistaDeployment.getBaseApplicationURL(getVistaApplication(), true);
        String requestUrl = ServerContext.getRequest().getRequestURL().toString();

        SystemWallMessage systemWallMessage = null;
        VistaSystemIdentification systemId = VistaDeployment.getSystemIdentification();
        switch (systemId) {
        case production:
            if (!requestUrl.startsWith(baseUrl)) {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
            ar.setEnviromentName("Production");
            break;
        case staging:
            if (requestUrl.startsWith(baseUrl)) {
                systemWallMessage = new SystemWallMessage(i18n.tr("This is internal staging environment!"), true);
            } else {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
            ar.setEnviromentName("Staging");
            break;
        case demo:
            systemWallMessage = new SystemWallMessage("Demo Environment", true);
            ar.setEnviromentName("Demo");
            break;
        default:
            if (ApplicationMode.isDevelopment()) {
                systemWallMessage = new SystemWallMessage("This is development and tests System", true);
            } else {
                systemWallMessage = new SystemWallMessage(i18n.tr("Repairs in Progress"), true);
            }
            Integer enviromentId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).enviromentId();
            if (enviromentId != null) {
                ar.setEnviromentName("Env" + enviromentId);
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

        if (SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)
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

        String host = ServerContext.getRequestServerName();
        List<String> hostParts = new ArrayList<String>(Arrays.asList(host.split("\\.")));
        Collections.reverse(hostParts);
        if (hostParts.size() >= 2) {
            String domain = "." + hostParts.get(1) + "." + hostParts.get(0);
            sessionCookie.setDomain(domain);
        }
        ServerContext.getResponse().addCookie(sessionCookie);
    }
}
