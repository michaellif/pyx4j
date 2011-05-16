/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface PasswordChangeRequest extends IEntity {

    IPrimitive<String> token();

    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Current password")
    IPrimitive<String> currentPassword();

    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "New password")
    IPrimitive<String> newPassword();

    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Confirm new password")
    IPrimitive<String> newPassword2();

}
