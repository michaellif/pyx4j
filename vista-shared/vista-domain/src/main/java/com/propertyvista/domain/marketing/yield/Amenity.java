/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-22
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.domain.marketing.yield;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Amenity extends IEntity {

    @Length(25)
    @ToString(index = 1)
    IPrimitive<String> name();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();
}
