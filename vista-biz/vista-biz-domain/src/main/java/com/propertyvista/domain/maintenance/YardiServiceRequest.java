/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface YardiServiceRequest extends IEntity {

    IPrimitive<Integer> requestId();

    IPrimitive<String> propertyCode();

    IPrimitive<String> unitCode();

    IPrimitive<String> tenantCode();

    IPrimitive<String> vendorCode();

    @Caption(name = "Request Title")
    IPrimitive<String> requestDescriptionBrief();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    @Caption(name = "Request Description")
    IPrimitive<String> requestDescriptionFull();

    IPrimitive<String> priority();

    IPrimitive<Boolean> permissionToEnter();

    IPrimitive<String> accessNotes();

    IPrimitive<String> problemDescription();

    IPrimitive<String> technicalNotes();

    IPrimitive<Boolean> tenantCaused();

    IPrimitive<String> requestorName();

    IPrimitive<String> requestorPhone();

    IPrimitive<String> requestorEmail();

    IPrimitive<String> authorizedBy();

    IPrimitive<String> currentStatus();

    IPrimitive<String> resolution();

}
