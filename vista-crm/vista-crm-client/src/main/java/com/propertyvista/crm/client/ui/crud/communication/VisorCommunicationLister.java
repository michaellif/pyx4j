/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author igors
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.communication.listers.CommunicationLister;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public class VisorCommunicationLister extends SiteDataTablePanel<CommunicationThreadDTO> {
    private static final I18n i18n = I18n.get(VisorCommunicationLister.class);

    private Button newMessage;

    private Button newTicket;

    private final IPrimePanePresenter presenter;

    private List<? extends CommunicationEndpoint> recipientScope;

    public VisorCommunicationLister(IPrimePanePresenter presenter, List<? extends CommunicationEndpoint> recipientScope) {
        this(presenter);
        this.recipientScope = recipientScope;
    }

    public VisorCommunicationLister(IPrimePanePresenter presenter) {
        super(CommunicationThreadDTO.class, GWT.<AbstractListCrudService<CommunicationThreadDTO>> create(CommunicationCrudService.class), true);

        this.presenter = presenter;

        setFilteringEnabled(true);
        // No sorting work for it
        getDataTable().setHasColumnClickSorting(false);

        addUpperActionItem(newMessage = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Message"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Message);
            }
        }));
        addUpperActionItem(newTicket = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Ticket"), new Command() {
            @Override
            public void execute() {
                editNewEntity(CategoryType.Ticket);
            }
        }));

        newTicket.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));
        newMessage.setPermission(DataModelPermission.permissionCreate(MessageDTO.class));

        setColumnDescriptors(CommunicationLister.createColumnDescriptors(null));
        setDataTableModel(new DataTableModel<CommunicationThreadDTO>());

    }

    @Override
    protected EntityListCriteria<CommunicationThreadDTO> updateCriteria(EntityListCriteria<CommunicationThreadDTO> criteria) {
        AppPlace place = presenter.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;
        CategoryType category = null;
        if (placeCriteria == null) {
            newMessage.setVisible(true);
            newTicket.setVisible(true);
            setAddNewActionEnabled(false);
            if (recipientScope != null) {
                OrCriterion senderOrRecipientCriteria = new OrCriterion(PropertyCriterion.in(criteria.proto().content().$().sender(), recipientScope),
                        PropertyCriterion.in(criteria.proto().content().$().recipients().$().recipient(), recipientScope));

                AndCriterion onBehalfCriteria = new AndCriterion(PropertyCriterion.in(criteria.proto().content().$().onBehalf(), recipientScope),
                        PropertyCriterion.eq(criteria.proto().content().$().onBehalfVisible(), true));

                criteria.or(senderOrRecipientCriteria, onBehalfCriteria);

            }
        } else {
            newMessage.setVisible(false);
            newTicket.setVisible(false);
            setAddNewActionEnabled(true);
            if (placeCriteria instanceof CategoryType) {
                setAddNewActionCaption(i18n.tr("New") + " " + placeCriteria.toString());
                criteria.eq(criteria.proto().category().categoryType(), category = (CategoryType) placeCriteria);
            } else if (placeCriteria instanceof MessageCategory) {
                MessageCategory mc = (MessageCategory) placeCriteria;
                if (TicketType.Maintenance.equals(mc.ticketType().getValue())) {
                    setAddNewActionEnabled(false);
                } else {
                    setAddNewActionCaption(i18n.tr("New") + " " + (category = mc.categoryType().getValue()).toString());
                }
                criteria.eq(criteria.proto().category(), mc);
            }
        }

        EntityListCriteria<CommunicationThreadDTO> result = super.updateCriteria(criteria);
        if (placeCriteria == null) {
            addOrIgnoreHidden(criteria);
        }
        return result;
    }

    private void addOrIgnoreHidden(EntityListCriteria<CommunicationThreadDTO> criteria) {
        List<Criterion> currentCriterias = criteria.getFilters();
        boolean ignoreHidden = true;
        if (currentCriterias != null && currentCriterias.size() > 0) {
            java.util.Iterator<Criterion> i = currentCriterias.iterator();

            while (i.hasNext()) {
                Criterion criterion = i.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

                    if (propertyCriterion.getPropertyPath().equals(criteria.proto().hidden().getPath().toString())) {
                        ignoreHidden = false;
                        break;
                    }
                }
            }
        }
        if (ignoreHidden) {
            criteria.notExists(criteria.proto().userPolicy(), PropertyCriterion.eq(criteria.proto().userPolicy().$().hidden(), true));
        } else {
            PropertyCriterion policy = criteria.getCriterion(criteria.proto().hidden());
            if (policy != null && policy.getValue() != null) {
                Boolean val = Boolean.valueOf(policy.getValue().toString());
                if (val != null && !val.booleanValue()) {
                    currentCriterias.remove(policy);
                    criteria.notExists(criteria.proto().userPolicy(), PropertyCriterion.eq(criteria.proto().userPolicy().$().hidden(), true));
                }
            }
        }
    }

    @Override
    protected void onItemNew() {
        AppPlace place = presenter.getPlace();
        Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;

        editNewEntity(placeCriteria);

    }

    private void editNewEntity(Object placeCriteria) {
        CommunicationCrudService.MessageInitializationData initData = EntityFactory.create(CommunicationCrudService.MessageInitializationData.class);
        if (placeCriteria == null) {
            initData.categoryType().setValue(null);
        } else if (placeCriteria instanceof MessageCategory) {
            initData.messageCategory().set((MessageCategory) placeCriteria);
        } else if (placeCriteria instanceof CategoryType) {
            initData.categoryType().setValue((CategoryType) placeCriteria);
        }
        if (recipientScope != null && recipientScope.size() > 0) {
            initData.recipients().addAll(recipientScope);
        }
        editNew(Message.class, initData);
    }
}
