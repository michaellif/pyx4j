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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class MaintenanceMetadataAbstractManager {

    public static final String cacheKey = "maintenance-metadata";

    protected void invalidateMeta(Building building) {
        CacheService.remove(getCacheKey(building));
    }

    protected abstract String getRoot();

    protected abstract String getCacheKey(Building building);

    protected abstract MaintenanceRequestMetadata retrieveMeta(Building building);

    protected abstract String[] getLevels();

    public MaintenanceRequestMetadata getMaintenanceMetadata(Building building) {
        MaintenanceRequestMetadata meta = (MaintenanceRequestMetadata) CacheService.get(getCacheKey(building));
        if (meta == null) {
            meta = retrieveMeta(building);
        }

        // retrieve levels
        if (meta.categoryLevels().isEmpty()) {
            String[] levels = getLevels();
            for (int i = 0; i < levels.length; i++) {
                meta.categoryLevels().add(createLevel(levels[i], i + 1));
            }
        }
        // retrieve categories
        if (meta.rootCategory().subCategories().getAttachLevel() != AttachLevel.Attached) {
            // load categories
            new CategoryTree(meta.rootCategory()).retrieveAll();
            // TODO - remove next line once new algo above is finalized
            // new CategoryTree(meta.rootCategory()).retrieveRecursive();
        }
        CacheService.put(getCacheKey(building), meta);

        return meta;
    }

    private MaintenanceRequestCategoryLevel createLevel(String name, int id) {
        MaintenanceRequestCategoryLevel level = EntityFactory.create(MaintenanceRequestCategoryLevel.class);
        level.level().setValue(id);
        level.name().setValue(name);
        return level;
    }

    public static class CategoryTree {
        private final MaintenanceRequestCategory root;

        private final Map<Key, MaintenanceRequestCategory> cTree;

        private int nodeCount = 0;

        public CategoryTree(MaintenanceRequestCategory root) {
            this.root = root;
            // initialize subCategories
            root.subCategories().setAttachLevel(AttachLevel.Attached);

            cTree = new HashMap<Key, MaintenanceRequestCategory>();
        }

        public MaintenanceRequestCategory getRoot() {
            return root;
        }

        public void retrieveAll() {
            // retrieve all categories for this root
            EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
            crit.eq(crit.proto().root(), root);
            List<MaintenanceRequestCategory> categories = Persistence.service().query(crit);
            // create category tree
            for (MaintenanceRequestCategory category : categories) {
                initNodeRecursive(category);
            }
        }

        public void retrieveRecursive() {
            retrieveRecursive(root);
        }

        public int getNodeCount() {
            return nodeCount;
        }

        private void retrieveRecursive(MaintenanceRequestCategory parent) {
            Persistence.service().retrieveMember(parent.subCategories());
            Integer level = parent.level().getValue();
            if (level == null) {
                level = 0;
            }
            for (MaintenanceRequestCategory cat : parent.subCategories()) {
                cat.level().setValue(level + 1);
                retrieveRecursive(cat);
                nodeCount++;
            }
        }

        /*
         * find parents up to the root if not done already; calculate levels
         */
        private MaintenanceRequestCategory initNodeRecursive(MaintenanceRequestCategory node) {
            if (node.isNull()) {
                return null;
            }
            if (node.equals(root)) {
                return root;
            }
            if (node.getAttachLevel() != AttachLevel.Attached) {
                Persistence.ensureRetrieve(node, AttachLevel.Attached);
            }
            MaintenanceRequestCategory treeCat = cTree.get(node.getPrimaryKey());
            if (treeCat == null) {
                MaintenanceRequestCategory parent = initNodeRecursive(node.parent());
                if (parent != null && !parent.isNull()) {
                    Integer level = parent.level().getValue();
                    if (level == null) {
                        level = 0;
                    }
                    node.level().setValue(level + 1);
                    node.subCategories().setAttachLevel(AttachLevel.Attached);
                    parent.subCategories().add(node);
                    cTree.put(node.getPrimaryKey(), node);
                    treeCat = node;
                    nodeCount++;
                }
            }
            return treeCat;
        }
    }
}
