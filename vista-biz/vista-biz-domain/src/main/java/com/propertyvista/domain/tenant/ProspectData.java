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
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface ProspectData extends IEntity {

    @NotNull
    IPrimitive<String> firstName();

    IPrimitive<String> middleName();

    @NotNull
    IPrimitive<String> lastName();

    @NotNull
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @NotNull
    @LogTransient
    @Editor(type = EditorType.password)
    IPrimitive<String> password();

    // new Application lease data:

    IPrimitive<String> ilsBuildingId();

    IPrimitive<String> ilsFloorplanId();

    IPrimitive<String> ilsUnitId();
}
