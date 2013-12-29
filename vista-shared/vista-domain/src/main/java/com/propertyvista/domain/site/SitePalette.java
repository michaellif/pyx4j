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

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface SitePalette extends IEntity {

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> object1();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> object2();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> contrast1();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> contrast2();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> background();

    @Editor(type = EditorType.hue)
    IPrimitive<Integer> foreground();

}
