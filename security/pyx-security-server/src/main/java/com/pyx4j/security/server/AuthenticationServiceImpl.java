/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-06-13
 * @author vlads
 */
package com.pyx4j.security.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.config.shared.ClientVersionMismatchError;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.security.rpc.PasswordRetrievalRequest;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.server.contexts.Visit;

/**
 * This implementation does not use DB to load and verify users.
 *
 * You need to override authenticate in App.
 */
public abstract class AuthenticationServiceImpl implements AuthenticationService {

    private static Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private static final I18n i18n = I18n.get(AuthenticationServiceImpl.class);

    public enum LoginFailedFlow {

        UserNotFound,

        UserAccountDisabled,

        WrongPassword

    }

    protected void assertClientSystemInfo(ClientSystemInfo clientSystemInfo) {
        String serverVersion = ApplicationVersion.getBuildLabel();
        if (clientSystemInfo == null) {
            throw new ClientVersionMismatchError(i18n.tr("Client version {0} does not match server version {1}", "", serverVersion));
        }
        if (((clientSystemInfo.isScript()) && (!serverVersion.contains("n/a")) && (!serverVersion.endsWith("-SNAPSHOT"))
                && (!serverVersion.equals(clientSystemInfo.getBuildLabel())))) {
            throw new ClientVersionMismatchError(
                    i18n.tr("Client version {0} does not match server version {1}", clientSystemInfo.getBuildLabel(), serverVersion));
        }
    }

    @Override
    public void getSystemReadOnlyStatus(AsyncCallback<Boolean> callback) {
        callback.onSuccess(ServerSideConfiguration.instance().datastoreReadOnly());
    }

    public AuthenticationResponse createAuthenticationResponse(String sessionToken) {
        AuthenticationResponse ar = new AuthenticationResponse();
        if (ServerContext.getSession() != null) {
            ar.setMaxInactiveInterval(ServerContext.getSession().getMaxInactiveInterval());
            ar.setSessionCookieName(ServerSideConfiguration.instance().getSessionCookieName());
            log.debug("session maxInactiveInterval {} sec", ar.getMaxInactiveInterval());
            switch (ServerSideConfiguration.instance().getEnvironmentType()) {
            case GAEDevelopment:
            case GAESandbox:
                if (ar.getMaxInactiveInterval() <= 0) {
                    ar.setMaxInactiveInterval(24 * Consts.HOURS2SEC);
                    log.debug("session maxInactiveInterval {} sec", ar.getMaxInactiveInterval());
                }
                break;
            default:
                break;
            }

            ar.setBehaviors(SecurityController.getBehaviors());
            ar.setPermissions(filterClientPermissions(SecurityController.getPermissions()));

            Visit visit = ServerContext.getVisit();
            if (visit != null) {
                if (EqualsHelper.equals(sessionToken, visit.getSessionToken())) {
                    ar.setUserVisit(visit.getUserVisit());
                    ar.setSessionToken(visit.getSessionToken());
                    ar.setAclTimeStamp(String.valueOf(visit.getAclTimeStamp()));
                } else {
                    log.warn("Invalid request sessionToken {}", sessionToken);
                    ar.setBehaviors(null);
                    ar.setPermissions(null);
                }
            }

        }

        if (ServerSideConfiguration.instance().useAppengineGoogleAccounts()) {
            AppengineUserService.updateAuthenticationResponse(ar);
        }

        if (ServerSideConfiguration.instance().datastoreReadOnly()) {
            ar.setDatastoreReadOnly(true);
        }
        ar.setProductionBackend(ServerSideConfiguration.instance().isProductionBackend());
        ar.setDevelopmentBehavior(ApplicationMode.isDevelopment());
        ar.setQaBehavior(ApplicationMode.isQa());
        ar.setDemoBehavior(ApplicationMode.isDemo());

        ar.setServertTime(System.currentTimeMillis());
        return ar;
    }

    protected boolean isClientPermissions(Permission permission) {
        if ((permission instanceof DataModelPermission) || (permission instanceof ActionPermission)) {
            return true;
        } else {
            return false;
        }
    }

    protected Collection<Permission> filterClientPermissions(Collection<Permission> permissions) {
        Collection<Permission> cleintPermissions = new ArrayList<Permission>();
        for (Permission permission : permissions) {
            if (isClientPermissions(permission)) {
                cleintPermissions.add(permission);
            }
        }
        return cleintPermissions;
    }

    @Override
    @IgnoreSessionToken
    public void verifyVersion(AsyncCallback<VoidSerializable> callback, ClientSystemInfo clientSystemInfo) {
        assertClientSystemInfo(clientSystemInfo);
        callback.onSuccess(null);
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken) {
        assertClientSystemInfo(clientSystemInfo);
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    @Override
    public void keepSessionAlive(AsyncCallback<VoidSerializable> callback) {
        // Session is keepedAlive by container if requests are made, So we need nothing.
        callback.onSuccess(null);
    }

    @Override
    @IgnoreSessionToken
    public void logout(AsyncCallback<AuthenticationResponse> callback) {
        // Allow all RPC requests to complete before session is closed, TODO Consider moving the same to behavior change in session.
        // Visit is destroyed after endSession, keep the variable
        ReadWriteLock sessionGuardLock = null;
        if (ServerContext.getVisit() != null) {
            sessionGuardLock = ServerContext.getVisit().getSessionGuardLock();
            try {
                //This thread hold a read lock already,
                sessionGuardLock.readLock().unlock();

                // 1 minute is default DB online transaction pool timeout
                if (!sessionGuardLock.writeLock().tryLock(1, TimeUnit.MINUTES)) {
                    sessionGuardLock = null;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Lifecycle.endSession();
        } finally {
            if (sessionGuardLock != null) {
                sessionGuardLock.writeLock().unlock();
                sessionGuardLock.readLock().lock();
            }
        }
        callback.onSuccess(createAuthenticationResponse(null));
    }

    @Override
    public void getLoginUrl(AsyncCallback<String> callback, String destinationURLComponent) {
        callback.onSuccess(ContainerHelper.getContainerHelper().createLoginURL(destinationURLComponent));
    }

    @Override
    public void getLogoutUrl(AsyncCallback<String> callback, String destinationURLComponent) {
        callback.onSuccess(ContainerHelper.getContainerHelper().createLogoutURL(destinationURLComponent));
    }

    @Override
    public void requestPasswordReset(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request) {
        throw new Error("Not implemented");
    }

    @Override
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken) {
        throw new Error("Not implemented");
    }
}
