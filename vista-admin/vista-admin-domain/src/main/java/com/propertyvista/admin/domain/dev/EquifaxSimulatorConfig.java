/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.dev;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.adminNamespace)
public interface EquifaxSimulatorConfig extends IEntity {

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(300000)
    IPrimitive<String> approveXml();

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(300000)
    IPrimitive<String> declineXml();

    @Editor(type = Editor.EditorType.richtextarea)
    @Length(300000)
    IPrimitive<String> moreInfoXml();
}
