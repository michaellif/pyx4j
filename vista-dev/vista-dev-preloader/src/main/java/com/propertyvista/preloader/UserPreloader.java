/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 */
package com.propertyvista.preloader;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.biz.preloader.UserPreloaderFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;

public class UserPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    @Override
    public String create() {
        int userCount = 0;

        CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();

        for (int i = 1; i <= config().maxPropertyManagers; i++) {
            String email = DemoData.UserType.PM.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue("Executive");
            emp.email().setValue(email);

            CrmUser crmUser = ServerSideFactory.create(UserPreloaderFacade.class).createCrmUser(emp.name().getStringView(), email, email, defaultRole);
            emp.user().set(crmUser);

            if (i == 1 || ApplicationMode.isDemo()) {
                EmployeeSignature signature = createEmployeeSignature(emp);
                if (signature != null) {
                    emp.signature().set(signature);
                } else {
                    log.warn("Could't create default signature for EMPLOYEE '{}'", emp.email().getValue());
                }
            }

            Persistence.service().persist(emp);

            userCount++;
        }

        for (int i = 1; i <= config().maxPropertyManagementEmployee; i++) {
            String email = DemoData.UserType.EMP.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
            emp.email().setValue(email);

            CrmUser crmUser = ServerSideFactory.create(UserPreloaderFacade.class).createCrmUser(emp.name().getStringView(), email, email, defaultRole);
            emp.user().set(crmUser);

            if (ApplicationMode.isDemo()) {
                EmployeeSignature signature = createEmployeeSignature(emp);
                if (signature != null) {
                    emp.signature().set(signature);
                } else {
                    log.warn("Could't create default signature for EMPLOYEE '{}'", emp.email().getValue());
                }
            }

            Persistence.service().persist(emp);
            userCount++;
        }

        for (int i = 1; i <= DemoData.UserType.OAPI.getDefaultMax(); i++) {
            String email = DemoData.UserType.OAPI.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
            emp.email().setValue(email);

            CrmUser crmUser = ServerSideFactory.create(UserPreloaderFacade.class).createCrmUser(emp.name().getStringView(), email, email,
                    CrmRolesPreloader.getOapiRole());
            emp.user().set(crmUser);

            if (ApplicationMode.isDemo()) {
                EmployeeSignature signature = createEmployeeSignature(emp);
                if (signature != null) {
                    emp.signature().set(signature);
                } else {
                    log.warn("Could't create default signature for EMPLOYEE '{}'", emp.email().getValue());
                }
            }

            Persistence.service().persist(emp);
            userCount++;
        }

        ServerSideFactory.create(UserPreloaderFacade.class).createVistaSupportUsers();

        return "Created " + userCount + " Employee/Users";
    }

    private EmployeeSignature createEmployeeSignature(Employee emp) {
        EmployeeSignature signature = null;

        String signatureFileName = getRandomSignatureFileName();
        byte bytes[] = getSignatureImageBytes(signatureFileName);

        if (bytes != null) {
            // Create Signature Blob
            EmployeeSignatureBlob blob = EntityFactory.create(EmployeeSignatureBlob.class);
            blob.contentType().setValue(MimeMap.getContentType(FilenameUtils.getExtension(signatureFileName)));
            blob.data().setValue(bytes);
            Persistence.service().persist(blob);

            // Create Employee Signature
            signature = EntityFactory.create(EmployeeSignature.class);
            signature.file().fileName().setValue(signatureFileName);
            signature.file().fileSize().setValue(bytes.length);
            signature.file().blobKey().setValue(blob.getPrimaryKey());
            signature.employee().set(emp);
        }

        return signature;
    }

    private byte[] getSignatureImageBytes(String signatureFileName) {
        boolean newData = false;
        byte bytes[] = CacheService.get(UserPreloader.class.getName() + signatureFileName);
        if (bytes == null) {
            try {
                bytes = IOUtils.getBinaryResource(signatureFileName, this.getClass());
                newData = true;
            } catch (IOException e) {
                log.error("Error preloading image '{}' for signature. ", signatureFileName, e);
            }
        }

        if (newData) {
            CacheService.put(UserPreloader.class.getName() + signatureFileName, bytes);
        }

        return bytes;
    }

    private String getRandomSignatureFileName() {
        int imageIndex = RandomUtil.nextInt(5, "signature", 4) + 1;
        return "signature-" + imageIndex + ".png";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(CrmUser.class, CrmUserCredential.class);
        } else {
            return "This is production";
        }
    }

}
