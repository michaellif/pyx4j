/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-29
 * @author ArtyomB
 */
package com.propertyvista.biz.dashboard;

import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetStorageFacadeImpl implements GadgetStorageFacade {

    private final CustomizationPersistenceHelper<GadgetMetadata> gadgetPersistenceHelper = new CustomizationPersistenceHelper<GadgetMetadata>(
            GadgetMetadataHolder.class, GadgetMetadata.class);

    @Override
    public GadgetMetadata load(String gadgetId) {
        return gadgetPersistenceHelper.load(gadgetId);
    }

    @Override
    public void save(GadgetMetadata gadgetMetadata, boolean allowOverwrite) {
        gadgetPersistenceHelper.save(gadgetMetadata.gadgetId().getValue(), gadgetMetadata, allowOverwrite, true);
    }

    @Override
    public void delete(String gadgetId) {
        gadgetPersistenceHelper.delete(gadgetId);
    }

}
