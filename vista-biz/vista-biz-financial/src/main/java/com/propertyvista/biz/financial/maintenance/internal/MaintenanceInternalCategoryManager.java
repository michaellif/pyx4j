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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;

public class MaintenanceInternalCategoryManager {

    private static class SingletonHolder {
        public static final MaintenanceInternalCategoryManager INSTANCE = new MaintenanceInternalCategoryManager();
    }

    static MaintenanceInternalCategoryManager instance() {
        return SingletonHolder.INSTANCE;
    }

    protected MaintenanceRequestCategory getMaintenanceRequestCategories() {
        // TODO replace with real preload data
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        List<MaintenanceRequestCategory> subCategories = new ArrayList<MaintenanceRequestCategory>();
        for (int i = 0; i < 5; i++) {
            category.name().setValue("subCategory" + i);
            subCategories.add(category);
        }
        category.name().setValue("topCategory");

        return category;
    }

}
