/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_0_9;

import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;

public class UpgradeProcedure109 implements UpgradeProcedure {

    @Override
    public int getUpgradeStepsCount() {
        return 1;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runMigrateArrearsGadgetSettigns();
            break;
        default:
            throw new IllegalArgumentException("Wrong step number: " + upgradeStep);
        }
    }

    private void runMigrateArrearsGadgetSettigns() {
        EntityQueryCriteria<GadgetMetadataHolder> criteria = EntityQueryCriteria.create(GadgetMetadataHolder.class);
        criteria.eq(criteria.proto().className(), ArrearsStatusGadgetMetadata.class.getSimpleName());
        List<GadgetMetadataHolder> gadgetSettingsList = Persistence.service().query(criteria);
        for (GadgetMetadataHolder gadgetSettings : gadgetSettingsList) {
            gadgetSettings.serializedForm().setValue(ArrearsStatusGadgetSettingsUpgrader.upgradeSettings(gadgetSettings.serializedForm().getValue()));
            Persistence.service().persist(gadgetSettings);
        }

    }

}
