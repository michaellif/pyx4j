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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;

public class ClientSecurityController extends SecurityController implements HasValueChangeHandlers<Set<Behavior>>, HasInitializeHandlers {

    private static Logger log = LoggerFactory.getLogger(ClientSecurityController.class);

    private HandlerManager handlerManager;

    private final AclImpl acl = new AclImpl();

    private boolean initialized;

    // Allow everything from Permission point of view
    private static class AclImpl implements Acl {

        private Set<Behavior> behaviours = Collections.emptySet();

        @Override
        public boolean checkBehavior(Behavior behavior) {
            return behaviours.contains(behavior);
        }

        @Override
        public boolean checkPermission(Permission permission) {
            return true;
        }

        @Override
        public Set<Behavior> getBehaviours() {
            return behaviours;
        }

    }

    public ClientSecurityController() {

    }

    public static ClientSecurityController instance() {
        return (ClientSecurityController) SecurityController.instance();
    }

    @Override
    public Acl authenticate(Set<Behavior> behaviours) {
        if (behaviours == null) {
            behaviours = Collections.emptySet();
        } else {
            behaviours = Collections.unmodifiableSet(behaviours);
        }
        if (!EqualsHelper.equals(acl.behaviours, behaviours)) {
            log.debug("Client behaviours changed {} -> {}", acl.behaviours, behaviours);
            acl.behaviours = behaviours;
            ValueChangeEvent.fire(this, acl.behaviours);
        }
        if (!initialized) {
            initialized = true;
            log.debug("Client security initialized");
            InitializeEvent.fire(this);
        }
        return acl;
    }

    @Override
    public Acl getAcl() {
        return acl;
    }

    protected HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<Behavior>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addInitializeHandler(InitializeHandler handler) {
        return addHandler(handler, InitializeEvent.getType());
    }

}
