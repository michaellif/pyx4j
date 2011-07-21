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

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.common.domain.person.Person;
import com.propertyvista.common.domain.tenant.TenantInLeaseFragment;

@Transient
public interface TenantEditorDTO extends IEntity, TenantInLeaseFragment {

    @EmbeddedEntity
    Person person();

    public static enum ChangeStatus {

        New,

        Updated;

    }

    IPrimitive<ChangeStatus> changeStatus();

}
