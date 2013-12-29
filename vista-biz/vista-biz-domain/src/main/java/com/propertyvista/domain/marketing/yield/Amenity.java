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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@AbstractEntity
public interface Amenity extends IEntity {

    @Length(128)
    @ToString(index = 1)
    IPrimitive<String> name();

    @Length(2048)
    IPrimitive<String> description();
}
