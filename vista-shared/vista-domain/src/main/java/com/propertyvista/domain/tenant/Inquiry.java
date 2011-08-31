/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public interface Inquiry extends IEntity {

    @ToString(index = 0)
    Name name();

    IList<Phone> phones();

    @EmbeddedEntity
    Email email();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    Building building();

    AptUnit unit();
}
