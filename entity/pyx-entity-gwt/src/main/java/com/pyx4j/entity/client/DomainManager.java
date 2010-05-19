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
 * Created on Apr 8, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.Behavior;

public class DomainManager {

    public enum DomainEventType {
        UPDATED, DELETED, OPENED, CLOSED, INVALIDATE
    };

    public interface DomainListener {

        void onDomainChange(IEntity entity, DomainEventType type);

    }

    private static List<DomainListener> listeners = new Vector<DomainListener>();

    static {
        ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                invalidate();
            }
        });

    }

    public static void invalidate() {
        ReferenceDataManager.invalidate();
        fireDomainChange(null, DomainEventType.INVALIDATE);
        listeners.clear();
    }

    //TODO implement different lists by types
    public static void addDomainListener(DomainListener listener, DomainEventType... type) {
        listeners.add(listener);
    }

    public static void removeDomainListener(DomainListener listener) {
        listeners.remove(listener);
    }

    private static void fireDomainChange(IEntity entity, DomainEventType type) {
        for (DomainListener listener : listeners) {
            listener.onDomainChange(entity, type);
        }
    }

    public static void entityOpened(IEntity entity) {
        fireDomainChange(entity, DomainEventType.OPENED);
    }

    public static void entityClosed(IEntity entity) {
        fireDomainChange(entity, DomainEventType.CLOSED);
    }

    public static void entityUpdated(IEntity entity) {
        ReferenceDataManager.update(entity);
        fireDomainChange(entity, DomainEventType.UPDATED);
    }

    public static void entityDeleted(IEntity entity) {
        fireDomainChange(entity, DomainEventType.DELETED);
    }

}
