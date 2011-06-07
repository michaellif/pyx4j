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
package com.propertyvista.portal.domain.site;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface PageContent extends IEntity {

    public static final String PATH_SEPARATOR = "/";

    @Deprecated
    PageDescriptor descriptor();

    @Indexed
    IPrimitive<String> path();

    @Owned
    IPrimitive<String> content();

}
