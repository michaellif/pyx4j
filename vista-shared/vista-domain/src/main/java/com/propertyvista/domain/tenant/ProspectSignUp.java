/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface ProspectSignUp extends IEntity {

    @NotNull
    IPrimitive<String> firstName();

    IPrimitive<String> middleName();

    @NotNull
    IPrimitive<String> lastName();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @Editor(type = EditorType.password)
    @NotNull
    @LogTransient
    IPrimitive<String> password();

    IPrimitive<String> ilsBuildingId();

    IPrimitive<String> ilsFloorplanId();

    IPrimitive<String> ilsUnitId();

}
