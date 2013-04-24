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
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;

import com.yardi.entity.maintenance.ServiceRequest;
import com.yardi.entity.maintenance.ServiceRequests;
import com.yardi.entity.maintenance.meta.Categories;
import com.yardi.entity.maintenance.meta.Category;
import com.yardi.entity.maintenance.meta.Priorities;
import com.yardi.entity.maintenance.meta.Status;
import com.yardi.entity.maintenance.meta.Statuses;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;

public class MaintenanceRequestMapper {

    public MaintenanceRequestCategory map(Categories categories) {
        MaintenanceRequestCategory topCategory = EntityFactory.create(MaintenanceRequestCategory.class);

        if (categories != null) {
            for (Category yCategory : categories.getCategory()) {
                MaintenanceRequestCategory category = createCategory(yCategory.getName());
                category.subCategories().addAll(getSubCategories(yCategory.getSubCategory()));

                topCategory.subCategories().add(category);
            }
        }

        return topCategory;
    }

    public List<MaintenanceRequestStatus> map(Statuses statuses) {
        List<MaintenanceRequestStatus> result = new ArrayList<MaintenanceRequestStatus>();
        if (statuses != null) {
            for (Status status : statuses.getStatus()) {
                for (Object statusName : status.getContent()) {
                    MaintenanceRequestStatus mrStatus = EntityFactory.create(MaintenanceRequestStatus.class);
                    mrStatus.name().setValue(statusName.toString());
                    result.add(mrStatus);
                }
            }
        }
        return result;
    }

    public List<MaintenanceRequestPriority> map(Priorities priorities) {
        List<MaintenanceRequestPriority> result = new ArrayList<MaintenanceRequestPriority>();
        if (priorities != null) {
            for (String priorityName : priorities.getPriority()) {
                MaintenanceRequestPriority mrPriority = EntityFactory.create(MaintenanceRequestPriority.class);
                mrPriority.name().setValue(priorityName);
                result.add(mrPriority);
            }
        }
        return result;
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

    public List<MaintenanceRequest> map(ServiceRequests serviceRequests) {
        List<MaintenanceRequest> maintenanceRequests = new ArrayList<MaintenanceRequest>();
        if (serviceRequests != null) {
            for (ServiceRequest serviceRequest : serviceRequests.getServiceRequest()) {
                maintenanceRequests.add(map(serviceRequest));
            }
        }
        return maintenanceRequests;
    }

    public MaintenanceRequest map(ServiceRequest serviceRequest) {
        MaintenanceRequest req = EntityFactory.create(MaintenanceRequest.class);

        req.id().setValue(new Key(serviceRequest.getServiceRequestId()));
        req.leaseParticipant().lease().unit().building().propertyCode().setValue(serviceRequest.getPropertyCode());
        req.leaseParticipant().lease().unit().info().number().setValue(serviceRequest.getUnitCode());
        req.leaseParticipant().participantId().setValue(serviceRequest.getTenantCode());
        //?? serviceRequest.getVendorCode();
        //?? serviceRequest.getServiceRequestBriefDescription();
        req.description().setValue(serviceRequest.getServiceRequestFullDescription());

        //?? serviceRequest.getPriority();

        req.category().name().setValue(serviceRequest.getCategory());
        if (StringUtils.isNotEmpty(serviceRequest.getSubCategory())) {
            MaintenanceRequestCategory subCategory = EntityFactory.create(MaintenanceRequestCategory.class);
            subCategory.name().setValue(serviceRequest.getSubCategory());
            req.category().subCategories().add(subCategory);
        }

        req.permissionToEnter().setValue(serviceRequest.isHasPermissionToEnter());

        req.leaseParticipant().customer().person().name().firstName().setValue(serviceRequest.getRequestorName());

        if (serviceRequest.getRequestorPhoneNumber() != null) {
            req.leaseParticipant().customer().person().homePhone().setValue(String.valueOf(serviceRequest.getRequestorPhoneNumber()));
        }
        req.leaseParticipant().customer().person().email().setValue(serviceRequest.getRequestorEmail());

        if (serviceRequest.getServiceRequestDate() != null) {
            req.submitted().setValue(new LogicalDate(serviceRequest.getServiceRequestDate()));
        }
        if (serviceRequest.getUpdateDate() != null) {
            req.updated().setValue(new LogicalDate(serviceRequest.getUpdateDate().toGregorianCalendar().getTimeInMillis()));
        }

// TODO        req.status().setValue(getStatus(serviceRequest.getCurrentStatus()));

        return req;
    }

    private MaintenanceRequestPriority getPriority(String priority) {
// TODO
//        for (MaintenanceRequestPriority issuePriority : MaintenanceRequestPriority.values()) {
//            if (issuePriority.name().equalsIgnoreCase(priority)) {
//                return issuePriority;
//            }
//        }
//        return MaintenanceRequestPriority.STANDARD;
        return null;
    }

    private MaintenanceRequestStatus getStatus(String currentStatus) {
// TODO
//        for (MaintenanceRequestStatus status : MaintenanceRequestStatus.values()) {
//            if (status.name().equalsIgnoreCase(currentStatus)) {
//                return status;
//            }
//        }
        return null;
    }

    public ServiceRequest map(MaintenanceRequest maintenanceRequest) throws YardiServiceException {
        ServiceRequest req = new ServiceRequest();

        if (maintenanceRequest.id().getValue() != null) {
            req.setServiceRequestId((short) maintenanceRequest.id().getValue().asLong());
        }
        req.setPropertyCode(maintenanceRequest.leaseParticipant().lease().unit().building().propertyCode().getValue());
        req.setUnitCode(maintenanceRequest.leaseParticipant().lease().unit().info().number().getValue());
        req.setTenantCode(maintenanceRequest.leaseParticipant().participantId().getValue());
        req.setServiceRequestFullDescription(maintenanceRequest.description().getValue());

        //TODO how map to actual status from Yardi
        //req.setCurrentStatus(getStatus(maintenanceRequest.status().getValue()));
        //req.setPriority(value);

        req.setCategory(maintenanceRequest.category().name().getValue());
        if (!maintenanceRequest.category().subCategories().isEmpty()) {
            req.setSubCategory(maintenanceRequest.category().subCategories().get(0).name().getValue());
        }

        req.setHasPermissionToEnter(maintenanceRequest.permissionToEnter().getValue());
        req.setRequestorName(maintenanceRequest.leaseParticipant().customer().person().name().firstName().getValue());

        String homePhone = maintenanceRequest.leaseParticipant().customer().person().homePhone().getValue();
        if (StringUtils.isNotEmpty(homePhone) && StringUtils.isNumeric(homePhone)) {
            req.setRequestorPhoneNumber(Long.valueOf(homePhone));
        }
        req.setRequestorEmail(maintenanceRequest.leaseParticipant().customer().person().email().getValue());

        req.setServiceRequestDate(maintenanceRequest.submitted().getValue());
        try {
            if (maintenanceRequest.updated().getValue() != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(maintenanceRequest.updated().getValue());
                req.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            }
        } catch (DatatypeConfigurationException e) {
            throw new YardiServiceException("Errors during date -> dateTime type conversion");
        }

        return req;
    }

    public ServiceRequest mapStrict(MaintenanceRequest maintenanceRequest) {
        ServiceRequest req = new ServiceRequest();

        req.setServiceRequestId((short) maintenanceRequest.id().getValue().asLong());
        req.setPropertyCode(maintenanceRequest.leaseParticipant().lease().unit().building().propertyCode().getValue());

        return req;
    }
}
