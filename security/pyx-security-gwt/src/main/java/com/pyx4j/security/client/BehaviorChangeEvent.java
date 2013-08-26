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

/**
 * Fired when set of user behaviors changed, e.g. Login/Logout/TerminateSession
 */
public class BehaviorChangeEvent extends GwtEvent<BehaviorChangeHandler> {

    static Type<BehaviorChangeHandler> TYPE = new Type<BehaviorChangeHandler>();

    private final Set<Behavior> behaviors;

    public BehaviorChangeEvent(Set<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public static Type<BehaviorChangeHandler> getType() {
        return TYPE;
    }

    @Override
    public GwtEvent.Type<BehaviorChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BehaviorChangeHandler handler) {
        handler.onBehaviorChange(this);
    }

    public Set<Behavior> getBehaviors() {
        return behaviors;
    }

}
