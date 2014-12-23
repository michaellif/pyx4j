/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2014
 * @author VladL
 */
package com.propertyvista.common.client.ui.components.folders;

import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelHR;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorLabel;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PapFolder extends VistaBoxFolder<PreauthorizedPaymentDTO> {

    private static final I18n i18n = I18n.get(PapFolder.class);

    public PapFolder() {
        super(PreauthorizedPaymentDTO.class, true);
        setOrderable(false);
        setNoDataLabel(i18n.tr("No AutoPay payments are setup"));
    }

    @Override
    protected CForm<PreauthorizedPaymentDTO> createItemForm(IObject<?> member) {
        return new PreauthorizedPaymentEditor();
    }

    @Override
    protected void removeItem(final CFolderItem<PreauthorizedPaymentDTO> item) {
        MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
            @Override
            public void execute() {
                PapFolder.super.removeItem(item);
            }
        });
    }

    /**
     * Override this method to supply tenant's payment methods
     *
     * @return list of available payment methods.
     */
    protected List<LeasePaymentMethod> getPaymentMethods() {
        return Collections.emptyList();
    }

    private class PreauthorizedPaymentEditor extends CForm<PreauthorizedPaymentDTO> {

        public PreauthorizedPaymentEditor() {
            super(PreauthorizedPaymentDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().effectiveFrom()).decorate().componentWidth(120);

            formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate();
            formPanel.append(Location.Right, proto().created()).decorate().componentWidth(180);
            formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(180);

            formPanel.append(Location.Dual, proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
                @Override
                protected AbstractEntitySelectorDialog<LeasePaymentMethod> getSelectorDialog() {
                    return new EntitySelectorListDialog<LeasePaymentMethod>(i18n.tr("Select Payment Method"), false, getPaymentMethods()) {
                        @Override
                        public boolean onClickOk() {
                            get(proto().paymentMethod()).setValue(getSelectedItems().iterator().next());
                            return true;
                        }
                    };
                }
            }).decorate();
            formPanel.append(Location.Dual, proto().coveredItemsDTO(), new PapCoveredItemDtoFolder() {
                @Override
                public void onAmontValueChange() {
                    fillTotal(getValue());
                }
            });

            HTML separator = new HTML("&nbsp;");
            separator.setStyleName(FormPanelHR.name());
            separator.setWidth("22em");
            formPanel.append(Location.Right, separator);
            formPanel.append(Location.Right, proto().total()).decorate().componentWidth(180);

            formPanel.append(Location.Dual, proto().comments()).decorate();

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().id()).setVisible(!getValue().id().isNull());
            get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
            get(proto().created()).setVisible(!getValue().created().isNull());
            get(proto().updated()).setVisible(!getValue().updated().isNull());

            get(proto().comments()).setVisible(isEditable() || !getValue().comments().isNull());

            fillTotal(getValue().coveredItemsDTO());
        }

        private void fillTotal(List<PreauthorizedPaymentCoveredItemDTO> items) {
            BigDecimal total = BigDecimal.ZERO;
            for (PreauthorizedPaymentCoveredItemDTO item : items) {
                if (!item.amount().isNull()) {
                    total = (total.add(item.amount().getValue()));
                }
            }
            get(proto().total()).setValue(total);
        }
    }
}
