/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 15, 2013
 * @author vlads
 */
package com.propertyvista.server.domain.security;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

@RpcTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(namespace = VistaNamespace.operationsNamespace)
public interface GlobalCrmUserIndex extends IEntity {

    @Indexed(group = { "f,1" })
    Pmc pmc();

    @Indexed(group = { "f,2" })
    IPrimitive<Key> crmUser();

    // Update with each CRM user update
    @Editor(type = EditorType.email)
    @NotNull
    @Length(64)
    @Indexed
    IPrimitive<String> email();

}
