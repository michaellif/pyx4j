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
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ARPolicyPreloader extends AbstractPolicyPreloader<ARPolicy> {

    private final static I18n i18n = I18n.get(ARPolicyPreloader.class);

    public ARPolicyPreloader() {
        super(ARPolicy.class);
    }

    @Override
    protected ARPolicy createPolicy(StringBuilder log) {
        ARPolicy policy = EntityFactory.create(ARPolicy.class);

        policy.creditDebitRule().setValue(ARPolicy.CreditDebitRule.oldestDebtFirst);

        return policy;
    }
}
