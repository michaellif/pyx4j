/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.auditrecords;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.themes.OperationsTheme;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public class AuditRecordForm extends OperationsEntityForm<AuditRecordOperationsDTO> {

    public AuditRecordForm(IForm<AuditRecordOperationsDTO> view) {
        super(AuditRecordOperationsDTO.class, view);

        setTabBarVisible(false);

        TwoColumnFlexFormPanel detailsTab = new TwoColumnFlexFormPanel();
        int row = -1;
        detailsTab.setWidget(++row, 0, inject(proto().userName(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(row, 1, inject(proto().remoteAddr(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(++row, 0, inject(proto().userKey(), new CLabel<Key>(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(row, 1, inject(proto().sessionId(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(++row, 0, inject(proto().event(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(row, 1, inject(proto().when(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(++row, 0, inject(proto().namespace(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(row, 1, inject(proto().worldTime(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(++row, 0,
                inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class)), new FormDecoratorBuilder(10).build()));
        detailsTab.setWidget(++row, 0, inject(proto().application(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(row, 1, inject(proto().targetEntity(), new FormDecoratorBuilder().build()));
        detailsTab.setWidget(++row, 0, 2, inject(proto().details(), new FormDecoratorBuilder(true).build()));

        selectTab(addTab(detailsTab));

        get(proto().details()).asWidget().setStyleName(OperationsTheme.OperationsStyles.TextFieldPreformatted.name(), true);

    }
}
