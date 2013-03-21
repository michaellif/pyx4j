/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.test.mock.MockDataModel;

public class FeatureItemTypeDataModel extends MockDataModel<FeatureItemType> {

    public FeatureItemTypeDataModel() {
    }

    @Override
    protected void generate() {

        List<FeatureItemType> featureItemTypes = new ArrayList<FeatureItemType>();

        featureItemTypes.add(generateChargeItemType("Regular Parking", Feature.Type.parking));
        featureItemTypes.add(generateChargeItemType("Wide Parking", Feature.Type.parking));
        featureItemTypes.add(generateChargeItemType("Narrow Parking", Feature.Type.parking));
        featureItemTypes.add(generateChargeItemType("Disabled Parking", Feature.Type.parking));
        featureItemTypes.add(generateChargeItemType("Cat", Feature.Type.pet));
        featureItemTypes.add(generateChargeItemType("Dog", Feature.Type.pet));
        featureItemTypes.add(generateChargeItemType("Small Locker", Feature.Type.locker));
        featureItemTypes.add(generateChargeItemType("Medium Locker", Feature.Type.locker));
        featureItemTypes.add(generateChargeItemType("Large Locker", Feature.Type.locker));
        featureItemTypes.add(generateChargeItemType("Fitness", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Pool", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Furnished", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Key", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Access Card", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Cable", Feature.Type.addOn));
        featureItemTypes.add(generateChargeItemType("Water", Feature.Type.utility));
        featureItemTypes.add(generateChargeItemType("Gas", Feature.Type.utility));
        featureItemTypes.add(generateChargeItemType("Hydro", Feature.Type.utility));
        featureItemTypes.add(generateChargeItemType("Booking", Feature.Type.booking));

        Persistence.service().persist(featureItemTypes);
    }

    private FeatureItemType generateChargeItemType(String name, Feature.Type featureType) {
        FeatureItemType type = EntityFactory.create(FeatureItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        addItem(type);
        return type;
    }

}
