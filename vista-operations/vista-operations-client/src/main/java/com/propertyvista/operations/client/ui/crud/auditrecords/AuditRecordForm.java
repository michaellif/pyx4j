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
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().userName()));
        detailsTab.setWidget(row, 1, injectAndDecorate(proto().remoteAddr()));
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().userKey(), new CLabel<Key>()));
        detailsTab.setWidget(row, 1, injectAndDecorate(proto().sessionId()));
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().event()));
        detailsTab.setWidget(row, 1, injectAndDecorate(proto().when()));
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().namespace()));
        detailsTab.setWidget(row, 1, injectAndDecorate(proto().worldTime()));
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class)), 10));
        detailsTab.setWidget(++row, 0, injectAndDecorate(proto().application()));
        detailsTab.setWidget(row, 1, injectAndDecorate(proto().targetEntity()));
        detailsTab.setWidget(++row, 0, 2, injectAndDecorate(proto().details(), true));

        selectTab(addTab(detailsTab));

        get(proto().details()).asWidget().setStyleName(OperationsTheme.OperationsStyles.TextFieldPreformatted.name(), true);

    }
}
