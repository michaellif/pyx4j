/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_1_0_7;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.dashboard.GadgetStorageFacade;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;
import com.propertyvista.server.common.gadgets.GadgetMetadataRepository;

public class UpgradeProcedure1107 implements UpgradeProcedure {

    @Override
    public int getUpgradeStepsCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            upgradeOldArrearsGadget();
            break;
        default:
        }
    }

    private void upgradeOldArrearsGadget() {
        EntityQueryCriteria<GadgetMetadataHolder> criteria = EntityQueryCriteria.create(GadgetMetadataHolder.class);
        criteria.eq(criteria.proto().className(), ArrearsStatusGadgetMetadata.class.getSimpleName());
        List<GadgetMetadataHolder> gadgets = Persistence.service().query(criteria);

        for (GadgetMetadataHolder rawGadgetMetadata : gadgets) {
            ArrearsStatusGadgetMetadata oldGadgetMetadata = (ArrearsStatusGadgetMetadata) ServerSideFactory.create(GadgetStorageFacade.class).load(
                    rawGadgetMetadata.identifierKey().getValue());
            ArrearsSummaryGadgetMetadata upgradedGadgetMetadata = (ArrearsSummaryGadgetMetadata) GadgetMetadataRepository.get().createGadgetMetadata(
                    EntityFactory.getEntityPrototype(ArrearsSummaryGadgetMetadata.class));

            upgradedGadgetMetadata.gadgetId().setValue(rawGadgetMetadata.identifierKey().getValue());
            if (!oldGadgetMetadata.category().isNull()) {
                upgradedGadgetMetadata.customizeCategory().setValue(true);
                upgradedGadgetMetadata.category().setValue(oldGadgetMetadata.category().getValue());
            }
            if (oldGadgetMetadata.customizeDate().isBooleanTrue()) {
                upgradedGadgetMetadata.customizeDate().setValue(true);
                upgradedGadgetMetadata.asOf().setValue(oldGadgetMetadata.asOf().getValue());
            }
            ServerSideFactory.create(GadgetStorageFacade.class).delete(rawGadgetMetadata.identifierKey().getValue());
            ServerSideFactory.create(GadgetStorageFacade.class).save(upgradedGadgetMetadata, true);
        }
    }
}
