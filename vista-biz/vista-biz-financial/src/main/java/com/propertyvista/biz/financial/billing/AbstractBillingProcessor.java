/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.propertyvista.biz.financial.AbstractProcessor;

public abstract class AbstractBillingProcessor extends AbstractProcessor {

    private final AbstractBillingManager billingManager;

    AbstractBillingProcessor(AbstractBillingManager billingManager) {
        this.billingManager = billingManager;
    }

    protected abstract void execute();

    public AbstractBillingManager getBillingManager() {
        return billingManager;
    }
}
