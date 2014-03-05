/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;

// BatchContext not really supported and is only used in MoneyInBatch
public class ARInternalPaymentBatchContext implements PaymentBatchContext {

    @Override
    public boolean isBatchFull() {
        return false;
    }

    @Override
    public void postBatch() throws ARException {
    }

    @Override
    public void cancelBatch() throws ARException {
    }

}
