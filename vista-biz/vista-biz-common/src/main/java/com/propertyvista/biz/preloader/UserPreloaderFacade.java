/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 9, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

public interface UserPreloaderFacade {

    public void createVistaSupportUsers();

    public CrmUser createCrmEmployee(String firstName, String lastName, String email, String password, Key onboardingUserKey, CrmRole... roles);

    public void assignSecurityQuestion(AbstractUserCredential<?> credential);

    public CrmUser createCrmUser(String name, String email, String password, CrmRole... roles);

    public CustomerUser createTenantUser(String name, String email, String password);

}
