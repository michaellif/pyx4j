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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile;

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
import com.propertyvista.operations.domain.payment.pad.FundsReconciliationRecordRecord;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;

public class PadReconciliationFileForm extends OperationsEntityForm<FundsReconciliationFileDTO> {

    public PadReconciliationFileForm(IForm<FundsReconciliationFileDTO> view) {
        super(FundsReconciliationFileDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileName())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fundsTransferType())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().created())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().remoteFileDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().fileNameDate())).build());

        // TODO Sorry for the mess with DTO and DBO here,  will be fixed once we have AttacheLEvel.countOnly
        AppPlaceBuilder<IList<FundsReconciliationRecordRecord>> appPlaceBuilder = new AppPlaceBuilder<IList<FundsReconciliationRecordRecord>>() {
            @Override
            public AppPlace createAppPlace(IList<FundsReconciliationRecordRecord> value) {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(FundsReconciliationRecordRecordDTO.class);
                place.formListerPlace().queryArg(
                        EntityFactory.getEntityPrototype(FundsReconciliationRecordRecordDTO.class).reconciliationSummary().reconciliationFile().id().getPath()
                                .toString(), value.getOwner().getPrimaryKey().toString());
                return place;
            }
        };

        CEntityCollectionCrudHyperlink<IList<FundsReconciliationRecordRecord>> link = new CEntityCollectionCrudHyperlink<IList<FundsReconciliationRecordRecord>>(
                appPlaceBuilder);
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().reconciliationRecords(), link)).build());

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, 2, ((PadReconciliationFileViewerView) getParentView()).getSummaryListerView().getLister());

        selectTab(addTab(panel));
        setTabBarVisible(false);
    }
}
