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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;

public class FundsReconciliationFileForm extends OperationsEntityForm<FundsReconciliationFileDTO> {

    private static final I18n i18n = I18n.get(FundsReconciliationFileForm.class);

    public FundsReconciliationFileForm(IForm<FundsReconciliationFileDTO> view) {
        super(FundsReconciliationFileDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().fileName(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().fundsTransferType(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().created(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().remoteFileDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().fileNameDate(), new FieldDecoratorBuilder().build()));

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
        panel.setWidget(++row, 0, 1, inject(proto().reconciliationRecords(), link, new FieldDecoratorBuilder().build()));

        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, 2, ((FundsReconciliationFileViewerView) getParentView()).getSummaryListerView().getLister());

        selectTab(addTab(panel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
