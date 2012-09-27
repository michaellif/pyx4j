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
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ArrearsStatusGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<ArrearsStatusGadgetMetadata> {

    private static final I18n i18n = I18n.get(ArrearsStatusGadgetMetadataDefaultSettings.class);

    @Override
    public void init(ArrearsStatusGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.pageSize().setValue(10);
        gadgetMetadata.category().setValue(DebitType.total);
        gadgetMetadata.columnDescriptors().addAll(defineArrearsStatusListerColumns());

    }

    private List<ColumnDescriptorEntity> defineArrearsStatusListerColumns() {
        LeaseArrearsSnapshotDTO proto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshotDTO.class);

        return Arrays.asList(//@formatter:off
                defColumn(proto.billingAccount().lease().unit().building().propertyCode()).visible(true).build(),
                defColumn(proto.billingAccount().lease().unit().building().info().name()).title(i18n.tr("Building")).build(),
                defColumn(proto.billingAccount().lease().unit().building().info().address().streetNumber()).visible(false).build(),
                defColumn(proto.billingAccount().lease().unit().building().info().address().streetName()).visible(false).build(),                    
                defColumn(proto.billingAccount().lease().unit().building().info().address().province().name()).visible(false).title(i18n.tr("Province")).build(),                    
                defColumn(proto.billingAccount().lease().unit().building().info().address().country().name()).visible(false).title(i18n.tr("Country")).build(),                    
                defColumn(proto.billingAccount().lease().unit().building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                defColumn(proto.billingAccount().lease().unit().info().number()).title(i18n.tr("Unit")).build(),
                defColumn(proto.billingAccount().lease().leaseId()).build(),
                defColumn(proto.billingAccount().lease().leaseFrom()).build(),
                defColumn(proto.billingAccount().lease().leaseTo()).build(),
                
                // arrears
                defColumn(proto.selectedBuckets().bucketCurrent()).build(),
                defColumn(proto.selectedBuckets().bucket30()).build(),
                defColumn(proto.selectedBuckets().bucket60()).build(),
                defColumn(proto.selectedBuckets().bucket90()).build(),
                defColumn(proto.selectedBuckets().bucketOver90()).build(),
                
                defColumn(proto.selectedBuckets().arrearsAmount()).build()
        //TODO calculate CREDIT AMOUNT                    
        //        column(proto.selectedBuckets().creditAmount()).build(),
        //        column(proto.selectedBuckets().totalBalance()).build(),
        //TODO calculate LMR                    
        //        column(proto.lmrToUnitRentDifference()).build()
        );//@formatter:on        
    }

}
