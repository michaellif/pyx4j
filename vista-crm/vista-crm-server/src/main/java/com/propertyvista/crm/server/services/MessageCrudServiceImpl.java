/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2014
 * @author igors
 */
package com.propertyvista.crm.server.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.IVRDelivery;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.NotificationDelivery;
import com.propertyvista.domain.communication.SMSDelivery;
import com.propertyvista.domain.communication.SpecialDelivery;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.communication.ThreadPolicyHandle;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.dto.MessageDTO.ViewScope;

public class MessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<Message, MessageDTO> implements MessageCrudService {
    private final static I18n i18n = I18n.get(MessageCrudServiceImpl.class);

    public MessageCrudServiceImpl() {
        super(Message.class, MessageDTO.class);
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, Message boProto, MessageDTO toProto) {
        if (path.equals(toProto.category().getPath().toString())) {
            return boProto.thread().category().getPath();
        }
        if (path.equals(toProto.status().getPath().toString())) {
            return boProto.thread().status().getPath();
        }
        if (path.equals(toProto.owner().getPath().toString()) || path.equals(toProto.ownerForList().getPath().toString())) {
            return boProto.thread().owner().getPath();
        }
        if (path.equals(toProto.category().categoryType().getPath().toString())) {
            return boProto.thread().category().categoryType().getPath();
        }
        if (path.equals(toProto.category().dispatchers().$().user().getPath().toString())) {
            return boProto.thread().category().dispatchers().$().user().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().isRead().getPath().toString()) || path.equals(toProto.isRead().getPath().toString())) {
            return boProto.recipients().$().isRead().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().star().getPath().toString()) || path.equals(toProto.star().getPath().toString())) {
            return boProto.recipients().$().star().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().recipient().getPath().toString())) {
            return boProto.recipients().$().recipient().getPath();
        }
        if (path.equals(toProto.content().$().date().getPath().toString())) {
            return boProto.date().getPath();
        }
        if (path.equals(toProto.subject().getPath().toString())) {
            return boProto.thread().subject().getPath();
        }
        if (path.equals(toProto.deliveryMethod().getPath().toString())) {
            return boProto.thread().deliveryMethod().getPath();
        }
        if (path.equals(toProto.hidden().getPath().toString())) {
            return boProto.thread().userPolicy().$().hidden().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Message> boCriteria, EntityListCriteria<MessageDTO> toCriteria) {
        List<Sort> sorts = toCriteria.getSorts();
        if (sorts == null || sorts.isEmpty()) {
            toCriteria.desc(toCriteria.proto().date());
        }
        PropertyCriterion recipientCiteria = toCriteria.getCriterion(toCriteria.proto().thread().content().$().recipients().$().recipient());
        if (recipientCiteria != null) {
            toCriteria.getFilters().remove(recipientCiteria);
            boCriteria.eq(boCriteria.proto().recipients().$().recipient(), CrmAppContext.getCurrentUserEmployee());
        }

        PropertyCriterion ownerCiteria = toCriteria.getCriterion(toCriteria.proto().viewScope());
        if (ownerCiteria != null && ownerCiteria.getValue() != null) {
            ViewScope critValue = (ViewScope) ownerCiteria.getValue();
            toCriteria.getFilters().remove(ownerCiteria);
            if (ViewScope.Dispatched.equals(critValue)) {
                boCriteria.eq(boCriteria.proto().thread().owner(),
                        ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));
            } else {
                boCriteria.add(new OrCriterion(PropertyCriterion.eq(boCriteria.proto().thread().category().categoryType(), CategoryType.Message),
                        PropertyCriterion.eq(boCriteria.proto().thread().owner(), CrmAppContext.getCurrentUserEmployee())));
                if (ViewScope.Direct.equals(critValue)) {
                    boCriteria.add(new OrCriterion(PropertyCriterion.isNull(boCriteria.proto().isSystem()), PropertyCriterion.eq(boCriteria.proto().isSystem(),
                            false)));
                }
            }
        }

        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected EntitySearchResult<Message> query(EntityListCriteria<Message> criteria) {
        return ServerSideFactory.create(CommunicationMessageFacade.class).query(criteria);
    }

    @Override
    public void listForHeader(AsyncCallback<EntitySearchResult<MessageDTO>> callback) {
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        Employee e = CrmAppContext.getCurrentUserEmployee();
        List<CommunicationThread> directThreads = communicationFacade.getDirectThreads(e);
        List<CommunicationThread> dispatchedThreads = communicationFacade.getDispathcedThreads(e);

        if (directThreads != null && directThreads.size() > 0 && dispatchedThreads != null && dispatchedThreads.size() > 0) {
            directThreads.removeAll(dispatchedThreads);
        }

        if (directThreads == null) {
            directThreads = new Vector<CommunicationThread>();
        }

        if (dispatchedThreads != null && dispatchedThreads.size() > 0) {
            directThreads.addAll(dispatchedThreads);
        }

        EntityListCriteria<MessageDTO> messageCriteria = EntityListCriteria.create(MessageDTO.class);
        if (directThreads != null && directThreads.size() > 0) {
            messageCriteria.add(PropertyCriterion.in(messageCriteria.proto().thread(), directThreads));
        } else {
            messageCriteria.notExists(messageCriteria.proto().thread());
        }
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        messageCriteria.setPageSize(50);
        messageCriteria.setPageNumber(0);
        list(callback, messageCriteria);
    }

    @Override
    protected MessageDTO init(InitializationData initializationData) {
        MessageDTO dto = EntityFactory.create(MessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.highImportance().setValue(false);
        dto.allowedReply().setValue(true);
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        dto.senderDTO().set(communicationFacade.generateEndpointDTO(CrmAppContext.getCurrentUserEmployee()));

        if (initializationData instanceof MessageInitializationData) {
            MessageInitializationData data = (MessageInitializationData) initializationData;
            if (data.forwardedMessage() != null && !data.forwardedMessage().isNull()) {
                MessageDTO forwardedMessage = data.forwardedMessage();
                if (forwardedMessage != null) {
                    dto.subject().setValue(communicationFacade.buildForwardSubject(forwardedMessage));
                    dto.text().setValue(communicationFacade.buildForwardText(forwardedMessage));
                    dto.hasAttachments().set(forwardedMessage.hasAttachments());

                    if (forwardedMessage.hasAttachments().getValue(false).booleanValue()) {
                        for (MessageAttachment fromAtt : forwardedMessage.attachments()) {
                            MessageAttachment toAtt = EntityFactory.create(MessageAttachment.class);
                            toAtt.description().set(fromAtt.description());
                            toAtt.file().set(fromAtt.file());
                            FileUploadRegistry.register(fromAtt.file());
                            dto.attachments().add(toAtt);
                        }
                    }
                }
            }
            if (data.messageCategory() != null && !data.messageCategory().isNull()) {
                dto.category().set(data.messageCategory());
            }

            if (data.deliveryMethod() != null && !data.deliveryMethod().isNull()) {
                dto.deliveryMethod().set(data.deliveryMethod());
                dto.allowedReply().setValue(false);
            }

            if (data.recipients() != null && data.recipients().size() > 0) {
                for (CommunicationEndpoint t : data.recipients()) {
                    dto.to().add(communicationFacade.generateEndpointDTO(t));
                }

            }
        }
        return dto;

    }

    @Override
    protected boolean persist(Message bo, MessageDTO to) {

        boolean isNew = to.thread() == null || to.thread().isNull() || to.thread().isEmpty();
        if (isNew) {
            bo.date().setValue(SystemDateManager.getDate());
        }

        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        communicationFacade.buildRecipientList(bo, to, null);

        bo.attachments().set(to.attachments());
        bo.date().setValue(SystemDateManager.getDate());
        bo.sender().set(CrmAppContext.getCurrentUserEmployee());
        bo.onBehalf().set(to.onBehalf());
        bo.onBehalfVisible().set(to.onBehalfVisible());
        bo.text().set(to.text());
        bo.highImportance().set(to.highImportance());
        if (to.category().isValueDetached()) {
            Persistence.service().retrieve(to.category());
        }
        boolean isTicket = CategoryType.Ticket.equals(to.category().categoryType().getValue());

        if (isTicket) {
            bo.recipients().add(communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), true));
        }
        CommunicationThread t = EntityFactory.create(CommunicationThread.class);
        t.subject().set(to.subject());
        if (to.deliveryMethod() != null && !to.deliveryMethod().isNull()) {
            SpecialDelivery da = null;
            switch (to.deliveryMethod().getValue()) {
            case SMS:
                da = EntityFactory.create(SMSDelivery.class);
                if (to.deliveredText() != null && !to.deliveredText().isNull()) {
                    t.subject().setValue(to.deliveredText().getValue().substring(0, Math.min(77, to.deliveredText().getValue().length())));
                }
                break;
            case IVR:
                da = EntityFactory.create(IVRDelivery.class);
                if (to.deliveredText() != null && !to.deliveredText().isNull()) {
                    t.subject().setValue(to.deliveredText().getValue().substring(0, Math.min(77, to.deliveredText().getValue().length())));
                }
                break;
            case Notification:
                NotificationDelivery nd = EntityFactory.create(NotificationDelivery.class);
                da = nd;
                nd.dateFrom().setValue(to.dateFrom() == null || to.dateFrom().isNull() ? new LogicalDate() : to.dateFrom().getValue(new LogicalDate()));
                nd.dateTo().set(to.dateTo());
                nd.notificationType().set(to.notificationType());
                break;
            }
            da.deliveredText().set(to.deliveredText());
            t.deliveryMethod().set(to.deliveryMethod());
            t.specialDelivery().set(da);
        }
        t.allowedReply().set(to.allowedReply());
        t.category().set(to.category());
        if (isNew && isTicket) {
            t.status().setValue(ThreadStatus.Open);
        }
        t.content().add(bo);
        t.owner().set(
                to.owner() == null || to.owner().isEmpty() || to.owner().isPrototype() || to.owner().isNull() ? communicationFacade
                        .getSystemEndpointFromCache(SystemEndpointName.Unassigned) : to.owner().endpoint());

        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
        return Persistence.secureSave(t);
    }

    @Override
    protected void enhanceRetrieved(Message bo, MessageDTO to, RetrieveTarget retrieveTarget) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceMessageDbo(bo, to, false, CrmAppContext.getCurrentUserEmployee());
    }

    @Override
    protected void enhanceListRetrieved(Message entity, MessageDTO dto) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceMessageDbo(entity, dto, true, CrmAppContext.getCurrentUserEmployee());
    }

    @Override
    public void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message, ThreadStatus threadStatus) {
        saveAndUpdate(callback, message, threadStatus, true);
    }

    private void saveAndUpdate(AsyncCallback<MessageDTO> callback, MessageDTO message, ThreadStatus threadStatus, boolean updateOwner) {
        Employee currentUser = CrmAppContext.getCurrentUserEmployee();
        if (message.date().isNull()) {
            CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
            Message m = communicationFacade.saveMessage(message, threadStatus, currentUser, updateOwner);
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned));
            dhCriteria.eq(dhCriteria.proto().message().thread(), m.thread());
            dhCriteria.add(new OrCriterion(PropertyCriterion.isNull(dhCriteria.proto().message().isSystem()), PropertyCriterion.eq(dhCriteria.proto().message()
                    .isSystem(), false)));
            List<DeliveryHandle> dhs = Persistence.service().query(dhCriteria, AttachLevel.Attached);
            if (message.thread().owner() != null && !message.thread().owner().isEmpty() && dhs != null && dhs.size() > 0) {
                for (DeliveryHandle dh : dhs) {
                    dh.recipient().set(message.thread().owner());
                    dh.isRead().setValue(false);
                    dh.star().setValue(false);
                    Persistence.service().persist(dh);
                }
                Persistence.service().commit();
            }
            retrieve(callback, m.getPrimaryKey(), RetrieveTarget.View);
        } else {
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), currentUser);
            dhCriteria.eq(dhCriteria.proto().message(), message);
            DeliveryHandle dh = Persistence.retrieveUnique(dhCriteria, AttachLevel.Attached);
            dh.isRead().set(message.isRead());
            dh.star().set(message.star());
            Persistence.service().persist(dh);
            Persistence.service().commit();
            callback.onSuccess(message);
        }
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
    }

    @Override
    public void assignOwnership(AsyncCallback<MessageDTO> callback, MessageDTO message, String additionalComment, IEntity employee) {
        CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        Employee e = null;
        if (employee != null) {
            if (employee.getInstanceValueClass().equals(Employee.class)) {
                e = employee.cast();
            } else {
                e = CrmAppContext.getCurrentUserEmployee();
            }
        }

        thread.owner().set(e == null ? communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned) : e);

        Persistence.service().persist(thread);
        Persistence.service().commit();

        message.owner().set(
                communicationFacade.generateEndpointDTO(e == null ? communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned) : e));
        message.status().set(thread.status());
        if (!CrmAppContext.getCurrentUserEmployee().equals(employee)) {
            MessageDTO dto = EntityFactory.create(MessageDTO.class);
            dto.to().add(message.owner());
            dto.text().setValue(
                    additionalComment == null ? i18n.tr("Ticket owner was changed to") + ": " + message.owner().name().getStringView() : additionalComment);
            dto.thread().set(thread);
            dto.isSystem().setValue(true);
            saveAndUpdate(callback, dto, null, false);
        } else {
            ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
            callback.onSuccess(message);
        }
    }

    @Override
    public void hideUnhide(AsyncCallback<MessageDTO> callback, MessageDTO source) {

        Employee currentUser = CrmAppContext.getCurrentUserEmployee();
        EntityQueryCriteria<ThreadPolicyHandle> policyCriteria = EntityQueryCriteria.create(ThreadPolicyHandle.class);

        policyCriteria.add(PropertyCriterion.in(policyCriteria.proto().thread(), source.thread()));
        policyCriteria.eq(policyCriteria.proto().policyConsumer(), currentUser);

        ThreadPolicyHandle handle = Persistence.secureRetrieve(policyCriteria);
        if (handle == null) {
            handle = EntityFactory.create(ThreadPolicyHandle.class);
            handle.policyConsumer().set(currentUser);
            handle.thread().set(source.thread());
            handle.hidden().setValue(true);
        } else {
            handle.hidden().setValue(!handle.hidden().getValue(false));
        }

        Persistence.service().persist(handle);
        Persistence.service().commit();
        retrieve(callback, source.getPrimaryKey(), RetrieveTarget.View);
    }
}