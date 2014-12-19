/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 3, 2014
 * @author michaellif
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;

@Transient
public interface EmployeeFeedback extends IEntity {

    enum Experience {
        like, dislike, nutral
    }

    enum Device {
        androidMobile, androidTablet, appleMobile, appleTablet, desctop
    }

    IPrimitive<Experience> experience();

    IPrimitive<String> like();

    IPrimitive<String> dislike();

    IPrimitive<Device> device();

    @Owner
    Employee employee();

}
