/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.ref;

import com.pyx4j.entity.core.adapters.ReferenceAdapter;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;

public class ProvinceReferenceAdapter implements ReferenceAdapter<Province> {

    @Override
    public EntityQueryCriteria<Province> getMergeCriteria(Province newEntity) {
        EntityQueryCriteria<Province> c = EntityQueryCriteria.create(Province.class);
        if (!newEntity.code().isNull()) {
            c.add(PropertyCriterion.eq(c.proto().code(), newEntity.code().getValue()));
        } else {
            c.add(PropertyCriterion.eq(c.proto().name(), newEntity.name().getValue()));
        }
        return c;
    }

    @Override
    public Province onEntityCreation(Province newEntity) {
        throw new Error("Can't create new Province/State '" + ((newEntity.code().isNull()) ? newEntity.name().getValue() : newEntity.code().getValue()) + "'");
    }

}
