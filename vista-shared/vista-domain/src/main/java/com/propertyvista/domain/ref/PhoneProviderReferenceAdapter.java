/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.ref;

import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class PhoneProviderReferenceAdapter implements ReferenceAdapter<PhoneProvider> {

    @Override
    public EntityQueryCriteria<PhoneProvider> getMergeCriteria(PhoneProvider newEntity) {
        EntityQueryCriteria<PhoneProvider> c = EntityQueryCriteria.create(PhoneProvider.class);
        c.add(PropertyCriterion.eq(c.proto().name(), newEntity.name().getValue()));
        return c;
    }

    @Override
    public PhoneProvider onEntityCreation(PhoneProvider newEntity) {
        return newEntity;
    }

}
