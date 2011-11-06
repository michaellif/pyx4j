/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Cached
public interface SitePalette extends IEntity {

    //TODO make single instance objects part of framework
    public final String cacheKey = "SitePalette";

    IPrimitive<Integer> object1();

    IPrimitive<Integer> object2();

    IPrimitive<Integer> contrast1();

    IPrimitive<Integer> contrast2();

    IPrimitive<Integer> background();

    IPrimitive<Integer> foreground();

}
