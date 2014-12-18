/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.core.ISet;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;

@DiscriminatorValue("EvictionPolicy")
public interface EvictionPolicy extends Policy {

    ISet<EvictionFlowStep> evictionFlow();
}
