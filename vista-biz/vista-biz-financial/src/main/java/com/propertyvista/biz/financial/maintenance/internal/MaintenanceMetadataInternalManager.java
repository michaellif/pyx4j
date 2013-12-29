/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.internal;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.maintenance.MaintenanceMetadataAbstractManager;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class MaintenanceMetadataInternalManager extends MaintenanceMetadataAbstractManager {
    private final static I18n i18n = I18n.get(MaintenanceMetadataInternalManager.class);

    private static class SingletonHolder {
        public static final MaintenanceMetadataInternalManager INSTANCE = new MaintenanceMetadataInternalManager();
    }

    static MaintenanceMetadataInternalManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected String[] getLevels() {
        return new String[] { i18n.tr("Area"), i18n.tr("Repair Subject"), i18n.tr("Details"), i18n.tr("Issue") };
    }

    @Override
    protected String getRoot() {
        return "ROOT";
    }

    @Override
    protected String getCacheKey(Building building) {
        // Default implementation uses same (preloaded) instance of MaintenanceRequestMetadata for all buildings
        return MaintenanceMetadataAbstractManager.cacheKey;
    }

    @Override
    protected MaintenanceRequestMetadata retrieveMeta(Building building) {
        // Only one instance of metadata exists in the DB
        EntityQueryCriteria<MaintenanceRequestMetadata> crit = EntityQueryCriteria.create(MaintenanceRequestMetadata.class);
        return Persistence.service().retrieve(crit);
    }
}
