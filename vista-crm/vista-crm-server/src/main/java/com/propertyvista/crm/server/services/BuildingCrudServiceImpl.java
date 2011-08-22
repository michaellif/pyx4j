/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BuildingDTO;

public class BuildingCrudServiceImpl extends GenericCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(Building in, BuildingDTO dto, boolean fromList) {

        if (!fromList) {
            // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
            PersistenceServicesFactory.getPersistenceService().retrieve(in.media());
            PersistenceServicesFactory.getPersistenceService().retrieve(in.serviceCatalog());
            PersistenceServicesFactory.getPersistenceService().retrieve(in.contacts().contacts());
            PersistenceServicesFactory.getPersistenceService().retrieve(in.marketing().adBlurbs());

            EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
            for (BuildingAmenity item : PersistenceServicesFactory.getPersistenceService().query(amenitysCriteria)) {
                dto.amenities().add(item);
            }

            EntityQueryCriteria<ServiceItemType> serviceItemCriteria = EntityQueryCriteria.create(ServiceItemType.class);
            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().featureType(), Feature.Type.utility));
            for (ServiceItemType item : PersistenceServicesFactory.getPersistenceService().query(serviceItemCriteria)) {
                dto.availableUtilities().add(item);
            }
        } else {
            // just clear unnecessary data before serialisation: 
            in.marketing().description().setValue(null);
        }
    }

    @Override
    protected void enhanceSaveDBO(Building dbo, BuildingDTO dto) {
        for (Media item : dbo.media()) {
            PersistenceServicesFactory.getPersistenceService().merge(item);
        }

        // save detached entities:
        PersistenceServicesFactory.getPersistenceService().merge(dbo.serviceCatalog());
    }
}
