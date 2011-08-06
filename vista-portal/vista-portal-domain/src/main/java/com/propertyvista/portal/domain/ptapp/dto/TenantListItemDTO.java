/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.ptapp.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.TenantInLease;

@Transient
public interface TenantListItemDTO extends IEntity {

    @EmbeddedEntity
    Person person();

    public static enum ChangeStatus {

        New,

        Updated;

    }

    IPrimitive<ChangeStatus> changeStatus();

    @ToString(index = 0)
    @NotNull
    IPrimitive<TenantInLease.Relationship> relationship();

    @ToString(index = 1)
    @NotNull
    IPrimitive<TenantInLease.Status> status();

    //TODO add appropriate description
    @Caption(name = "Take Ownership", description = "Take Ownership of application filling means ...")
    IPrimitive<Boolean> takeOwnership();

}
