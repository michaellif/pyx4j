/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Collection;
import java.util.UUID;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.generator.DashboardGenerator;

public class DashboardPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        DashboardGenerator generator = new DashboardGenerator();

        persistDashboards(generator.systemDashboards);
        persistDashboards(generator.buildingDashboards);

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(generator.systemDashboards.size() + generator.buildingDashboards.size()).append(" dashboards");
        return sb.toString();
    }

    @Override
    public String delete() {
        return deleteAll(DashboardMetadata.class);
    }

    void persistDashboards(Collection<DashboardMetadata> metadatas) {
        for (DashboardMetadata dm : metadatas) {
            persistDashboard(dm);
        }
    }

    void persistDashboard(DashboardMetadata dm) {
        for (GadgetMetadata gadget : dm.gadgets()) {
            gadget.setPrimaryKey(new Key(1));
            gadget.gadgetId().setValue(UUID.randomUUID().toString());
            dm.gadgetIds().add(gadget.gadgetId().getValue());
            new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class).save(gadget.gadgetId().getValue(), gadget,
                    true);

        }

        Persistence.service().persist(dm);
    }

}
