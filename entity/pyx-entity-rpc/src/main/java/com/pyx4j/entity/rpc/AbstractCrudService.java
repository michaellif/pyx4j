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
 */
package com.pyx4j.entity.rpc;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.rpc.shared.ServiceExecution;

public interface AbstractCrudService<E extends IEntity> extends AbstractListCrudService<E> {

    public static enum RetrieveOperation implements Serializable {

        List,

        View,

        Edit,

        Save;

        public boolean isRead() {
            return this == List || this == View;
        }

        public boolean isEdit() {
            return this == Edit || this == Save;
        }

        public BindingType getBindingType() {
            switch (this) {
            case View:
                return BindingType.View;
            case Edit:
                return BindingType.Edit;
            case List:
                return BindingType.List;
            case Save:
                return BindingType.Save;
            default:
                throw new IllegalArgumentException(this.name());
            }
        }
    }

    /**
     * Inherit your InitializationData from this marker
     */
    @Transient
    @AbstractEntity
    public interface InitializationData extends IEntity {

    }

    @Transient
    public interface DuplicateData extends InitializationData {

        IPrimitive<Key> originalEntityKey();
    }

    /**
     * Creates in-memory entity object, returns to client without persisting.
     */
    public void init(AsyncCallback<E> callback, InitializationData initializationData);

    public void retrieve(AsyncCallback<E> callback, Key entityId, RetrieveOperation retrieveOperation);

    public void create(AsyncCallback<Key> callback, E editableEntity);

    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, E editableEntity);
}
