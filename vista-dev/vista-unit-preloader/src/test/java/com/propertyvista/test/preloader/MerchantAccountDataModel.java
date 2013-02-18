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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;

public class MerchantAccountDataModel {

    private final BuildingDataModel buildingDataModel;

    public MerchantAccountDataModel(PreloadConfig config, BuildingDataModel buildingDataModel) {
        this.buildingDataModel = buildingDataModel;
    }

    public void generate() {
        MerchantAccount merchantAccount = Persistence.service().retrieve(EntityQueryCriteria.create(MerchantAccount.class));

        if (merchantAccount == null) {
            final Pmc pmc = VistaDeployment.getCurrentPmc();

            final MerchantAccount createMerchantAccount = EntityFactory.create(MerchantAccount.class);
            createMerchantAccount.merchantTerminalId().setValue("BIRCHTT1");
            createMerchantAccount.bankId().setValue("9");
            createMerchantAccount.branchTransitNumber().setValue("54685");
            createMerchantAccount.accountNumber().setValue("548758665");
            createMerchantAccount.chargeDescription().setValue("Pay for Tests");

            NamespaceManager.runInTargetNamespace(VistaNamespace.operationsNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    ServerSideFactory.create(PmcFacade.class).persistMerchantAccount(pmc, createMerchantAccount);
                    Persistence.service().commit();
                    return null;
                }
            });

            merchantAccount = createMerchantAccount;
        }

        BuildingMerchantAccount bma = EntityFactory.create(BuildingMerchantAccount.class);
        bma.merchantAccount().set(merchantAccount);
        bma.building().set(buildingDataModel.getBuilding());
        Persistence.service().persist(bma);
    }
}
