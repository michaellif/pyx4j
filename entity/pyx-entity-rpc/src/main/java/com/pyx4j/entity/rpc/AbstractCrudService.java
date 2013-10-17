/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on Apr 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.ServiceExecution;

public interface AbstractCrudService<E extends IEntity> extends AbstractListService<E> {

    public static enum RetrieveTarget {

        View,

        Edit;

    }

    /**
     * Inherit your InitializationData from this marker
     */
    @Transient
    @AbstractEntity
    public interface InitializationData extends IEntity {

    }

    /**
     * Creates in-memory entity object, returns to client without persisting.
     */
    public void init(AsyncCallback<E> callback, InitializationData initializationData);

    public void retrieve(AsyncCallback<E> callback, Key entityId, RetrieveTarget retrieveTarget);

    public void create(AsyncCallback<Key> callback, E editableEntity);

    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, E editableEntity);
}
