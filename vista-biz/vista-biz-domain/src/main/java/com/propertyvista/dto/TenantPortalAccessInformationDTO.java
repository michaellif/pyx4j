/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
@SecurityEnabled
public interface TenantPortalAccessInformationDTO extends IEntity {

    IPrimitive<String> leaseId();

    IPrimitive<String> unit();

    IPrimitive<String> address();

    IPrimitive<String> cityZip();

    IPrimitive<String> city();

    IPrimitive<String> province();

    IPrimitive<String> postalCode();

    IPrimitive<String> firstName();

    IPrimitive<String> middleName();

    IPrimitive<String> lastName();

    IPrimitive<String> tenantNameFull();

    IPrimitive<String> portalRegistrationBuiding();

    IPrimitive<String> portalRegistrationToken();

}
