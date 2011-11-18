/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
@Caption(name = "Property Management Company (PMC)")
public interface PmcDTO extends IEntity {

    @NotNull
    @Caption(name = "Company name")
    IPrimitive<String> name();

    @NotNull
    @Caption(name = "DNS name")
    IPrimitive<String> dnsName();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @NotNull
    @Editor(type = EditorType.password)
    IPrimitive<String> password();
}
