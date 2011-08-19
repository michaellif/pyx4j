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
package com.propertyvista.domain.property;

import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class PropertyManagerReferenceAdapter implements ReferenceAdapter<PropertyManager> {

    @Override
    public EntityQueryCriteria<PropertyManager> getMergeCriteria(PropertyManager newEntity) {
        EntityQueryCriteria<PropertyManager> c = EntityQueryCriteria.create(PropertyManager.class);
        c.add(PropertyCriterion.eq(c.proto().name(), newEntity.name().getValue()));
        return c;
    }

    @Override
    public PropertyManager onEntityCreation(PropertyManager newEntity) {
        return newEntity;
    }

}
