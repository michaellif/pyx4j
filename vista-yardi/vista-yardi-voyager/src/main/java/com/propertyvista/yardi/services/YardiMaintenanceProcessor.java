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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;
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
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Tenant;

public class YardiMaintenanceProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiMaintenanceProcessor.class);

    public ServiceRequest convertRequest(MaintenanceRequest mr) {
        Persistence.ensureRetrieve(mr.reporter().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(mr.reporter().lease().unit().building(), AttachLevel.Attached);

        ServiceRequest req = new ServiceRequest();

        if (!mr.requestId().isNull()) {
            try {
                req.setServiceRequestId(Short.valueOf(mr.requestId().getValue()));
            } catch (NumberFormatException ignore) {
            }
        }

        req.setPropertyCode(mr.reporter().lease().unit().building().propertyCode().getValue());
        req.setUnitCode(mr.reporter().lease().unit().info().number().getValue());
        req.setTenantCode(mr.reporter().participantId().getValue());
        req.setTenantCaused(true);
        req.setServiceRequestFullDescription(mr.description().getValue());
        req.setServiceRequestBriefDescription(mr.summary().getValue());

        req.setHasPermissionToEnter(mr.permissionToEnter().getValue());
        req.setAccessNotes(mr.petInstructions().getValue());

        req.setCurrentStatus(mr.status().name().getValue());
        req.setPriority(mr.priority().name().getValue());

        Persistence.ensureRetrieve(mr.category(), AttachLevel.Attached);
        Persistence.ensureRetrieve(mr.category().parent(), AttachLevel.Attached);

        req.setSubCategory(mr.category().name().getValue());
        req.setCategory(mr.category().parent().name().getValue());

        req.setRequestorName(mr.reporter().customer().person().name().firstName().getValue());
        req.setRequestorEmail(mr.reporter().customer().person().email().getValue());
        // TODO - extract numbers, then convert to long
        String homePhone = mr.reporter().customer().person().homePhone().getValue();
        if (StringUtils.isNotEmpty(homePhone) && StringUtils.isNumeric(homePhone)) {
            req.setRequestorPhoneNumber(Long.valueOf(homePhone));
        }

        req.setServiceRequestDate(mr.submitted().getValue());

        if (mr.updated().getValue() != null) {
            try {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(mr.updated().getValue());
                req.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            } catch (DatatypeConfigurationException ignore) {
            }
        }

        return req;
    }

    public void updateRequest(PmcYardiCredential yc, MaintenanceRequest mr, ServiceRequest sr) {
        updateRequest(mr, createRequest(yc, sr));
    }

    // we will need to update and reload meta from here if request categories, status, or priority do not exist
    public MaintenanceRequest mergeRequest(PmcYardiCredential yc, ServiceRequest request) {
        EntityQueryCriteria<MaintenanceRequest> crit = EntityQueryCriteria.create(MaintenanceRequest.class);
        crit.add(PropertyCriterion.eq(crit.proto().requestId(), request.getServiceRequestId().toString()));
        MaintenanceRequest mr = Persistence.service().retrieve(crit);
        if (mr == null) {
            mr = createRequest(yc, request);
        } else {
            updateRequest(yc, mr, request);
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
        Persistence.service().persist(oldRoot);
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
                log.debug("new category: {} -> {}", oldParent.name().getValue(), newName);
                category = createCategory(newName);
                category.parent().set(oldParent);
                oldParent.subCategories().add(category);
            }
            if (newCat instanceof Category) {
                List<?> subCategories = ((Category) newCat).getSubCategory();
                if (subCategories == null || subCategories.size() == 0) {
                    MaintenanceRequestCategory empty = createCategory(null);
                    empty.parent().set(category);
//                    category.subCategories().add(empty);
                } else {
                    mergeCategoriesRecursive(category, subCategories);
                }
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

    private MaintenanceRequest createRequest(PmcYardiCredential yc, ServiceRequest request) {
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
                log.warn("Request dropped - Tenant not found: {}", request.getTenantCode());
                return null;
            } else {
                mr.reporter().set(tenant);
            }
        } else {
            // TODO - remove tenant ownership for MaintenanceRequest domain object
            log.warn("Request dropped - Tenant is null");
            return null;
        }
        // category
        if (request.getCategory() != null) {
            MaintenanceRequestCategory category = findCategory(request.getCategory(), null);
            if (category == null) {
                metaReloaded = reloadMeta(yc);
                category = findCategory(request.getCategory(), null);
                if (category == null) {
                    log.warn("Request dropped - Category not found: {}", request.getCategory());
                    return null;
                }
            }
            MaintenanceRequestCategory subcat = findCategory(request.getSubCategory(), category);
            if (subcat == null && !metaReloaded) {
                metaReloaded = reloadMeta(yc);
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
                metaReloaded = reloadMeta(yc);
                stat = findStatus(request.getCurrentStatus());
                if (stat == null) {
                    log.warn("Request dropped - Status not found: {}", request.getCurrentStatus());
                    return null;
                }
            }
            mr.status().set(stat);
        }
        // priority
        if (request.getPriority() != null) {
            MaintenanceRequestPriority pr = findPriority(request.getPriority());
            if (pr == null && !metaReloaded) {
                metaReloaded = reloadMeta(yc);
                pr = findPriority(request.getPriority());
                if (pr == null) {
                    log.warn("Request dropped - Priority not found: {}", request.getPriority());
                    return null;
                }
            }
            mr.priority().set(pr);
        }
        // other data
        mr.requestId().setValue(request.getServiceRequestId().toString());
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
        mr.requestId().set(newData.requestId());
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

    private boolean reloadMeta(PmcYardiCredential yc) {
        try {
            YardiMaintenanceRequestsService.getInstance().loadMaintenanceRequestMeta(yc);
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
