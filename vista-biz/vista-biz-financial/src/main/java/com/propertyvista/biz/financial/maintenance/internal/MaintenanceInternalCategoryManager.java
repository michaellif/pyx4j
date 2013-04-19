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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryMeta;

public class MaintenanceInternalCategoryManager {

    private MaintenanceRequestCategoryMeta meta;

    private static class SingletonHolder {
        public static final MaintenanceInternalCategoryManager INSTANCE = new MaintenanceInternalCategoryManager();
    }

    static MaintenanceInternalCategoryManager instance() {
        return SingletonHolder.INSTANCE;
    }

    protected MaintenanceRequestCategoryMeta getMaintenanceRequestCategoryMeta(boolean levelsOnly) {
        MaintenanceRequestCategoryMeta result = EntityFactory.create(MaintenanceRequestCategoryMeta.class);
        if (meta == null) {
            meta = EntityFactory.create(MaintenanceRequestCategoryMeta.class);
        }
        if (meta.levels().isEmpty()) {
            EntityQueryCriteria<MaintenanceRequestCategoryLevel> labelCrit = EntityQueryCriteria.create(MaintenanceRequestCategoryLevel.class);
            meta.levels().addAll(Persistence.service().query(labelCrit));
        }
        result.levels().set(meta.levels());
        if (!levelsOnly) {
            if (meta.root().isEmpty()) {
                EntityQueryCriteria<MaintenanceRequestCategory> rootCrit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
                rootCrit.add(PropertyCriterion.eq(rootCrit.proto().name(), "ROOT"));
                meta.root().set(Persistence.service().retrieve(rootCrit));
                retrieveSubCategories(meta.root());
            }
            result.root().set(meta.root());
        }
        return result;
    }

    private void retrieveSubCategories(MaintenanceRequestCategory parent) {
        if (parent.subCategories() != null) {
            Persistence.service().retrieveMember(parent.subCategories());
            for (MaintenanceRequestCategory cat : parent.subCategories()) {
                retrieveSubCategories(cat);
            }
        }
    }
}
