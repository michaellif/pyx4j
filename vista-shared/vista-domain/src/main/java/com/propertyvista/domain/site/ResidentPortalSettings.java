/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface ResidentPortalSettings extends IEntity {
    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    SiteDescriptor site();

    @Caption(name = "Resident Portal Enabled")
    IPrimitive<Boolean> enabled();

    @Caption(name = "Use Custom Skin")
    IPrimitive<Boolean> useCustomHtml();

    @Owned
    IList<HtmlContent> customHtml();

    @Owned
    IList<HtmlContent> proxyHtml();
}
