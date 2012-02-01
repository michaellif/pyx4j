/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.util;

import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;

public abstract class AbstractPoliciesPreloader extends DataPreloaderCollection {

    PolicyNode topNode;

    protected void setTopNode(PolicyNode topNode) {
        this.topNode = topNode;
    }

    protected void add(AbstractPolicyPreloader<? extends Policy> policyPreloader) {
        policyPreloader.setTopNode(topNode);
        super.add(policyPreloader);
    }

    @Override
    public String create() {
        return "Created default policies:\n\t" + super.create().replace("\n", "\n\t");
    }

}
