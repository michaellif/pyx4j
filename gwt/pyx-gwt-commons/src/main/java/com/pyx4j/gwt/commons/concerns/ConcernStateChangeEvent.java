/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 3, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.concerns;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when the event source changes is state (visibility) due to security context changes.
 *
 * @see AbstractConcern
 */
public class ConcernStateChangeEvent extends GwtEvent<ConcernStateChangeEvent.Handler> {

    private static Type<ConcernStateChangeEvent.Handler> TYPE = new Type<ConcernStateChangeEvent.Handler>();

    public interface Handler extends EventHandler {

        void onSecureConcernStateChanged(ConcernStateChangeEvent event);

    }

    public static Type<ConcernStateChangeEvent.Handler> getType() {
        return TYPE;
    }

    public ConcernStateChangeEvent() {
    }

    @Override
    public GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onSecureConcernStateChanged(this);
    }
}
