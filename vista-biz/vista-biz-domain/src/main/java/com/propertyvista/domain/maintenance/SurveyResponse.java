/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface SurveyResponse extends IEntity {

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    IPrimitive<Integer> rating();
}
