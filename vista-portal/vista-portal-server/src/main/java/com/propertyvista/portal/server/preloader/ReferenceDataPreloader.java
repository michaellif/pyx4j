/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority.PriorityLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.ref.PhoneProvider;
import com.propertyvista.portal.server.preloader.ido.MaintenanceTreeImport;

public class ReferenceDataPreloader extends AbstractDataPreloader {

    public ReferenceDataPreloader() {

    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PhoneProvider.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {
        createNamed(PhoneProvider.class, "Rogers", "Bell", "Telus", "Fido", "Mobilicity", "Primus", "Télébec", "Virgin Mobile", "Wind Mobile");
        createInternalMaintenancePreload();
        return null;
    }

    public void createInternalMaintenancePreload() {
        MaintenanceRequestMetadata meta = EntityFactory.create(MaintenanceRequestMetadata.class);
        meta.rootCategory().set(EntityFactory.create(MaintenanceRequestCategory.class));
        meta.rootCategory().name().setValue("ROOT");
        createMaintenanceCategories(meta.rootCategory(), "maintenance-tree-unit.csv", IssueElementType.ApartmentUnit);
        createMaintenanceCategories(meta.rootCategory(), "maintenance-tree-amenities.csv", IssueElementType.Amenities);
        createMaintenanceCategories(meta.rootCategory(), "maintenance-tree-exterior.csv", IssueElementType.Exterior);

        meta.priorities().addAll(createMaintenancePriorities());
        meta.statuses().addAll(createMaintenanceStatuses());
        Persistence.service().persist(meta.rootCategory());
        Persistence.service().persist(meta);
    }

    private MaintenanceRequestCategory createMaintenanceCategories(MaintenanceRequestCategory root, String dataFile, IssueElementType type) {
        // create categories for each level
        List<MaintenanceTreeImport> data = EntityCSVReciver.create(MaintenanceTreeImport.class).loadResourceFile(
                IOUtils.resourceFileName(dataFile, ReferenceDataPreloader.class));

        Map<String, MaintenanceRequestCategory> categories = new HashMap<String, MaintenanceRequestCategory>();
        for (MaintenanceTreeImport row : data) {
            // Find or create Element
            MaintenanceRequestCategory element = categories.get(type.toString() + row.rooms().getValue());
            if (element == null) {
                element = createMaintenanceCategory(row.rooms().getValue(), root);
                categories.put(type.toString() + row.rooms().getValue(), element);
                root.subCategories().add(element);
            }
            element.type().setValue(type);
            // Find or create  Subject
            MaintenanceRequestCategory subject = null;
            for (MaintenanceRequestCategory subj : element.subCategories()) {
                if (subj.name().getValue().equals(row.repairSubject().getValue())) {
                    subject = subj;
                    break;
                }
            }
            if (subject == null) {
                subject = createMaintenanceCategory(row.repairSubject().getValue(), root);
                element.subCategories().add(subject);
            }
            // Find or create Subject Details
            MaintenanceRequestCategory detail = null;
            for (MaintenanceRequestCategory det : subject.subCategories()) {
                if (det.name().getValue().equals(row.subjectDetails().getValue())) {
                    detail = det;
                    break;
                }
            }
            if (detail == null) {
                detail = createMaintenanceCategory(row.subjectDetails().getValue(), root);
                subject.subCategories().add(detail);
            }
            // Create IssueClassification
            MaintenanceRequestCategory classification = createMaintenanceCategory(row.issue().getValue(), root);
            detail.subCategories().add(classification);
        }
        return root;
    }

    private MaintenanceRequestCategory createMaintenanceCategory(String name, MaintenanceRequestCategory root) {
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        category.name().setValue(name);
        category.root().set(root);
        return category;
    }

    private List<MaintenanceRequestPriority> createMaintenancePriorities() {
        List<MaintenanceRequestPriority> priorities = new ArrayList<MaintenanceRequestPriority>();
        for (PriorityLevel level : PriorityLevel.values()) {
            MaintenanceRequestPriority priority = EntityFactory.create(MaintenanceRequestPriority.class);
            priority.level().setValue(level);
            priority.name().setValue(level.toString());
            priorities.add(priority);
        }
        return priorities;
    }

    private List<MaintenanceRequestStatus> createMaintenanceStatuses() {
        List<MaintenanceRequestStatus> statuses = new ArrayList<MaintenanceRequestStatus>();
        for (StatusPhase phase : StatusPhase.values()) {
            MaintenanceRequestStatus status = EntityFactory.create(MaintenanceRequestStatus.class);
            status.phase().setValue(phase);
            status.name().setValue(phase.toString());
            statuses.add(status);
        }
        return statuses;
    }

}
