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
package com.propertyvista.biz.financial.billingext;

import com.propertyvista.biz.financial.billing.AbstractProcessor;

public abstract class ExternalAbstractProcessor extends AbstractProcessor {

    private final ExternalBillProducer billProducer;

    ExternalAbstractProcessor(ExternalBillProducer billProducer) {
        this.billProducer = billProducer;
    }

    protected abstract void execute();

    public ExternalBillProducer getBillProducer() {
        return billProducer;
    }
}
