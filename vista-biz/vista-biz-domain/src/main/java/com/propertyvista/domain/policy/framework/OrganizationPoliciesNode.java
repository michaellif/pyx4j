/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.framework;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * 
 * Node in the policies hierarchy that represents the whole organization.
 * 
 */
@DiscriminatorValue("OrganizationPoliciesNode")
@Caption(name = "Organization")
@ToStringFormat(value = "Organization")
public interface OrganizationPoliciesNode extends PolicyNode {

    @Deprecated
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    IPrimitive<String> x();
}
