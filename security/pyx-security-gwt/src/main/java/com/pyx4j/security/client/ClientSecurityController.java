/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 13, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.client;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.security.shared.AccessRule;
import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class ClientSecurityController extends SecurityController {

    private static Logger log = LoggerFactory.getLogger(ClientSecurityController.class);

    private AclImpl acl = new AclImpl();

    private boolean initialized;

    // Allow everything from Permission point of view
    private static class AclImpl implements Acl {

        private Set<Behavior> behaviors = Collections.emptySet();

        @Override
        public boolean checkBehavior(Behavior behavior) {
            return behaviors.contains(behavior);
        }

        @Override
        public boolean checkPermission(Permission permission) {
            return true;
        }

        @Override
        public Set<Behavior> getBehaviours() {
            return behaviors;
        }

        @Override
        public int hashCode() {
            return behaviors.hashCode();
        }

        @Override
        public <T extends AccessRule> List<T> getAccessRules(Class<T> accessRuleInterfaceClass, Object subject) {
            return null;
        }

        boolean isUnsecure() {
            return false;
        }

    }

    private static class UnsecureAclImpl extends AclImpl {

        @Override
        public boolean checkBehavior(Behavior behavior) {
            return true;
        }

        @Override
        boolean isUnsecure() {
            return true;
        }

    }

    public ClientSecurityController() {
        SessionMonitor.initialize();
    }

    public static ClientSecurityController instance() {
        return (ClientSecurityController) SecurityController.instance();
    }

    public static void setUnsecure() {
        if (ApplicationMode.isDevelopment()) {
            instance().acl = new UnsecureAclImpl();
        }
    }

    public static boolean isUnsecure() {
        if (ApplicationMode.isDevelopment()) {
            return instance().acl.isUnsecure();
        } else {
            return false;
        }
    }

    @Override
    public Acl authenticate(Set<Behavior> behaviors) {
        if (behaviors == null) {
            behaviors = Collections.emptySet();
        } else {
            behaviors = Collections.unmodifiableSet(behaviors);
        }
        if (!EqualsHelper.equals(acl.behaviors, behaviors)) {
            log.debug("Client behaviors changed {} -> {}", acl.behaviors, behaviors);
            acl.behaviors = behaviors;
            ClientEventBus.fireEvent(new BehaviorChangeEvent(acl.behaviors));
        }

        if (!initialized) {
            initialized = true;
            log.debug("Client security initialized");
            ClientEventBus.fireEvent(new ContextInitializeEvent());
        }
        return acl;
    }

    @Override
    public Set<Behavior> getAllBehaviors(Set<Behavior> behaviors) {
        return null;
    }

    @Override
    public Acl getAcl() {
        return acl;
    }

    public static HandlerRegistration addSecurityControllerHandler(BehaviorChangeHandler handler) {
        return ClientEventBus.addHandler(BehaviorChangeEvent.getType(), handler);
    }

    public static HandlerRegistration addContextInitializeHandler(ContextInitializeHandler handler) {
        return ClientEventBus.addHandler(ContextInitializeEvent.TYPE, handler);
    }

}
