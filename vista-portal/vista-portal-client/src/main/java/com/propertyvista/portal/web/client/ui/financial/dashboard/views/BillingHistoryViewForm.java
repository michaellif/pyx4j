/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class BillingHistoryViewForm extends CPortalEntityForm<BillingHistoryDTO> {

    private static final I18n i18n = I18n.get(BillingHistoryViewForm.class);

    public BillingHistoryViewForm() {
        super(BillingHistoryDTO.class, null, i18n.tr("Billing History"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().bills(), new BillDataFolder()));

        return content;
    }

    class BillDataFolder extends VistaBoxFolder<BillDataDTO> {

        public BillDataFolder() {
            super(BillDataDTO.class, false);
        }

        @Override
        public IFolderItemDecorator<BillDataDTO> createItemDecorator() {
            BoxFolderItemDecorator<BillDataDTO> decor = (BoxFolderItemDecorator<BillDataDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof BillDataDTO) {
                return new BillDataViewer();
            }
            return super.create(member);
        }

        private class BillDataViewer extends CEntityDecoratableForm<BillDataDTO> {

            public BillDataViewer() {
                super(BillDataDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().referenceNo(), new CNumberLabel()), 100).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount(), new CMoneyLabel()), 100).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().fromDate(), new CDateLabel()), 100).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().dueDate(), new CDateLabel()), 100).build());

                return content;
            }
        }
    }
}