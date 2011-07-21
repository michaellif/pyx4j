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
package com.propertyvista.common.domain.tenant;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.company.Company;
import com.propertyvista.common.domain.person.Person;
import com.propertyvista.portal.domain.ptapp.EmergencyContact;
import com.propertyvista.portal.domain.ptapp.Vehicle;

public interface Tenant extends IEntity {

    @Translatable
    public enum Type {

        person,

        company;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ReadOnly
    @Detached
    @MemberColumn(name = "user_id")
    User user();

    @ToString(index = 1)
    @MemberColumn(name = "tenantType")
    IPrimitive<Type> type();

    @ToString
    @EmbeddedEntity
    Person person();

    @EmbeddedEntity
    Company company();

    @Owned
    @Length(3)
    IList<Vehicle> vehicles();

    @Owned
    @Length(3)
    IList<EmergencyContact> emergencyContacts();
}
