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
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferfile;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;

public class FundsTransferFileForm extends OperationsEntityForm<FundsTransferFileDTO> {

    private static final I18n i18n = I18n.get(FundsTransferFileForm.class);

    public FundsTransferFileForm(IPrimeFormView<FundsTransferFileDTO, ?> view) {
        super(FundsTransferFileDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().fileCreationNumber()).decorate();
        formPanel.append(Location.Left, proto().fileName()).decorate();
        formPanel.append(Location.Left, proto().companyId()).decorate();
        formPanel.append(Location.Left, proto().status()).decorate();
        formPanel.append(Location.Left, proto().fundsTransferType()).decorate();
        formPanel.append(Location.Left, proto().sent()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();
        formPanel.append(Location.Left, proto().updated()).decorate();
        formPanel.append(Location.Left, proto().acknowledged()).decorate();
        formPanel.append(Location.Left, proto().recordsCount()).decorate();
        formPanel.append(Location.Left, proto().fileAmount()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentFileName()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentRemoteFileDate()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentStatusCode()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentRejectReasonMessage()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentStatus()).decorate();

        // TODO The mess with DTO and DBO here
        {
            AppPlaceBuilder<IList<FundsTransferRecord>> appPlaceBuilder = new AppPlaceBuilder<IList<FundsTransferRecord>>() {
                @Override
                public AppPlace createAppPlace(IList<FundsTransferRecord> value) {
                    CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(FundsTransferRecordDTO.class);
                    place.formListerPlace().queryArg(
                            EntityFactory.getEntityPrototype(FundsTransferRecordDTO.class).padBatch().padFile().id().getPath().toString(),
                            value.getOwner().getPrimaryKey().toString());
                    return place;
                }
            };

            CEntityCollectionCrudHyperlink<IList<FundsTransferRecord>> link = new CEntityCollectionCrudHyperlink<IList<FundsTransferRecord>>(appPlaceBuilder);
            formPanel.append(Location.Left, proto().debitRecords(), link).decorate();
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
            formPanel.append(Location.Left, proto().batches(), link).decorate();
        }

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
