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
package com.propertyvista.biz.financial.maintenance;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;

public abstract class MaintenanceMetadataAbstractManager {

    public static final String cacheKey = "maintenance-metadata";

    protected void invalidateMeta() {
        CacheService.remove(cacheKey);
    }

    protected abstract String getRoot();

    protected abstract String[] getLevels();

    public MaintenanceRequestMetadata getMaintenanceMetadata(boolean levelsOnly) {
        MaintenanceRequestMetadata meta = (MaintenanceRequestMetadata) CacheService.get(cacheKey);

        MaintenanceRequestMetadata result = EntityFactory.create(MaintenanceRequestMetadata.class);
        if (meta == null) {
            meta = EntityFactory.create(MaintenanceRequestMetadata.class);
        }
        // retrieve levels
        if (meta.categoryLevels().isEmpty()) {
            String[] levels = getLevels();
            for (int i = 0; i < levels.length; i++) {
                meta.categoryLevels().add(createLevel(levels[i], i + 1));
            }
        }
        result.categoryLevels().set(meta.categoryLevels());
        // retrieve statuses
        if (meta.statuses().isEmpty()) {
            EntityQueryCriteria<MaintenanceRequestStatus> crit = EntityQueryCriteria.create(MaintenanceRequestStatus.class);
            meta.statuses().addAll(Persistence.service().query(crit));
        }
        result.statuses().set(meta.statuses());
        // retrieve priorities
        if (meta.priorities().isEmpty()) {
            EntityQueryCriteria<MaintenanceRequestPriority> crit = EntityQueryCriteria.create(MaintenanceRequestPriority.class);
            meta.priorities().addAll(Persistence.service().query(crit));
        }
        result.priorities().set(meta.priorities());
        if (!levelsOnly) {
            // retrieve categories
            if (meta.rootCategory().isNull()) {
                EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
                crit.add(PropertyCriterion.eq(crit.proto().name(), getRoot()));
                meta.rootCategory().set(Persistence.service().retrieve(crit));
                retrieveSubCategoriesRecursive(meta.rootCategory());

                if (meta.rootCategory().isNull()) {
                    // create root
                    MaintenanceRequestCategory root = EntityFactory.create(MaintenanceRequestCategory.class);
                    root.name().setValue(getRoot());
                    meta.rootCategory().set(root);
                    Persistence.service().persist(root);
                }
            }
            result.rootCategory().set(meta.rootCategory());
        }
        CacheService.put(cacheKey, meta);
        return result;
    }

    private void retrieveSubCategoriesRecursive(MaintenanceRequestCategory parent) {
        Persistence.service().retrieveMember(parent.subCategories());
        Integer level = parent.level().getValue();
        if (level == null) {
            level = 0;
        }
        for (MaintenanceRequestCategory cat : parent.subCategories()) {
            cat.level().setValue(level + 1);
            retrieveSubCategoriesRecursive(cat);
        }
    }

    private MaintenanceRequestCategoryLevel createLevel(String name, int id) {
        MaintenanceRequestCategoryLevel level = EntityFactory.create(MaintenanceRequestCategoryLevel.class);
        level.level().setValue(id);
        level.name().setValue(name);
        return level;
    }
}
