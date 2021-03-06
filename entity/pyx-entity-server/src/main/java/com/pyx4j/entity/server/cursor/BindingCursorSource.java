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
 * Created on Nov 9, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.cursor;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.CursorIteratorDelegate;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.BindingContext;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.entity.shared.utils.EntityBinder;

//TODO need to review validity of this implementation. For now it is not used.
// PRoblem: Criteria in constructor override criteria used in getCursor
@Deprecated
public class BindingCursorSource<BO extends IEntity, TO extends IEntity> implements CursorSource<TO> {

    protected final EntityQueryCriteria<BO> criteria;

    protected final EntityBinder<BO, TO> binder;

    public static <BO extends IEntity, TO extends IEntity> BindingCursorSource<BO, TO> create(EntityQueryCriteria<BO> criteria, EntityBinder<BO, TO> binder) {
        return new BindingCursorSource<BO, TO>(criteria, binder);
    }

    public BindingCursorSource(EntityQueryCriteria<BO> criteria, EntityBinder<BO, TO> binder) {
        this.binder = binder;
        this.criteria = criteria;
    }

    @Override
    public ICursorIterator<TO> getCursor(String encodedCursorReference, EntityQueryCriteria<TO> criteria, AttachLevel attachLevel) {
        ICursorIterator<BO> boCreateIterator = Persistence.service().query(encodedCursorReference, this.criteria, attachLevel);

        ICursorIterator<TO> toCreateIterator = new CursorIteratorDelegate<TO, BO>(boCreateIterator) {

            @Override
            public TO next() {
                BO bo = unfiltered.next();
                TO to = binder.createTO(bo, new BindingContext(BindingType.List));
                return to;
            }

        };

        return toCreateIterator;
    }

}
