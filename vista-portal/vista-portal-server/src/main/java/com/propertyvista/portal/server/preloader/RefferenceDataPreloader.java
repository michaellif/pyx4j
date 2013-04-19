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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.IssueElement;
import com.propertyvista.domain.maintenance.IssueRepairSubject;
import com.propertyvista.domain.maintenance.IssueSubjectDetails;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryLevel;
import com.propertyvista.domain.ref.PhoneProvider;
import com.propertyvista.portal.server.preloader.ido.MaintenanceTreeImport;

public class RefferenceDataPreloader extends AbstractDataPreloader {

    public RefferenceDataPreloader() {

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
//        createIssueClassifications();
        createMaintenanceCategories();
        return null;
    }

    private void createIssueClassifications() {
        List<MaintenanceTreeImport> data = EntityCSVReciver.create(MaintenanceTreeImport.class).loadResourceFile(
                IOUtils.resourceFileName("maintenance-tree.csv", RefferenceDataPreloader.class));

        Map<String, IssueElement> elements = new HashMap<String, IssueElement>();
        for (MaintenanceTreeImport row : data) {
            // Find or create Element
            IssueElement element = elements.get(row.type().getValue() + row.rooms().getValue());
            if (element == null) {
                element = EntityFactory.create(IssueElement.class);
                element.type().set(row.type());
                element.name().set(row.rooms());
                Persistence.service().persist(element);
                elements.put(row.type().getValue() + row.rooms().getValue(), element);
            }
            // Find or create  Subject
            IssueRepairSubject subject = null;
            for (IssueRepairSubject subj : element.subjects()) {
                if (subj.name().getValue().equals(row.repairSubject().getValue())) {
                    subject = subj;
                    break;
                }
            }
            if (subject == null) {
                subject = EntityFactory.create(IssueRepairSubject.class);
                subject.issueElement().set(element);
                subject.name().set(row.repairSubject());
                Persistence.service().persist(subject);
                element.subjects().add(subject);
            }
            // Find or create Subject Details
            IssueSubjectDetails detail = null;
            for (IssueSubjectDetails det : subject.details()) {
                if (det.name().getValue().equals(row.subjectDetails().getValue())) {
                    detail = det;
                    break;
                }
            }
            if (detail == null) {
                detail = EntityFactory.create(IssueSubjectDetails.class);
                detail.subject().set(subject);
                detail.name().set(row.subjectDetails());
                Persistence.service().persist(detail);
                subject.details().add(detail);
            }
            // Create IssueClassification
            IssueClassification classification = EntityFactory.create(IssueClassification.class);
            classification.subjectDetails().set(detail);
            classification.issue().set(row.issue());
            classification.priority().set(row.priority());
            Persistence.service().persist(classification);
        }
    }

    private void createMaintenanceCategories() {
        // persist levels
        MaintenanceRequestCategoryLevel level1 = createMaintenanceCategoryLevel("IssueElement", 1);
        MaintenanceRequestCategoryLevel level2 = createMaintenanceCategoryLevel("IssueRepairSubject", 2);
        MaintenanceRequestCategoryLevel level3 = createMaintenanceCategoryLevel("IssueSubjectDetails", 3);
        MaintenanceRequestCategoryLevel level4 = createMaintenanceCategoryLevel("IssueClassification", 4);
        Persistence.service().persist(level1);
        Persistence.service().persist(level2);
        Persistence.service().persist(level3);
        Persistence.service().persist(level4);
        // create categories for each level
        MaintenanceRequestCategory root = createMaintenanceCategory("ROOT", null);
        List<MaintenanceTreeImport> data = EntityCSVReciver.create(MaintenanceTreeImport.class).loadResourceFile(
                IOUtils.resourceFileName("maintenance-tree.csv", RefferenceDataPreloader.class));

        Map<String, MaintenanceRequestCategory> categories = new HashMap<String, MaintenanceRequestCategory>();
        for (MaintenanceTreeImport row : data) {
            // Find or create Element
            MaintenanceRequestCategory element = categories.get(row.type().getValue() + row.rooms().getValue());
            if (element == null) {
                element = createMaintenanceCategory(row.rooms().getValue(), level1);
                categories.put(row.type().getValue() + row.rooms().getValue(), element);
                root.subCategories().add(element);
            }
            // Find or create  Subject
            MaintenanceRequestCategory subject = null;
            for (MaintenanceRequestCategory subj : element.subCategories()) {
                if (subj.name().getValue().equals(row.repairSubject().getValue())) {
                    subject = subj;
                    break;
                }
            }
            if (subject == null) {
                subject = createMaintenanceCategory(row.repairSubject().getValue(), level2);
                subject.parent().set(element);
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
                detail = createMaintenanceCategory(row.subjectDetails().getValue(), level3);
                detail.parent().set(subject);
                subject.subCategories().add(detail);
            }
            // Create IssueClassification
            MaintenanceRequestCategory classification = createMaintenanceCategory(row.issue().getValue(), level4);
            classification.parent().set(detail);
            detail.subCategories().add(classification);
        }
        Persistence.service().persist(root);
    }

    private MaintenanceRequestCategory createMaintenanceCategory(String name, MaintenanceRequestCategoryLevel level) {
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        category.level().set(level);
        category.name().setValue(name);
        return category;
    }

    private MaintenanceRequestCategoryLevel createMaintenanceCategoryLevel(String name, int level) {
        MaintenanceRequestCategoryLevel catLevel = EntityFactory.create(MaintenanceRequestCategoryLevel.class);
        catLevel.name().setValue(name);
        catLevel.level().setValue(level);
        return catLevel;
    }
}
