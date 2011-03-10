/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.ChargeType;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.domain.pt.PetChargeRule;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.rpc.pt.services.PetsServices;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.VoidSerializable;

public class PetsServicesImpl extends EntityServicesImpl implements PetsServices {

    //======== New way  ===================

    @Override
    public void retrieve(VoidSerializable nothing, AsyncCallback<Pets> callback) {
        EntityQueryCriteria<Pets> criteria = EntityQueryCriteria.create(Pets.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        Pets ret = secureRetrieve(criteria);
        if (ret == null) {
            ret = EntityFactory.create(Pets.class);
        }
        loadTransientData(ret);

        callback.onSuccess(ret);
    }

    @Override
    public void save(Pets editableEntity, AsyncCallback<Pets> callback) {

        // app specific security stuff
        final Application application = PtUserDataAccess.getCurrentUserApplication();
        // update Owned Members  TODO move to super or to framework
        EntityGraph.applyRecursively(editableEntity, new EntityGraph.ApplyMethod() {
            @Override
            public void apply(IEntity entity) {
                if (entity instanceof IBoundToApplication) {
                    ((IBoundToApplication) entity).application().set(application);
                }
            }
        });

        secureSave(editableEntity);

        loadTransientData(editableEntity);

        callback.onSuccess(editableEntity);
    }

    /*
     * We can load the data required for pets validation in additional RPC call. But we
     * want to save on number of requests to make application work faster for users, each
     * request may take 10 seconds on slow network connection.
     */
    private static void loadTransientData(Pets pets) {
        // TODO get it from building
        PetChargeRule petCharge = EntityFactory.create(PetChargeRule.class);
        petCharge.chargeType().setValue(ChargeType.monthly);
        petCharge.value().setValue(20);
        pets.petChargeRule().set(petCharge);
        pets.petWeightMaximum().setValue(25);
        pets.petsMaximum().setValue(3);
    }

    //======== Old way  ===================

    public static class RetrieveImpl implements PetsServices.Retrieve {

        @Override
        public Pets execute(VoidSerializable request) {
            EntityQueryCriteria<Pets> criteria = EntityQueryCriteria.create(Pets.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
            Pets ret = secureRetrieve(criteria);
            if (ret == null) {
                ret = EntityFactory.create(Pets.class);
            }
            loadTransientData(ret);
            return ret;
        }

    }

    public static class SaveImpl implements PetsServices.Save {

        @Override
        public Pets execute(Pets request) {

            // TODO app specific security

            secureSave(request);

            loadTransientData(request);

            return request;
        }

    }

}
