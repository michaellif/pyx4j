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
 * Created on 2010-10-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.adapters;

import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Province;

public class ProvinceReferenceAdapter implements ReferenceAdapter<Province> {

    @Override
    public EntityQueryCriteria<Province> getMergeCriteria(Province newEntity) {
        EntityQueryCriteria<Province> c = EntityQueryCriteria.create(Province.class);
        c.add(PropertyCriterion.eq(c.meta().name() + IndexAdapter.SECONDARY_PRROPERTY_SUFIX, newEntity.name().getValue().toLowerCase()));
        return c;
    }

    @Override
    public Province onEntityCreation(Province newEntity) {
        return newEntity;
    }

}
