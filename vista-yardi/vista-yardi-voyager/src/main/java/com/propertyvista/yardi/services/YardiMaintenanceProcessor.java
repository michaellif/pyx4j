/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.maintenance.ServiceRequest;
import com.yardi.entity.maintenance.meta.Categories;
import com.yardi.entity.maintenance.meta.Category;
import com.yardi.entity.maintenance.meta.Priorities;
import com.yardi.entity.maintenance.meta.Status;
import com.yardi.entity.maintenance.meta.Statuses;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;

public class YardiMaintenanceProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiMaintenanceProcessor.class);

    public String getProprtyList() {
        StringBuilder sb = new StringBuilder();
        for (Building property : Persistence.service().query(EntityQueryCriteria.create(Building.class))) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(property.propertyCode().getValue());
        }
        return sb.toString();
    }

    // we will need to update and reload meta from here if request categories, status, or priority do not exist
    public MaintenanceRequest mergeRequest(ServiceRequest request) {
        EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
        crit.add(PropertyCriterion.eq(crit.proto().requestId(), request.getServiceRequestId().toString()));
        MaintenanceRequest mr = Persistence.service().retrieve(crit);
        if (mr == null) {
            mr = createRequest(request);
        } else {
            updateRequest(mr, createRequest(request));
        }
        return mr;
    }

    public List<MaintenanceRequestStatus> mergeStatuses(Statuses statuses) {
        if (statuses == null) {
            return null;
        }
        List<MaintenanceRequestStatus> oldStatuses = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).statuses();
        Map<String, MaintenanceRequestStatus> oldMap = new HashMap<String, MaintenanceRequestStatus>();
        List<MaintenanceRequestStatus> toBeRemoved = new ArrayList<MaintenanceRequestStatus>(oldStatuses);
        for (MaintenanceRequestStatus status : oldStatuses) {
            oldMap.put(status.name().getValue(), status);
        }
        Set<String> oldNames = oldMap.keySet();
        for (Status status : statuses.getStatus()) {
            for (String newName : getStatusesRecursive(status)) {
                if (oldNames.contains(newName)) {
                    toBeRemoved.remove(oldMap.get(newName));
                } else {
                    oldStatuses.add(createStatus(newName));
                }
            }
        }
        for (MaintenanceRequestStatus stat : toBeRemoved) {
            // make sure we don't have any associated requests
            EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
            crit.add(PropertyCriterion.eq(crit.proto().status(), stat));
            if (Persistence.service().count(crit) == 0) {
                oldStatuses.remove(stat);
                Persistence.service().delete(stat);
            }
        }
        return oldStatuses;
    }

    public List<MaintenanceRequestPriority> mergePriorities(Priorities priorities) {
        if (priorities == null) {
            return null;
        }
        List<MaintenanceRequestPriority> oldPriorities = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).priorities();
        Map<String, MaintenanceRequestPriority> oldMap = new HashMap<String, MaintenanceRequestPriority>();
        List<MaintenanceRequestPriority> toBeRemoved = new ArrayList<MaintenanceRequestPriority>(oldPriorities);
        for (MaintenanceRequestPriority priority : oldPriorities) {
            oldMap.put(priority.name().getValue(), priority);
        }
        Set<String> oldNames = oldMap.keySet();
        for (String priority : priorities.getPriority()) {
            if (oldNames.contains(priority)) {
                toBeRemoved.remove(oldMap.get(priority));
            } else {
                oldPriorities.add(createPriority(priority));
            }
        }
        for (MaintenanceRequestPriority stat : toBeRemoved) {
            // make sure we don't have any associated requests
            EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
            crit.add(PropertyCriterion.eq(crit.proto().priority(), stat));
            if (Persistence.service().count(crit) == 0) {
                oldPriorities.remove(stat);
                Persistence.service().delete(stat);
            }
        }
        return oldPriorities;
    }

    public MaintenanceRequestCategory mergeCategories(Categories categories) {
        MaintenanceRequestCategory oldRoot = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).rootCategory();
        mergeCategoriesRecursive(oldRoot, categories.getCategory());
        return oldRoot;
    }

    private void mergeCategoriesRecursive(MaintenanceRequestCategory oldParent, List<?> newList) {
        List<MaintenanceRequestCategory> toBeRemoved = new ArrayList<MaintenanceRequestCategory>();
        Map<String, MaintenanceRequestCategory> oldMap = new HashMap<String, MaintenanceRequestCategory>();
        if (oldParent.subCategories() != null) {
            Persistence.service().retrieveMember(oldParent.subCategories());
            toBeRemoved.addAll(oldParent.subCategories());
            for (MaintenanceRequestCategory oldCat : oldParent.subCategories()) {
                oldMap.put(oldCat.name().getValue(), oldCat);
            }
        }
        Set<String> oldNames = oldMap.keySet();
        for (Object newCat : newList) {
            String newName = newCat instanceof Category ? ((Category) newCat).getName() : newCat.toString();
            MaintenanceRequestCategory category;
            if (oldNames.contains(newName)) {
                category = oldMap.get(newName);
                toBeRemoved.remove(category);
            } else {
                category = createCategory(newName);
                category.parent().set(oldParent);
                oldParent.subCategories().add(category);
            }
            if (newCat instanceof Category) {
                mergeCategoriesRecursive(category, ((Category) newCat).getSubCategory());
            }
        }
        for (MaintenanceRequestCategory cat : toBeRemoved) {
            // make sure we don't have any associated requests
            EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
            crit.add(PropertyCriterion.eq(crit.proto().category(), cat));
            if (Persistence.service().count(crit) == 0) {
                oldParent.subCategories().remove(cat);
                Persistence.service().delete(cat);
            }
        }
    }

    private MaintenanceRequestCategory createCategory(String name) {
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        category.name().setValue(name);
        return category;
    }

    private MaintenanceRequestStatus createStatus(String name) {
        MaintenanceRequestStatus status = EntityFactory.create(MaintenanceRequestStatus.class);
        status.name().setValue(name);
        return status;
    }

    private MaintenanceRequestPriority createPriority(String name) {
        MaintenanceRequestPriority priority = EntityFactory.create(MaintenanceRequestPriority.class);
        priority.name().setValue(name);
        return priority;
    }

    private MaintenanceRequest createRequest(ServiceRequest request) {
        // TODO
        MaintenanceRequest mr = EntityFactory.create(MaintenanceRequest.class);
        boolean metaReloaded = false;
        // find tenant first (if ticket is TenantCaused)
        if (request.getTenantCode() != null) {
            EntityQueryCriteria<Tenant> crit = EntityQueryCriteria.create(Tenant.class);
            crit.add(PropertyCriterion.eq(crit.proto().participantId(), request.getTenantCode()));
            crit.add(PropertyCriterion.eq(crit.proto().lease().unit().info().number(), request.getUnitCode()));
            crit.add(PropertyCriterion.eq(crit.proto().lease().unit().building().propertyCode(), request.getPropertyCode()));
            Tenant tenant = Persistence.service().retrieve(crit);
            if (tenant == null) {
                log.warn("Tenant not found: {}", request.getTenantCode());
                return null;
            } else {
                mr.leaseParticipant().set(tenant);
            }
        } else {
            return null;
        }
        // category
        if (request.getCategory() != null) {
            MaintenanceRequestCategory category = findCategory(request.getCategory(), null);
            if (category == null) {
                metaReloaded = reloadMeta();
                category = findCategory(request.getCategory(), null);
                if (category == null) {
                    log.warn("Category not found: {}", request.getCategory());
                    return null;
                }
            }
            MaintenanceRequestCategory subcat = findCategory(request.getSubCategory(), category);
            if (subcat == null && !metaReloaded) {
                metaReloaded = reloadMeta();
                subcat = findCategory(request.getSubCategory(), category);
                if (subcat == null) {
                    log.warn("SubCategory not found: {}", request.getSubCategory());
                    return null;
                }
            }
            mr.category().set(subcat);
        }
        // status
        {
            MaintenanceRequestStatus stat = findStatus(request.getCurrentStatus());
            if (stat == null && !metaReloaded) {
                metaReloaded = reloadMeta();
                stat = findStatus(request.getCurrentStatus());
                if (stat == null) {
                    log.warn("Status not found: {}", request.getCurrentStatus());
                    return null;
                }
            }
            mr.status().set(stat);
        }
        // priority
        {
            MaintenanceRequestPriority pr = findPriority(request.getPriority());
            if (pr == null && !metaReloaded) {
                metaReloaded = reloadMeta();
                pr = findPriority(request.getPriority());
                if (pr == null) {
                    log.warn("Priority not found: {}", request.getPriority());
                    return null;
                }
            }
            mr.priority().set(pr);
        }
        // other data
        mr.summary().setValue(request.getServiceRequestBriefDescription());
        mr.description().setValue(request.getServiceRequestFullDescription());
        mr.permissionToEnter().setValue(request.isHasPermissionToEnter());
        mr.petInstructions().setValue(request.getAccessNotes());
        mr.submitted().setValue(new LogicalDate(request.getServiceRequestDate()));
        mr.updated().setValue(new LogicalDate(request.getUpdateDate().toGregorianCalendar().getTimeInMillis()));

        return mr;
    }

    private void updateRequest(MaintenanceRequest mr, MaintenanceRequest newData) {
        // TODO
        mr.category().set(newData.category());
        mr.priority().set(newData.priority());
        mr.status().set(newData.status());
        mr.updated().set(newData.updated());
        mr.scheduledDate().set(newData.scheduledDate());
        mr.scheduledTime().set(newData.scheduledTime());
        mr.description().set(newData.description());
        mr.summary().set(newData.summary());
        mr.permissionToEnter().set(newData.permissionToEnter());
        mr.petInstructions().set(newData.petInstructions());
    }

    private List<String> getStatusesRecursive(Status status) {
        List<String> result = new ArrayList<String>();
        for (Object cont : status.getContent()) {
            if (cont instanceof Status) {
                result.addAll(getStatusesRecursive((Status) cont));
            } else {
                result.add(cont.toString());
            }
        }
        return result;
    }

    private boolean reloadMeta() {
        try {
            YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequestMeta();
            return true;
        } catch (YardiServiceException e) {
            log.warn("Could not reload service metadata");
            return false;
        }
    }

    private MaintenanceRequestCategory findCategory(String name, MaintenanceRequestCategory parent) {
        if (name == null) {
            return null;
        }
        if (parent == null) {
            parent = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).rootCategory();
        }
        for (MaintenanceRequestCategory cat : parent.subCategories()) {
            if (name.equals(cat.name().getValue())) {
                return cat;
            }
        }
        return null;
    }

    private MaintenanceRequestStatus findStatus(String name) {
        if (name == null) {
            return null;
        }
        for (MaintenanceRequestStatus stat : ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).statuses()) {
            if (name.equals(stat.name().getValue())) {
                return stat;
            }
        }
        return null;
    }

    private MaintenanceRequestPriority findPriority(String name) {
        if (name == null) {
            return null;
        }
        for (MaintenanceRequestPriority pr : ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(false).priorities()) {
            if (name.equals(pr.name().getValue())) {
                return pr;
            }
        }
        return null;
    }
}
