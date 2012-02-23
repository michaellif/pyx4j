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
package com.propertyvista.server.billing.preload;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.ChargePolicy;

public class ChargePolicyDataModel {

    private final ChargeCodesDataModel chargeCodesDataModel;

    private final ProductItemTypesDataModel productItemTypesDataModel;

    private ChargePolicy chargePolicy;

    public ChargePolicyDataModel(ChargeCodesDataModel chargeCodesDataModel, ProductItemTypesDataModel productItemTypesDataModel) {
        this.chargeCodesDataModel = chargeCodesDataModel;
        this.productItemTypesDataModel = productItemTypesDataModel;
    }

    public void generate(boolean persist) {
        ChargePolicy chargePolicy = EntityFactory.create(ChargePolicy.class);
//        chargePolicy.chargePolicyItems()

        Persistence.service().persist(chargePolicy);
    }

    public ChargePolicy getChargePolicy() {
        return chargePolicy;
    }

}
