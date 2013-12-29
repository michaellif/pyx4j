/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-01-29
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.domain.legal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.shared.i18n.CompiledLocale;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface LegalDocument extends IEntity {
    @NotNull
    IPrimitive<CompiledLocale> locale();

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(300000)
    IPrimitive<String> content();
}
