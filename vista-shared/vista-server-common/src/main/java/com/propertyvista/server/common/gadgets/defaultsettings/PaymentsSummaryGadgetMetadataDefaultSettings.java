/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets.defaultsettings;

import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class PaymentsSummaryGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<PaymentsSummaryGadgetMetadata> {

    @Override
    public void init(PaymentsSummaryGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.paymentsSummaryListerSettings().pageSize().setValue(10);
        gadgetMetadata.paymentStatus().addAll(PaymentStatus.processed());
    }

}
