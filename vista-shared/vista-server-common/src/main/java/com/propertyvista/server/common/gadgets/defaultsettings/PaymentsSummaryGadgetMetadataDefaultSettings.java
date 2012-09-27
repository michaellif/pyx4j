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

import static com.propertyvista.server.common.gadgets.defaultsettings.ColumnDescriptorEntityBuilder.defColumn;

import java.util.Arrays;
import java.util.Collection;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class PaymentsSummaryGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<PaymentsSummaryGadgetMetadata> {

    private static final I18n i18n = I18n.get(PaymentsSummaryGadgetMetadataDefaultSettings.class);

    @Override
    public void init(PaymentsSummaryGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.columnDescriptors().addAll(definePaymentsSummaryListerColumns());
        gadgetMetadata.paymentStatus().addAll(PaymentStatus.processed());
    }

    private Collection<? extends ColumnDescriptorEntity> definePaymentsSummaryListerColumns() {
        PaymentsSummary proto = EntityFactory.create(PaymentsSummary.class);
        return Arrays.asList(//@formatter:off
                (PaymentsSummary.summaryByBuilding)?
                        defColumn(proto.building()).build():
                        defColumn(proto.merchantAccount().accountNumber()).title(i18n.tr("Merchant Account")).build(),
                defColumn(proto.status()).build(),
                defColumn(proto.cash()).build(),
                defColumn(proto.cheque()).build(),
                defColumn(proto.eCheque()).build(),
                defColumn(proto.eft()).build(),
                defColumn(proto.cc()).build(),
                defColumn(proto.interac()).build()
        );//@formatter:on        
    }

}
