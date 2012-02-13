/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 28, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.ref;

import com.pyx4j.entity.adapters.index.CaseInsensitiveIndexAdapter;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.policy.framework.PolicyNode;

@DiscriminatorValue("Disc_Country")
public interface Country extends PolicyNode {

    @ToString
    @Indexed(adapters = CaseInsensitiveIndexAdapter.class)
    IPrimitive<String> name();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Province> provinces();

}