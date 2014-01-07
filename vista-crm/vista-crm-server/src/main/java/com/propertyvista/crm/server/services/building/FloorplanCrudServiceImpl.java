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
package com.propertyvista.crm.server.services.building;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanCrudServiceImpl extends AbstractCrudServiceDtoImpl<Floorplan, FloorplanDTO> implements FloorplanCrudService {

    public FloorplanCrudServiceImpl() {
        super(Floorplan.class, FloorplanDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(Floorplan bo, FloorplanDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.amenities());
        to.amenities().set(bo.amenities());
        Persistence.ensureRetrieve(bo.ilsSummary(), AttachLevel.Attached);
        to.ilsSummary().set(bo.ilsSummary());
        Persistence.ensureRetrieve(bo.media(), AttachLevel.Attached);
        to.media().set(bo.media());
        // ils
        EntityQueryCriteria<ILSProfileFloorplan> criteria = EntityQueryCriteria.create(ILSProfileFloorplan.class);
        criteria.eq(criteria.proto().floorplan(), bo);
        to.ilsProfile().addAll(Persistence.service().query(criteria));
    }

    @Override
    protected void persist(Floorplan dbo, FloorplanDTO in) {
        boolean isCreate = dbo.id().isNull();

        if (dbo.counters().id().isNull()) {
            // initialize unit counters
            dbo.counters()._unitCount().setValue(0);
            dbo.counters()._marketingUnitCount().setValue(0);
        }

        String origMarketingName = null;
        if (!isCreate) {
            Floorplan origFloorplan = Persistence.service().retrieve(Floorplan.class, dbo.id().getValue());
            origMarketingName = origFloorplan.marketingName().getValue();
        }

        Persistence.service().merge(dbo);
        updateCounters(dbo, origMarketingName);

        //  Update _values on AptUnit, TODO see if # had not been modified and then do not save AptUnit
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
        // ils marketing
        {
            EntityQueryCriteria<ILSProfileFloorplan> criteria = EntityQueryCriteria.create(ILSProfileFloorplan.class);
            criteria.eq(criteria.proto().floorplan(), in);
            List<ILSProfileFloorplan> ilsData = Persistence.service().query(criteria);
            ilsData.clear();
            for (ILSProfileFloorplan profile : in.ilsProfile()) {
                profile.floorplan().set(dbo);
                ilsData.add(profile);
            }
            Persistence.service().persist(ilsData);
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
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), dbo.building()));
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
                criteria.add(PropertyCriterion.eq(criteria.proto().building(), dbo.building()));
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

    @Override
    public void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback, Floorplan floorplan) {
        // find configured vendors for the building
        Persistence.ensureRetrieve(floorplan, AttachLevel.Attached);
        Persistence.ensureRetrieve(floorplan.building(), AttachLevel.IdOnly);
        Vector<ILSVendor> vendors = new Vector<ILSVendor>();
        EntityQueryCriteria<ILSProfileBuilding> crit = EntityQueryCriteria.create(ILSProfileBuilding.class);
        crit.eq(crit.proto().building(), floorplan.building());
        for (ILSProfileBuilding config : Persistence.service().query(crit)) {
            vendors.add(config.vendor().getValue());
        }
        callback.onSuccess(vendors);
    }

}
