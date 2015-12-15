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
 * Created on Dec 7, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.cursor;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;

public class SimpleCursorSource<E extends IEntity> implements CursorSource<E> {

    public SimpleCursorSource() {
    }

    @Override
    public ICursorIterator<E> getCursor(String encodedCursorReference, EntityQueryCriteria<E> criteria, AttachLevel attachLevel) {
        return Persistence.service().query(encodedCursorReference, criteria, attachLevel);
    }

}
