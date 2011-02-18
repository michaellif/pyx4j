/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Floorplan;
import com.propertyvista.portal.domain.IUserEntity;
import com.propertyvista.portal.domain.Picture;
import com.propertyvista.portal.domain.Unit;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.IApplicationEntity;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class PotentialTenantServicesImpl extends EntityServicesImpl implements PotentialTenantServices {

    private final static Logger log = LoggerFactory.getLogger(PotentialTenantServicesImpl.class);

    public static class UnitExistsImpl implements PotentialTenantServices.UnitExists, IsIgnoreSessionTokenService {

        @Override
        public Boolean execute(UnitSelectionCriteria request) {

            log.info("Checking whether unit exists for {} ", request);

            // find building first
            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
            buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), request.propertyCode().getValue()));
            Building building = PersistenceServicesFactory.getPersistenceService().retrieve(buildingCriteria);

            log.info("Found [{}] building {}", request.propertyCode().getValue(), building);
            if (building == null) {
                return false;
            }

            // find floor plan
            EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
            floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), request.floorplanName().getValue()));
            Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);

            log.info("Found [{}] floorplan {}", request.floorplanName().getValue(), floorplan);
            if (floorplan == null) {
                return false;
            }

            // find unit with floor plan
            EntityQueryCriteria<Unit> unitCriteria = EntityQueryCriteria.create(Unit.class);
            unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().building(), building));
            unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().floorplan(), floorplan));
            Unit unit = PersistenceServicesFactory.getPersistenceService().retrieve(unitCriteria);

            return unit != null;
        }
    }

    public static class GetCurrentApplicationImpl implements PotentialTenantServices.GetCurrentApplication {

        @Override
        public Application execute(UnitSelectionCriteria request) {

            // find application by user
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtUserDataAccess.getCurrentUser()));
            Application application = secureRetrieve(criteria);

            if (application == null) {

                // find building
                EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
                buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), request.propertyCode().getValue()));
                Building building = PersistenceServicesFactory.getPersistenceService().retrieve(buildingCriteria);

                if (building == null) {
                    log.info("Could not find building with propertyCode {}", request.propertyCode());
                    throw new UserRuntimeException("Selected building not found");
                }

                UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
                unitSelection.propertyCode().set(request.propertyCode());
                unitSelection.floorplanName().set(request.floorplanName());
                unitSelection.building().set(building);

                application = EntityFactory.create(Application.class);
                application.user().set(PtUserDataAccess.getCurrentUser());
                secureSave(application);

                unitSelection.application().set(application);
                secureSave(unitSelection);

            } else {
                //Verify if buildingName and floorplanName are the same
                EntityQueryCriteria<UnitSelection> unitSelectionCriteria = EntityQueryCriteria.create(UnitSelection.class);
                criteria.add(PropertyCriterion.eq(unitSelectionCriteria.proto().application(), application));
                UnitSelection unitSelection = secureRetrieve(unitSelectionCriteria);

                if ((!unitSelection.propertyCode().equals(request.propertyCode())) || (!unitSelection.floorplanName().equals(request.floorplanName()))) {
                    //TODO What if they are diferent ?  We need to discard some part of application flow.
                }
            }
            PtUserDataAccess.setCurrentUserApplication(application);
            return application;
        }
    }

    public static class RetrieveByPKImpl extends EntityServicesImpl.RetrieveByPKImpl implements PotentialTenantServices.RetrieveByPK {

        @Override
        public IEntity execute(EntityCriteriaByPK<?> request) {
            IEntity ret;
            if (request.getPrimaryKey() == 0) {
                // Find first Entity of that type in Application 
                @SuppressWarnings("unchecked")
                EntityQueryCriteria<IApplicationEntity> criteria = EntityQueryCriteria.create((Class<IApplicationEntity>) request.getEntityClass());
                criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
                ret = secureRetrieve(criteria);
            } else {
                ret = super.execute(request);
            }

            if (ret instanceof UnitSelection) {
                loadAvailableUnits((UnitSelection) ret);
            }

            return ret;
        }
    }

    public static class SaveImpl extends EntityServicesImpl.MergeSaveImpl implements PotentialTenantServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            final User currentUser = PtUserDataAccess.getCurrentUser();
            final Application application = PtUserDataAccess.getCurrentUserApplication();

            if ((request instanceof IUserEntity) || (request instanceof IApplicationEntity)) {
                // update Owned Members
                EntityGraph.applyRecursively(request, new EntityGraph.ApplyMethod() {

                    @Override
                    public void apply(IEntity entity) {
                        if (entity instanceof IUserEntity) {
                            ((IUserEntity) entity).user().set(currentUser);
                        } else if (entity instanceof IApplicationEntity) {
                            ((IApplicationEntity) entity).application().set(application);
                        }
                    }
                });
            } else {
                throw new UnRecoverableRuntimeException("Invalid object");
            }

            IEntity ret = super.execute(request);

            if (ret instanceof UnitSelection) {
                loadAvailableUnits((UnitSelection) ret);
            }

            return ret;
        }

    }

    /**
     * Build the AvalableUnitsByFloorplan object.
     */
    private static void loadAvailableUnits(UnitSelection unitSelection) {
        AvailableUnitsByFloorplan availableUnits = unitSelection.availableUnits();

        // find floor plan
        EntityQueryCriteria<Floorplan> floorplanCriteria = EntityQueryCriteria.create(Floorplan.class);
        floorplanCriteria.add(PropertyCriterion.eq(floorplanCriteria.proto().name(), unitSelection.floorplanName().getValue()));
        Floorplan floorplan = PersistenceServicesFactory.getPersistenceService().retrieve(floorplanCriteria);

        if (floorplan == null) {
            log.info("Could not find floorplan {}", unitSelection.floorplanName());
            return;
        }
        availableUnits.floorplan().set(floorplan);
        for (Picture picture : floorplan.pictures()) {
            prepareImage(picture);
        }

        // find units
        EntityQueryCriteria<Unit> criteria = EntityQueryCriteria.create(Unit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), unitSelection.building()));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), floorplan));
        List<Unit> units = PersistenceServicesFactory.getPersistenceService().query(criteria);
        log.info("Found " + units.size() + " units");
        availableUnits.units().addAll(units);
    }

    //TODO If IE6 ?
    private static void prepareImage(Picture picture) {
        if (!picture.content().isNull()) {
            picture.contentBase64().setValue(new Base64().encodeToString(picture.content().getValue()));
        }
    }
}
