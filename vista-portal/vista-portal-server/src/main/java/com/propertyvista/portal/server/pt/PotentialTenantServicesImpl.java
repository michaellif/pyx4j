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

import com.propertyvista.portal.domain.IUserEntity;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.IApplicationEntity;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

public class PotentialTenantServicesImpl extends EntityServicesImpl implements PotentialTenantServices {

    public static class UnitExistsImpl implements PotentialTenantServices.UnitExists {

        @Override
        public Boolean execute(UnitSelectionCriteria request) {
            //TODO Dmitry please implement.
            return false;
        }

    }

    public static class GetCurrentApplicationImpl implements PotentialTenantServices.GetCurrentApplication {

        @Override
        public Application execute(UnitSelectionCriteria request) {
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtUserDataAccess.getCurrentUser()));
            Application application = secureRetrieve(criteria);
            if (application == null) {

                //TODO Dmitry find if UnitSelectionCriteria is valid. e.g. the same as in UnitExistsImpl
                UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
                unitSelection.buildingName().set(request.buildingName());
                unitSelection.floorplanName().set(request.floorplanName());
                //unitSelection.building().set(????);  //TODO Dmitry use found building

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

                if ((!unitSelection.buildingName().equals(request.buildingName())) || (!unitSelection.floorplanName().equals(request.floorplanName()))) {
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
                loadAvalableUnits((UnitSelection) ret);
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
                loadAvalableUnits((UnitSelection) ret);
            }

            return ret;
        }

    }

    /**
     * TODO Dmitry
     * 
     * Build the AvalableUnitsByFloorplan object.
     */
    private static void loadAvalableUnits(UnitSelection unitSelection) {

    }

}
