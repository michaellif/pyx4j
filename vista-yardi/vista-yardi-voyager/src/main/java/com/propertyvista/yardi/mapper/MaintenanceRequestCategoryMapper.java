/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 4, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.util.ArrayList;
import java.util.List;

import com.yardi.entity.maintenance.meta.Category;
import com.yardi.entity.maintenance.meta.YardiMaintenanceConfigMeta;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;

public class MaintenanceRequestCategoryMapper {

    public MaintenanceRequestCategory map(YardiMaintenanceConfigMeta meta) {
        MaintenanceRequestCategory topCategory = EntityFactory.create(MaintenanceRequestCategory.class);

        if (meta != null && meta.getCategories() != null) {
            for (Category yCategory : meta.getCategories().getCategory()) {
                MaintenanceRequestCategory category = createCategory(yCategory.getName());
                category.subCategories().addAll(getSubCategories(yCategory.getSubCategory()));

                topCategory.subCategories().add(category);
            }
        }

        return topCategory;
    }

    private List<MaintenanceRequestCategory> getSubCategories(List<String> ySubCategories) {
        List<MaintenanceRequestCategory> subCategories = new ArrayList<MaintenanceRequestCategory>();

        for (String ySubCategory : ySubCategories) {
            MaintenanceRequestCategory subCategory = createCategory(ySubCategory);
            subCategories.add(subCategory);
        }

        return subCategories;
    }

    private MaintenanceRequestCategory createCategory(String name) {
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        category.name().setValue(name);
        return category;
    }
}
