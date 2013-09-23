/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.shared.IPrimitive;

@EmbeddedEntity
public interface MarketingContactPhone extends MarketingContact {
    @Override
    @Caption(name = "Phone")
    @Editor(type = EditorType.phone)
    IPrimitive<String> value();
}
