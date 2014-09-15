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
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.backoffice.prime.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.backoffice.prime.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;
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

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().fileName()).decorate();
        formPanel.append(Location.Left, proto().fundsTransferType()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();
        formPanel.append(Location.Left, proto().remoteFileDate()).decorate();
        formPanel.append(Location.Left, proto().fileNameDate()).decorate();

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
        formPanel.append(Location.Left, proto().reconciliationRecords(), link).decorate();

        formPanel.br();

        formPanel.append(Location.Dual, ((FundsReconciliationFileViewerView) getParentView()).getSummaryListerView().getLister());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
