/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.company;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.company.Employee;

@Transient
public interface EmployeeDTO extends Employee {

    IPrimitive<Boolean> enabled();

    @Caption(name = "Role")
    IPrimitive<VistaBehavior> behavior();

    /* password is used for new entity creation only */
    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Password")
    IPrimitive<String> password();

    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Confirm password")
    IPrimitive<String> passwordConfirm();

}
