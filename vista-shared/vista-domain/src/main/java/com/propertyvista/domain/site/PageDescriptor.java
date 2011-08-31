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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.media.File;

public interface PageDescriptor extends Locale {

    public enum Type {

        staticContent,

        findApartment,

        potentialTenants,

        residents,

        landing;
    }

    @ToString(index = 0)
    @MemberColumn(name = "pageType")
    IPrimitive<Type> type();

    @NotNull
    @ToString(index = 1)
    IPrimitive<String> caption();

    @Owner
    PageDescriptor parent();

    @Transient
    @Caption(name = "Child Pages:")
    IList<PageDescriptor> childPages();

    @Owned
    @Detached
    PageContent content();

    // Image for landing page and for static
    File image();
}
