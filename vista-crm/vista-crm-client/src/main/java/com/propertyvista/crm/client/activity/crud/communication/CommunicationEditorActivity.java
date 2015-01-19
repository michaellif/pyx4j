/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.crm.rpc.services.CommunicationCrudService.MessageInitializationData;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.SpecialDelivery.DeliveryMethod;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public class CommunicationEditorActivity extends CrmEditorActivity<CommunicationThreadDTO> implements CommunicationEditorView.Presenter {

    private final CrudAppPlace place;

    private MessageCategory mc;

    private CategoryType mgc;

    private DeliveryMethod dm;

    private IList<CommunicationEndpoint> recipients;

    public CommunicationEditorActivity(CrudAppPlace place) {
        super(CommunicationThreadDTO.class, place, CrmSite.getViewFactory().getView(CommunicationEditorView.class), GWT
                .<CommunicationCrudService> create(CommunicationCrudService.class));
        this.place = place;
        InitializationData data = place.getInitializationData();
        if (data != null && data instanceof MessageInitializationData) {
            MessageInitializationData mid = (MessageInitializationData) data;
            recipients = mid.recipients();

            mc = mid.messageCategory();
            mgc = mid.categoryType() == null || mid.categoryType().isNull() ? null : mid.categoryType().getValue();
            dm = mid.deliveryMethod().getValue(null);
        } else {
            Object placeCriteria = place instanceof Message ? ((Message) place).getCriteria() : null;

            if (placeCriteria == null) {
                mgc = CategoryType.Message;
            } else if (placeCriteria instanceof MessageCategory) {
                mc = (MessageCategory) placeCriteria;
            } else if (placeCriteria instanceof CategoryType) {
                mgc = (CategoryType) placeCriteria;
            }
        }
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationThreadDTO> callback, MessageDTO message) {
        ((CommunicationCrudService) getService()).saveMessage(callback, message, null);

    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        MessageInitializationData initData = EntityFactory.create(MessageInitializationData.class);
        MessageDTO fm = place instanceof Message ? ((Message) place).getForwardedMessage() : null;
        initData.forwardedMessage().set(fm);
        if (recipients != null) {
            initData.recipients().set(recipients);
        }

        if (mc != null) {
            initData.messageCategory().set(mc);
        }
        initData.deliveryMethod().setValue(dm);
        callback.onSuccess(initData);
    }

    @Override
    public MessageCategory getCategory() {
        return mc;
    }

    @Override
    public DeliveryMethod getDeliveryMethod() {
        return dm;
    }

    @Override
    public CategoryType getCategoryType() {
        CategoryType result = null;
        if (mc != null) {
            result = mc.categoryType().getValue();
        }

        if (result == null) {
            return mgc;
        }
        return result;
    }

    @Override
    public String getEntityName() {
        if (dm != null) {
            return dm.toString();
        }
        CategoryType ct = getCategoryType();
        if (ct == null) {
            return CategoryType.Message.toString();
        }
        return ct.toString();
    }
}
