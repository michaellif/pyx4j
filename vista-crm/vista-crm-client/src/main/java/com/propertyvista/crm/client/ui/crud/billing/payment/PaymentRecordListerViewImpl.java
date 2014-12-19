/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-22
 * @author vlads
 */
package com.propertyvista.crm.client.ui.crud.billing.payment;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordListerViewImpl extends AbstractListerView<PaymentRecordDTO> implements PaymentRecordListerView {

    public PaymentRecordListerViewImpl() {
        setDataTablePanel(new PaymentRecordLister());
    }
}
