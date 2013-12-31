/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.PublicVisibilityType;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PropertyPhoneIO extends IEntity {

    IPrimitive<String> type();

    IPrimitive<String> designation();

    IPrimitive<String> name();

    IPrimitive<String> description();

    @Editor(type = EditorType.phone)
    IPrimitive<String> number();

    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    IPrimitive<PublicVisibilityType> visibility();

}
