/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.ref;

import com.pyx4j.entity.core.adapters.IndexAdapter;
import com.pyx4j.entity.core.adapters.ReferenceAdapter;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;

public class CountryReferenceAdapter implements ReferenceAdapter<Country> {

    @Override
    public EntityQueryCriteria<Country> getMergeCriteria(Country newEntity) {
        EntityQueryCriteria<Country> c = EntityQueryCriteria.create(Country.class);
        String name = newEntity.name().getValue().toLowerCase();
        if (name.equals("us") || name.equals("usa")) {
            name = "united states";
        } else if (name.equals("uk")) {
            name = "united kingdom";
        }
        c.add(new PropertyCriterion(c.proto().name().getFieldName() + IndexAdapter.SECONDARY_PRROPERTY_SUFIX, Restriction.EQUAL, name));
        return c;
    }

    @Override
    public Country onEntityCreation(Country newEntity) {
        return newEntity;
    }

}
