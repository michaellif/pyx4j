/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.CustomerUser;

@Transient
@ToStringFormat("{0}, {1}")
public interface ApplicationUserDTO extends IEntity {

    @I18n(context = "Application User")
    @XmlType(name = "ApplicationUser")
    public enum ApplicationUser {

        Applicant, CoApplicant, Guarantor;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ToString(index = 1)
    IPrimitive<ApplicationUser> userType();

    @ToString(index = 0)
    Person person();

    CustomerUser user();
}
