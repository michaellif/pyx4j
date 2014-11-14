/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.activity.crud.communication.MessageEditorActivity;
import com.propertyvista.crm.client.ui.components.boxes.TenantSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.communication.selector.CommunicationEndpointSelector;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.misc.VistaTODO;

public class MessageEditForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageEditForm.class);

    CommunicationEndpointSelector epSelectorNew;

    public MessageEditForm(IPrimeFormView<MessageDTO, ?> view) {
        super(MessageDTO.class, view);
        setTabBarVisible(false);
        selectTab(addTab(createGeneralForm(), i18n.tr("New message")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public IsWidget createGeneralForm() {
        FormPanel formPanel = new FormPanel(this);

        epSelectorNew = createCommunicationEndpointSelectorNew();

        formPanel.append(Location.Dual, proto().to(), epSelectorNew).decorate();

        formPanel.append(Location.Dual, proto().subject()).decorate();

        formPanel.append(Location.Dual, proto().category(), new CEntityComboBox<MessageCategory>(MessageCategory.class) {
            @Override
            public void retriveOptions(final AsyncOptionsReadyCallback<MessageCategory> callback) {
                resetCriteria();
                resetOptions();
                if (getParentView() == null || getParentView().getPresenter() == null) {
                    retriveOptionsPrivate(callback);
                } else {
                    MessageEditorActivity presenter = ((MessageEditorActivity) getParentView().getPresenter());
                    final CategoryType categoryType = presenter.getCategoryType();
                    if (categoryType == null) {
                        retriveOptionsPrivate(callback);
                    } else {
                        final PropertyCriterion critType = PropertyCriterion.eq(proto().categoryType(), categoryType);
                        final PropertyCriterion critTicketType = PropertyCriterion.ne(proto().ticketType(), TicketType.Maintenance);

                        resetCriteria();
                        addCriterion(critType);
                        addCriterion(critTicketType);

                        resetOptions();
                        retriveOptionsPrivate(callback);
                    }
                }
            }

            private void retriveOptionsPrivate(final AsyncOptionsReadyCallback<MessageCategory> callback) {
                super.retriveOptions(new AsyncOptionsReadyCallback<MessageCategory>() {
                    @Override
                    public void onOptionsReady(List<MessageCategory> opt) {

                        if (callback != null && opt != null) {
                            callback.onOptionsReady(opt);
                        }
                    }
                });
            }
        }).decorate();

        get(proto().category());

        formPanel.append(Location.Left, proto().onBehalf(), new TenantSelector()).decorate();
        formPanel.append(Location.Right, proto().onBehalfVisible()).decorate().customLabel(i18n.tr("Is Visible For Tenant"));
        formPanel.append(Location.Left, proto().allowedReply()).decorate();
        formPanel.append(Location.Right, proto().highImportance()).decorate();
        formPanel.br();

        if (VistaTODO.USE_RTF_EDITOR_FOR_COMMUNICATION) {
            formPanel.append(Location.Dual, proto().text(), new CRichTextArea()).decorate();
        } else {
            formPanel.append(Location.Dual, proto().text()).decorate();
        }
        formPanel.append(Location.Dual, proto().attachments(), new MessageAttachmentFolder());
        formPanel.br();

        return formPanel;
    }

    @Override
    protected MessageDTO preprocessValue(MessageDTO value, boolean fireEvent, boolean populate) {
        if (value == null || value.getPrimaryKey() == null || value.getPrimaryKey().isDraft()) {
            MessageCategory mc = ((MessageEditorActivity) getParentView().getPresenter()).getCategory();
            if (mc != null && !mc.isNull()) {
                get(proto().category()).setEditable(false);
            } else {
                get(proto().category()).setEditable(true);
            }
        }
        return value;
    }

    public void reinit() {
        //TODO : epSelector.removeAll();
    }

    public void addToItem(CommunicationEndpointDTO item) {
        getValue().to().add(item);
    }

    public void removeToItem(CommunicationEndpointDTO item) {
        getValue().to().remove(item);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
    }

    private CommunicationEndpointSelector createCommunicationEndpointSelectorNew() {
        return new CommunicationEndpointSelector();
    }

    class TenantSelector extends CEntitySelectorHyperlink<Tenant> {
        @Override
        protected AppPlace getTargetPlace() {
            return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().getPrimaryKey());
        }

        @Override
        protected TenantSelectionDialog getSelectorDialog() {
            return new TenantSelectionDialog() {

                @Override
                public boolean onClickOk() {
                    setValue(getSelectedItem());
                    return true;
                }
            };
        }
    }
}
