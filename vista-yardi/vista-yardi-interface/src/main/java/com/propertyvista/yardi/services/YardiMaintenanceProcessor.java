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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class YardiMaintenanceProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiMaintenanceProcessor.class);

    // Mapping for statuses from Yardi "Standard" set 
    private final static Map<String, MaintenanceRequestStatus.StatusPhase> yardiStatusMap;
    static {
        yardiStatusMap = new HashMap<String, MaintenanceRequestStatus.StatusPhase>();
        yardiStatusMap.put("Call", MaintenanceRequestStatus.StatusPhase.Submitted);
        yardiStatusMap.put("In Progress", MaintenanceRequestStatus.StatusPhase.Submitted);
        yardiStatusMap.put("Scheduled", MaintenanceRequestStatus.StatusPhase.Scheduled);
        yardiStatusMap.put("Work Completed", MaintenanceRequestStatus.StatusPhase.Resolved);
        yardiStatusMap.put("Canceled", MaintenanceRequestStatus.StatusPhase.Cancelled);
    }

    public ServiceRequest convertRequest(MaintenanceRequest mr) {
        ServiceRequest req = new ServiceRequest();

        if (!mr.requestId().isNull()) {
            try {
                req.setServiceRequestId(Short.valueOf(mr.requestId().getValue()));
            } catch (NumberFormatException ignore) {
            }
        }

        req.setPropertyCode(mr.building().propertyCode().getValue());
        if (mr.unit().getInstanceValueClass().equals(AptUnit.class)) {
            req.setUnitCode(mr.unit().<AptUnit> cast().info().number().getValue());
        }

        req.setRequestorName(mr.reporterName().getValue());
        req.setRequestorEmail(mr.reporterEmail().getValue());
        Long requestorPhone = null;
        try {
            requestorPhone = Long.valueOf(mr.reporterPhone().getValue().replaceAll("\\D", ""));
        } catch (Exception ignore) {

        }
        req.setRequestorPhoneNumber(requestorPhone);
// TODO - TenantCode sets BillTo property; enable when WorkOrder funtionality is implemented
//        req.setTenantCode(mr.billTo().participantId().getValue());
// TODO - find out the use of TenantCaused
//        req.setTenantCaused(!mr.reporter().isNull());

// TODO choose between FullDescription and DescriptionNotes
//        req.setServiceRequestFullDescription(mr.description().getValue());
        req.setProblemDescriptionNotes(mr.description().getValue());
        req.setServiceRequestBriefDescription(mr.summary().getValue());

        req.setHasPermissionToEnter(mr.permissionToEnter().getValue());
        req.setAccessNotes(mr.petInstructions().getValue());

        req.setCurrentStatus(mr.status().name().getValue());
        req.setPriority(mr.priority().name().getValue());

        Persistence.ensureRetrieve(mr.category(), AttachLevel.Attached);
        Persistence.ensureRetrieve(mr.category().parent(), AttachLevel.Attached);

        req.setSubCategory(mr.category().name().getValue());
        req.setCategory(mr.category().parent().name().getValue());

        req.setServiceRequestDate(mr.submitted().getValue());

        return req;
    }

    // we will update and reload meta from here if request categories, status, or priority do not exist
    public MaintenanceRequest mergeRequest(PmcYardiCredential yc, ServiceRequest request) throws YardiServiceException {
        final Key yardiInterfaceId = yc.getPrimaryKey();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.eq(criteria.proto().building().propertyCode(), request.getPropertyCode());
        criteria.eq(criteria.proto().building().integrationSystemId(), yardiInterfaceId);
        criteria.eq(criteria.proto().requestId(), request.getServiceRequestId().toString());
        MaintenanceRequest mr = Persistence.service().retrieve(criteria);
        if (mr == null) {
            mr = EntityFactory.create(MaintenanceRequest.class);
        }
        return updateRequest(yc, mr, request);
    }

    public MaintenanceRequest updateRequest(PmcYardiCredential yc, MaintenanceRequest mr, ServiceRequest request) throws YardiServiceException {
        boolean metaReloaded = false;
        // find building - propertyCode field is mandatory
        final Key yardiInterfaceId = yc.getPrimaryKey();
        Building building = null;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().propertyCode(), request.getPropertyCode());
            criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);
            building = Persistence.service().retrieve(criteria);
            if (building == null) {
                throw new YardiServiceException("Request dropped - Building not found: " + request.getPropertyCode());
            } else {
                mr.building().set(building);
            }
        }
        MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(building);
        // unit
        if (request.getUnitCode() != null) {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.eq(criteria.proto().building(), mr.building());
            criteria.eq(criteria.proto().info().number(), request.getUnitCode());
            AptUnit unit = Persistence.service().retrieve(criteria);
            if (unit == null) {
                throw new YardiServiceException("Request dropped - Unit not found: " + request.getUnitCode());
            } else {
                mr.unit().set(unit);
            }
        } else {
            mr.unit().setValue(null);
        }
        // find tenant
        // TODO TenantCode is set by Yardi BillTo field and should be matched to Vista tenant once WorkOrder is implemented
