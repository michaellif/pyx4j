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
 * Created on Feb 2, 2015
 * @author vlads
 */
package com.pyx4j.entity.server;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;

public abstract class CursorIteratorDelegate<T extends IEntity, B extends IEntity> implements ICursorIterator<T> {

    protected final ICursorIterator<B> unfiltered;

    protected CursorIteratorDelegate(final ICursorIterator<B> unfiltered) {
        this.unfiltered = unfiltered;
    }

    @Override
    public boolean hasNext() {
        return unfiltered.hasNext();
    }

    @Override
    public void remove() {
        unfiltered.remove();
    }

    @Override
    public String encodedCursorReference() {
        return unfiltered.encodedCursorReference();
    }

    @Override
    public void close() {
        unfiltered.close();
    }
}
