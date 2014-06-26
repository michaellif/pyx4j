/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.company;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("Portfolio")
public interface Portfolio extends IEntity, CommunicationEndpoint {

    @NotNull
    @ToString
    @Length(64)
    IPrimitive<String> name();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Detached(level = AttachLevel.IdOnly)
    IList<Building> buildings();
}