//        if (request.getTenantCode() != null) {
//            EntityQueryCriteria<Tenant> crit = EntityQueryCriteria.create(Tenant.class);
//            crit.add(PropertyCriterion.eq(crit.proto().participantId(), request.getTenantCode()));
//            crit.add(PropertyCriterion.eq(crit.proto().lease().unit().building().propertyCode(), request.getPropertyCode()));
//            Tenant tenant = Persistence.service().retrieve(crit);
//            if (tenant == null) {
//                throw new YardiServiceException("Request dropped - Tenant not found: " + request.getTenantCode());
//                return null;
//            } else {
//                mr.reporter().set(tenant);
//            }
//        }
        // category
        if (request.getCategory() != null) {
            MaintenanceRequestCategory category = findCategory(request.getCategory(), meta.rootCategory());
            if (category == null) {
                metaReloaded = reloadMeta(yc);
                category = findCategory(request.getCategory(), meta.rootCategory());
                if (category == null) {
                    throw new YardiServiceException("Request dropped - Category not found: " + request.getCategory());
                }
            }
            MaintenanceRequestCategory subcat = findCategory(request.getSubCategory(), category);
            if (subcat == null && !metaReloaded) {
                metaReloaded = reloadMeta(yc);
                subcat = findCategory(request.getSubCategory(), category);
                if (subcat == null) {
                    throw new YardiServiceException("SubCategory not found: " + request.getSubCategory());
                }
            }
            mr.category().set(subcat);
        } else {
            mr.category().setValue(null);
        }
        // status
        if (request.getCurrentStatus() != null) {
            MaintenanceRequestStatus stat = findStatus(request.getCurrentStatus(), meta);
            if (stat == null && !metaReloaded) {
                metaReloaded = reloadMeta(yc);
                stat = findStatus(request.getCurrentStatus(), meta);
                if (stat == null) {
                    throw new YardiServiceException("Request dropped - Status not found: " + request.getCurrentStatus());
                }
            }
            mr.status().set(stat);
        } else {
            mr.status().set(null);
        }
        // priority
        if (request.getPriority() != null) {
            MaintenanceRequestPriority pr = findPriority(request.getPriority(), meta);
            if (pr == null && !metaReloaded) {
                metaReloaded = reloadMeta(yc);
                pr = findPriority(request.getPriority(), meta);
                if (pr == null) {
                    throw new YardiServiceException("Request dropped - Priority not found: " + request.getPriority());
                }
            }
            mr.priority().set(pr);
        } else {
            mr.priority().set(null);
        }
        // other data
        mr.requestId().setValue(request.getServiceRequestId().toString());
        if (mr.reporter().isNull()) {
            mr.reporterName().setValue(request.getRequestorName());
            mr.reporterPhone().setValue(request.getRequestorPhoneNumber() == null ? null : request.getRequestorPhoneNumber().toString());
            mr.reporterEmail().setValue(request.getRequestorEmail());
        }
        mr.summary().setValue(request.getServiceRequestBriefDescription());
