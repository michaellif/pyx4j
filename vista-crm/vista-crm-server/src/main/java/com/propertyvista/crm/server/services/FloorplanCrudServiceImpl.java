/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.List;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.FloorplanCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanCrudServiceImpl extends GenericCrudServiceDtoImpl<Floorplan, FloorplanDTO> implements FloorplanCrudService {

    public FloorplanCrudServiceImpl() {
        super(Floorplan.class, FloorplanDTO.class);
    }

    @Override
    protected void enhanceDTO(Floorplan in, FloorplanDTO dto, boolean fromList) {

        EntityQueryCriteria<FloorplanAmenity> amenitysCriteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
        for (FloorplanAmenity amenity : Persistence.service().query(amenitysCriteria)) {
            dto.amenities().add(amenity);
        }

        if (!fromList) {
            Persistence.service().retrieve(dto.media());
        }
    }

    @Override
    protected void persistDBO(Floorplan dbo, FloorplanDTO in) {
        for (Media item : dbo.media()) {
            Persistence.service().merge(item);
        }
        boolean isCreate = dbo.id().isNull();

        if (dbo.counters().id().isNull()) {
            Persistence.service().persist(dbo.counters());
        }

        String origMarketingName = null;
        if (!isCreate) {
            Floorplan origFloorplan = Persistence.service().retrieve(Floorplan.class, dbo.id().getValue());
            origMarketingName = origFloorplan.marketingName().getValue();
        }

        Persistence.service().merge(dbo);
        updateCounters(dbo, origMarketingName);

        if (!isCreate) {
            EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), dbo));
            List<FloorplanAmenity> existingAmenities = Persistence.service().query(criteria);
            for (FloorplanAmenity amenity : existingAmenities) {
                if (!in.amenities().contains(amenity)) {
                    Persistence.service().delete(amenity);
                }
            }
        }
        for (FloorplanAmenity amenity : in.amenities()) {
            amenity.belongsTo().set(dbo);
        }
        Persistence.service().merge(in.amenities());

        //Update _values on AptUnit, TODO see if # had not been modified and then do not save AptUnit
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), dbo));
            List<AptUnit> units = Persistence.service().query(criteria);
            for (AptUnit u : units) {
                u.info()._bathrooms().set(dbo.bathrooms());
                u.info()._bedrooms().set(dbo.bedrooms());
            }
            Persistence.service().persist(units);
        }
    }

    private void updateCounters(Floorplan dbo, String origMarketingName) {
        boolean counterModified = false;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), dbo));
            Integer orig = dbo.counters()._unitCount().getValue();
            dbo.counters()._unitCount().setValue(Persistence.service().count(criteria));
            if (!dbo.counters()._unitCount().getValue().equals(orig)) {
                counterModified = true;
            }
        }
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan().marketingName(), dbo.marketingName().getValue()));
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), dbo.building()));
            Integer orig = dbo.counters()._marketingUnitCount().getValue();
            dbo.counters()._marketingUnitCount().setValue(Persistence.service().count(criteria));
            if (!dbo.counters()._marketingUnitCount().getValue().equals(orig)) {
                counterModified = true;
            }
        }
        if (counterModified) {
            Persistence.service().persist(dbo.counters());
        }

        //Update other Floorplans with the same marketingName
        {
            EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().marketingName(), dbo.marketingName().getValue()));
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), dbo.building()));
            for (Floorplan othrPlan : Persistence.service().query(criteria)) {
                othrPlan.counters()._marketingUnitCount().set(dbo.counters()._marketingUnitCount());
                Persistence.service().persist(othrPlan.counters());
            }
        }
        if (!EqualsHelper.equals(origMarketingName, dbo.marketingName().getValue())) {
            int newCount;
            {
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().floorplan().marketingName(), origMarketingName));
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), dbo.building()));
                newCount = Persistence.service().count(criteria);
            }
            EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().marketingName(), origMarketingName));
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), dbo.building()));
            for (Floorplan othrPlan : Persistence.service().query(criteria)) {
                othrPlan.counters()._marketingUnitCount().setValue(newCount);
                Persistence.service().persist(othrPlan.counters());
            }
        }
    }
}
