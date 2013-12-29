/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface PageCaption extends IEntity {

    @NotNull
    AvailableLocale locale();

    @NotNull
    IPrimitive<String> caption();

    /**
     * Caption that represents parent page in secondary navigation menu
     */
    @NotNull
    IPrimitive<String> secondaryCaption();

}
