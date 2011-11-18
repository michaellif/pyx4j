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
 * Created on Oct 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.client;

import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.security.shared.Behavior;

public class SecurityControllerEvent extends GwtEvent<SecurityControllerHandler> {

    static Type<SecurityControllerHandler> TYPE = new Type<SecurityControllerHandler>();

    private final Set<Behavior> behaviours;

    public SecurityControllerEvent(Set<Behavior> behaviours) {
        this.behaviours = behaviours;
    }

    public static Type<SecurityControllerHandler> getType() {
        return TYPE;
    }

    @Override
    public GwtEvent.Type<SecurityControllerHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SecurityControllerHandler handler) {
        handler.onSecurityContextChange(this);
    }

    public Set<Behavior> getBehaviours() {
        return behaviours;
    }

}
