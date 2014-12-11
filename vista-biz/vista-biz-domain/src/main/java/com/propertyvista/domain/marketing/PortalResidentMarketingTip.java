/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.marketing;

import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface PortalResidentMarketingTip extends IEntity {

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @NotNull
    @ToString(index = 0)
    IPrimitive<PortalResidentMarketingTarget> target();

    @ToString
    IPrimitive<String> comments();

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(300000)
    IPrimitive<String> content();
}
