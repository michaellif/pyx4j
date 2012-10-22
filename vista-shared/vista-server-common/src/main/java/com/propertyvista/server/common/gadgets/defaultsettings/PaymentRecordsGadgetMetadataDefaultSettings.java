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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentRecordsGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<PaymentRecordsGadgetMetadata> {

    private static final I18n i18n = I18n.get(PaymentRecordsGadgetMetadataDefaultSettings.class);

    @Override
    public void init(PaymentRecordsGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.columnDescriptors().addAll(definePaymentRecordsListerColumns());
        gadgetMetadata.paymentMethodFilter().addAll(EnumSet.allOf(PaymentType.class));
        gadgetMetadata.paymentStatusFilter().addAll(EnumSet.complementOf(EnumSet.of(PaymentStatus.Processing)));
    }

    private List<ColumnDescriptorEntity> definePaymentRecordsListerColumns() {
        PaymentRecordForReportDTO proto = EntityFactory.create(PaymentRecordForReportDTO.class);
        return Arrays.asList(//@formatter:off
        );//@formatter:on

    }
}
