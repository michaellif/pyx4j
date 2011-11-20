/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.IUserEntity;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.person.Person;

public interface Tenant extends IUserEntity {

    @I18n
    @XmlType(name = "TenantType")
    public enum Type {

        person,

        company;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @MemberColumn(name = "tenantType")
    IPrimitive<Type> type();

    @ToString(index = 0)
    @EmbeddedEntity
    Person person();

    @ToString(index = 1)
    @EmbeddedEntity
    Company company();

    @Owned
// TODO : commented because of strange behavior of with @Owned - entities duplicated on loading/saving...  
//    @Detached
    @Length(3)
    IList<EmergencyContact> emergencyContacts();
}