// TODO choose between FullDescription and DescriptionNotes
//        mr.description().setValue(request.getServiceRequestFullDescription());
        mr.description().setValue(request.getProblemDescriptionNotes());
        mr.permissionToEnter().setValue(request.isHasPermissionToEnter());
        mr.petInstructions().setValue(request.getAccessNotes());
        if (mr.submitted().isNull()) {
            mr.submitted().setValue(request.getServiceRequestDate());
        }
        mr.updated().setValue(request.getUpdateDate());

        return mr;
    }

    public List<MaintenanceRequestStatus> mergeStatuses(Statuses statuses, MaintenanceRequestMetadata meta) {
        if (statuses == null) {
            return null;
        }
        List<MaintenanceRequestStatus> oldStatuses = meta.statuses();
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
            EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
            criteria.eq(criteria.proto().status(), stat);
            if (Persistence.service().count(criteria) == 0) {
                oldStatuses.remove(stat);
                Persistence.service().delete(stat);
            }
        }
        return oldStatuses;
    }

    public List<MaintenanceRequestPriority> mergePriorities(Priorities priorities, MaintenanceRequestMetadata meta) {
        if (priorities == null) {
            return null;
        }
        List<MaintenanceRequestPriority> oldPriorities = meta.priorities();
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
            EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
            criteria.eq(criteria.proto().priority(), stat);
            if (Persistence.service().count(criteria) == 0) {
                oldPriorities.remove(stat);
                Persistence.service().delete(stat);
            }
        }
        return oldPriorities;
    }

    public MaintenanceRequestCategory mergeCategories(Categories categories, MaintenanceRequestMetadata meta) {
        mergeCategoriesRecursive(meta.rootCategory(), categories.getCategory(), meta.rootCategory());
        return meta.rootCategory();
    }

    private void mergeCategoriesRecursive(MaintenanceRequestCategory oldParent, List<?> newList, MaintenanceRequestCategory root) {
        if (oldParent.subCategories().getAttachLevel() != AttachLevel.Attached) {
            Persistence.service().retrieveMember(oldParent.subCategories());
        }
        List<MaintenanceRequestCategory> toBeRemoved = new ArrayList<MaintenanceRequestCategory>();
        Map<String, MaintenanceRequestCategory> oldMap = new HashMap<String, MaintenanceRequestCategory>();
        if (!oldParent.subCategories().isNull()) {
            toBeRemoved.addAll(oldParent.subCategories());
            for (MaintenanceRequestCategory oldCat : oldParent.subCategories()) {
                oldMap.put(oldCat.name().getValue(), oldCat);
            }
        }
        Set<String> oldNames = oldMap.keySet();
        for (Object newCat : newList) {
            String newName = newCat == null ? null : (newCat instanceof Category ? ((Category) newCat).getName() : newCat.toString());

            MaintenanceRequestCategory category;
            if (oldNames.contains(newName)) {
                category = oldMap.get(newName);
                toBeRemoved.remove(category);
            } else {
                log.debug("new category: {} -> {}", oldParent.name().getValue(), newName);
                category = createCategory(newName, root);
                oldParent.subCategories().add(category);
            }
            if (newCat instanceof Category) {
                List<?> subCategories = ((Category) newCat).getSubCategory();
                if (subCategories == null || subCategories.size() == 0) {
                    // our parent category must have a single null-named sub-category
                    subCategories = Arrays.asList(new String[] { null });
                }
                mergeCategoriesRecursive(category, subCategories, root);
            }
        }
        for (MaintenanceRequestCategory cat : toBeRemoved) {
            // make sure we don't have any associated requests
            EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
            criteria.eq(criteria.proto().category(), cat);
            if (Persistence.service().count(criteria) == 0) {
                oldParent.subCategories().remove(cat);
                Persistence.service().delete(cat);
            }
        }
    }

    private MaintenanceRequestCategory createCategory(String name, MaintenanceRequestCategory root) {
        MaintenanceRequestCategory category = EntityFactory.create(MaintenanceRequestCategory.class);
        category.name().setValue(name);
        category.root().set(root);
        return category;
    }

    private MaintenanceRequestStatus createStatus(String name) {
        MaintenanceRequestStatus status = EntityFactory.create(MaintenanceRequestStatus.class);
        status.name().setValue(name);
        status.phase().setValue(yardiStatusMap.get(name));
        return status;
    }

    private MaintenanceRequestPriority createPriority(String name) {
        MaintenanceRequestPriority priority = EntityFactory.create(MaintenanceRequestPriority.class);
        priority.name().setValue(name);
        return priority;
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
        } catch (RemoteException e) {
            log.warn("Could not reload service metadata");
            return false;
        }
    }

    private MaintenanceRequestCategory findCategory(String name, MaintenanceRequestCategory parent) {
        // name could be null
        for (MaintenanceRequestCategory cat : parent.subCategories()) {
            if ((name == null && cat.name().isNull()) || (name != null && name.equals(cat.name().getValue()))) {
                return cat;
            }
        }
        return null;
    }

    private MaintenanceRequestStatus findStatus(String name, MaintenanceRequestMetadata meta) {
        if (name == null) {
            return null;
        }
        for (MaintenanceRequestStatus stat : meta.statuses()) {
            if (name.equals(stat.name().getValue())) {
                return stat;
            }
        }
        return null;
    }

    private MaintenanceRequestPriority findPriority(String name, MaintenanceRequestMetadata meta) {
        if (name == null) {
            return null;
        }
        for (MaintenanceRequestPriority pr : meta.priorities()) {
            if (name.equals(pr.name().getValue())) {
                return pr;
            }
        }
        return null;
    }
}
