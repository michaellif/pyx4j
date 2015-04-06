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
 */
package com.pyx4j.tester.client.domain.test;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.shared.adapters.index.CaseInsensitiveIndexAdapter;

public interface TestCountry extends IEntity {

    @ToString
    @Indexed(adapters = CaseInsensitiveIndexAdapter.class)
    IPrimitive<String> name();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<TestProvince> provinces();

}