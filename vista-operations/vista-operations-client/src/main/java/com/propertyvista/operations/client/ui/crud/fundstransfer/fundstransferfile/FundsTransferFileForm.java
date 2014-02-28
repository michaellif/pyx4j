/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferfile;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;

public class FundsTransferFileForm extends OperationsEntityForm<FundsTransferFileDTO> {

    public FundsTransferFileForm(IForm<FundsTransferFileDTO> view) {
        super(FundsTransferFileDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileCreationNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileName())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().companyId())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().status())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fundsTransferType())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().sent())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().created())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().updated())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledged())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().recordsCount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileAmount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentFileName())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentRemoteFileDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatusCode())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentRejectReasonMessage())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().acknowledgmentStatus())).build());

        // TODO The mess with DTO and DBO here
        {
            AppPlaceBuilder<IList<FundsTransferRecord>> appPlaceBuilder = new AppPlaceBuilder<IList<FundsTransferRecord>>() {
                @Override
                public AppPlace createAppPlace(IList<FundsTransferRecord> value) {
                    CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(FundsTransferRecordDTO.class);
                    place.formListerPlace().queryArg(EntityFactory.getEntityPrototype(FundsTransferRecordDTO.class).padBatch().padFile().id().getPath().toString(),
                            value.getOwner().getPrimaryKey().toString());
                    return place;
                }
            };

            CEntityCollectionCrudHyperlink<IList<FundsTransferRecord>> link = new CEntityCollectionCrudHyperlink<IList<FundsTransferRecord>>(appPlaceBuilder);
            panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().debitRecords(), link)).build());
        }

        {
            AppPlaceBuilder<IList<FundsTransferBatch>> appPlaceBuilder = new AppPlaceBuilder<IList<FundsTransferBatch>>() {
                @Override
                public AppPlace createAppPlace(IList<FundsTransferBatch> value) {
                    CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(FundsTransferBatchDTO.class);
                    place.formListerPlace().queryArg(EntityFactory.getEntityPrototype(FundsTransferBatchDTO.class).padFile().id().getPath().toString(),
                            value.getOwner().getPrimaryKey().toString());
                    return place;
                }
            };

            CEntityCollectionCrudHyperlink<IList<FundsTransferBatch>> link = new CEntityCollectionCrudHyperlink<IList<FundsTransferBatch>>(appPlaceBuilder);
            panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().batches(), link)).build());
        }

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
