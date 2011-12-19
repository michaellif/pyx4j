/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policy;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.OrganizationPolicyBrowserService;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;

public class OrganizationPolicyBrowserServiceImpl implements OrganizationPolicyBrowserService {

    @Override
    public void getCountries(AsyncCallback<Vector<Country>> callback) {
        EntityQueryCriteria<Country> criteria = new EntityQueryCriteria<Country>(Country.class);

        callback.onSuccess(new Vector<Country>(Persistence.service().query(criteria)));
    }

    @Override
    public void getComplexes(AsyncCallback<Vector<Complex>> callback, Key countryPk) {
        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().info().address().country(), countryPk));
        criteria.add(PropertyCriterion.eq(criteria.proto().complexPrimary(), true));
        List<Building> primaryBuildings = Persistence.service().query(criteria);

        Vector<Complex> complexes = new Vector<Complex>(primaryBuildings.size());
        for (Building building : primaryBuildings) {
            //Persistence.service().retrieve(building.complex());
            complexes.add((Complex) building.complex().cloneEntity());
        }

        callback.onSuccess(complexes);
    }

    @Override
    public void getBuildings(AsyncCallback<Vector<Building>> callback, Key complexPk) {
        EntityQueryCriteria<Building> criteria = new EntityQueryCriteria<Building>(Building.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().complex(), complexPk));

        callback.onSuccess(new Vector<Building>(Persistence.service().query(criteria)));
    }

    @Override
    public void getUnits(AsyncCallback<Vector<AptUnit>> callback, Key buildingPk) {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);

        criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), buildingPk));

        callback.onSuccess(new Vector<AptUnit>(Persistence.service().query(criteria)));
    }

}
