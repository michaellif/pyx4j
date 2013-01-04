/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.preloader;

import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.OnboardingMerchantAccount;

public class MerchantAccountDataModel {

    private final BuildingDataModel buildingDataModel;

    public MerchantAccountDataModel(PreloadConfig config, BuildingDataModel buildingDataModel) {
        this.buildingDataModel = buildingDataModel;
    }

    public void generate() {
        MerchantAccount merchantAccount = Persistence.service().retrieve(EntityQueryCriteria.create(MerchantAccount.class));

        if (merchantAccount == null) {
            merchantAccount = EntityFactory.create(MerchantAccount.class);

            final OnboardingMerchantAccount globalbMerchantAccount = EntityFactory.create(OnboardingMerchantAccount.class);
            globalbMerchantAccount.pmc().set(VistaDeployment.getCurrentPmc());
            globalbMerchantAccount.merchantTerminalId().setValue("BIRCHTT1");
            globalbMerchantAccount.bankId().setValue("9");
            globalbMerchantAccount.branchTransitNumber().setValue("54685");
            globalbMerchantAccount.accountNumber().setValue("548758665");
            globalbMerchantAccount.chargeDescription().setValue("Pay for Tests");

            merchantAccount.invalid().setValue(Boolean.FALSE);

            merchantAccount.bankId().setValue(globalbMerchantAccount.bankId().getValue());
            merchantAccount.branchTransitNumber().setValue(globalbMerchantAccount.branchTransitNumber().getValue());
            merchantAccount.accountNumber().setValue(globalbMerchantAccount.accountNumber().getValue());

            merchantAccount.chargeDescription().setValue(globalbMerchantAccount.chargeDescription().getValue());
            merchantAccount.merchantTerminalId().setValue(globalbMerchantAccount.merchantTerminalId().getValue());

            Persistence.service().persist(merchantAccount);

            // join accounts
            globalbMerchantAccount.merchantAccountKey().setValue(merchantAccount.getPrimaryKey());

            NamespaceManager.runInTargetNamespace(VistaNamespace.adminNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    Persistence.service().persist(globalbMerchantAccount);
                    Persistence.service().commit();
                    return null;
                }
            });
        }

        BuildingMerchantAccount bma = EntityFactory.create(BuildingMerchantAccount.class);
        bma.merchantAccount().set(merchantAccount);
        bma.building().set(buildingDataModel.getBuilding());
        Persistence.service().persist(bma);
    }
}
