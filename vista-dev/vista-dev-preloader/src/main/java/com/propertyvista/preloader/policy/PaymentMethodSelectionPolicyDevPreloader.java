/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader.policy;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;

public class PaymentMethodSelectionPolicyDevPreloader extends BaseVistaDevDataPreloader {

    public PaymentMethodSelectionPolicyDevPreloader() {
    }

    @Override
    public String create() {
        PaymentTypeSelectionPolicy policy = Persistence.service().retrieve(EntityQueryCriteria.create(PaymentTypeSelectionPolicy.class));
        policy.residentPortalCreditCardVisa().setValue(false);
        policy.prospectCreditCardVisa().setValue(false);
        Persistence.service().persist(policy);
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
