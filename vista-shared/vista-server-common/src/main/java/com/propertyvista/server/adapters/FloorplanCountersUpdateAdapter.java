/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.MemberMeta;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class FloorplanCountersUpdateAdapter implements com.propertyvista.shared.adapters.FloorplanCountersUpdateAdapter {

    @Override
    public boolean allowModifications(AptUnit entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        Floorplan floorplanOrig = null;
        if (valueOrig != null) {
            floorplanOrig = Persistence.service().retrieve(Floorplan.class, (Key) valueOrig);
        }
        Floorplan floorplanNew = null;
        if (valueNew != null) {
            floorplanNew = Persistence.service().retrieve(Floorplan.class, (Key) valueNew);
        }

        boolean updateMarketingCounter = true;
        if ((floorplanNew != null) && (floorplanOrig != null) && (floorplanOrig.marketingName().equals(floorplanNew.marketingName()))) {
            updateMarketingCounter = false;
        }

        if (floorplanOrig != null) {
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplanOrig));
                floorplanOrig.counters()._unitCount().setValue(Persistence.service().count(criteria) - 1);
            }
            if (updateMarketingCounter) {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan().marketingName(), floorplanOrig.marketingName().getValue()));
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), floorplanOrig.building()));
                floorplanOrig.counters()._marketingUnitCount().setValue(Persistence.service().count(criteria) - 1);
            }
            boolean addingCounters = floorplanOrig.counters().id().isNull();
            Persistence.service().persist(floorplanOrig.counters());
            if (addingCounters) {
                Persistence.service().persist(floorplanOrig);
            }
        }

        if (floorplanNew != null) {
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplanNew));
                floorplanNew.counters()._unitCount().setValue(Persistence.service().count(criteria) + 1);
            }
            if (updateMarketingCounter) {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan().marketingName(), floorplanNew.marketingName().getValue()));
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), floorplanNew.building()));
                floorplanNew.counters()._marketingUnitCount().setValue(Persistence.service().count(criteria) + 1);
            }
            boolean addingCounters = floorplanNew.counters().id().isNull();
            Persistence.service().persist(floorplanNew.counters());
            if (addingCounters) {
                Persistence.service().persist(floorplanNew);
            }
        }

        return true;
    }
}
