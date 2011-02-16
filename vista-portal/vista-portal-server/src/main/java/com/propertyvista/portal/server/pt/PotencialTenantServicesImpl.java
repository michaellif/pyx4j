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
import com.propertyvista.portal.rpc.pt.PotencialTenantServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

public class PotencialTenantServicesImpl extends EntityServicesImpl implements PotencialTenantServices {

    public static class UnitExistsImpl implements PotencialTenantServices.UnitExists {

        @Override
        public Boolean execute(UnitSelectionCriteria request) {
            return false;
        }

    }

    public static class GetCurrentApplicationImpl implements PotencialTenantServices.GetCurrentApplication {

        @Override
        public Application execute(UnitSelectionCriteria request) {
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtUserDataAccess.getCurrentUser()));
            Application application = secureRetrieve(criteria);
            if (application == null) {
                application = EntityFactory.create(Application.class);
                application.user().set(PtUserDataAccess.getCurrentUser());
                secureSave(application);
            }
            PtUserDataAccess.setCurrentUserApplication(application);
            return application;
        }
    }

    public static class RetrieveByPKImpl extends EntityServicesImpl.RetrieveByPKImpl implements PotencialTenantServices.RetrieveByPK {

        @Override
        public IEntity execute(EntityCriteriaByPK<?> request) {
            if (request.getPrimaryKey() == 0) {
                // Find first Entity of that type in Application 
                @SuppressWarnings("unchecked")
                EntityQueryCriteria<IApplicationEntity> criteria = EntityQueryCriteria.create((Class<IApplicationEntity>) request.getEntityClass());
                criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
                return secureRetrieve(criteria);
            } else {
                return super.execute(request);
            }
        }
    }

    public static class SaveImpl extends EntityServicesImpl.MergeSaveImpl implements PotencialTenantServices.Save {

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

            return super.execute(request);
        }

    }

    public static class GetAvalableUnitsImpl implements PotencialTenantServices.GetAvalableUnits {

        @Override
        public UnitSelection execute(UnitSelection request) {
            return null;
        }

    }
}
