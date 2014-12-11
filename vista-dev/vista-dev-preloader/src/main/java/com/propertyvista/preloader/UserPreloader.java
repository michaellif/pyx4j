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
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.generator.SecurityGenerator;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.shared.config.VistaDemo;

public class UserPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(UserPreloader.class);

    private final static String IMG_SIGNATURE = "signature.png";

    static CustomerUser createTenantUser(String name, String email, String password) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<CustomerUser> criteria = EntityQueryCriteria.create(CustomerUser.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<CustomerUser> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        CustomerUser user = EntityFactory.create(CustomerUser.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        CustomerUserCredential credential = EntityFactory.create(CustomerUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(email));
        credential.enabled().setValue(Boolean.TRUE);

        Persistence.service().persist(credential);

        return user;
    }

    public static CrmUser createCrmUser(String name, String email, String password, CrmRole... roles) {
        if (!ApplicationMode.isDevelopment()) {
            EntityQueryCriteria<CrmUser> criteria = EntityQueryCriteria.create(CrmUser.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
            List<CrmUser> users = Persistence.service().query(criteria);
            if (users.size() != 0) {
                log.debug("User already exists");
                return users.get(0);
            }
        }
        CrmUser user = EntityFactory.create(CrmUser.class);

        user.name().setValue(name);
        user.email().setValue(email);

        Persistence.service().persist(user);

        CrmUserCredential credential = EntityFactory.create(CrmUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(password));
        credential.enabled().setValue(Boolean.TRUE);
        credential.accessAllBuildings().setValue(Boolean.TRUE);
        for (CrmRole role : roles) {
            if (role != null) {
                credential.roles().add(role);
            }
        }

        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            SecurityGenerator.assignSecurityQuestion(credential);
        }

        Persistence.service().persist(credential);
        ServerSideFactory.create(UserManagementFacade.class).createGlobalCrmUserIndex(user);

        return user;
    }

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

            emp.user().set(createCrmUser(emp.name().getStringView(), email, email, defaultRole));

            if (i == 1) {
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

            emp.user().set(createCrmUser(emp.name().getStringView(), email, email, defaultRole));

            Persistence.service().persist(emp);
            userCount++;
        }

        for (int i = 1; i <= DemoData.UserType.OAPI.getDefaultMax(); i++) {
            String email = DemoData.UserType.OAPI.getEmail(i);

            Employee emp = CommonsGenerator.createEmployee().duplicate(Employee.class);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(emp);
            emp.title().setValue(CommonsGenerator.randomEmployeeTitle());
            emp.email().setValue(email);

            emp.user().set(createCrmUser(emp.name().getStringView(), email, email, CrmRolesPreloader.getOapiRole()));

            Persistence.service().persist(emp);
            userCount++;
        }

        //TODO
        //PmcCreator.createVistaSupportUsers();

        return "Created " + userCount + " Employee/Users";
    }

    private EmployeeSignature createEmployeeSignature(Employee emp) {

        EmployeeSignature signature = null;

        try {
            byte bytes[] = IOUtils.getBinaryResource(IMG_SIGNATURE, this.getClass());
            if (bytes != null) {
                // Create Signature Blob
                EmployeeSignatureBlob blob = EntityFactory.create(EmployeeSignatureBlob.class);
                blob.contentType().setValue(MimeMap.getContentType(FilenameUtils.getExtension(IMG_SIGNATURE)));
                blob.data().setValue(bytes);
                Persistence.service().persist(blob);

                // Create Employee Signature
                signature = EntityFactory.create(EmployeeSignature.class);
                signature.file().fileName().setValue(IMG_SIGNATURE);
                signature.file().fileSize().setValue(bytes.length);
                signature.file().blobKey().setValue(blob.getPrimaryKey());
                signature.employee().set(emp);
            }
        } catch (IOException e) {
            log.error("Error preloading image '{}' for signature. ", IMG_SIGNATURE, e);
        }

        return signature;

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
