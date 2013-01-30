/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.security.rpc.AuthenticationRequest;

@Transient
public interface SelfRegistrationDTO extends AuthenticationRequest {

    //building  ComboBox
    @NotNull
    SelfRegistrationBuildingDTO building();

    @NotNull
    IPrimitive<String> firstName();

    @NotNull
    IPrimitive<String> lastName();

    @NotNull
    IPrimitive<String> secuirtyCode();

    // email and password inherited

    @NotNull
    @RpcTransient
    @Transient
    @Editor(type = EditorType.password)
    IPrimitive<String> passwordConfirm();
}
