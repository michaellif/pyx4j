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
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
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
import com.propertyvista.dto.communication.CommunicationEndpointDTO;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.CommunicationThreadDTO.ViewScope;
import com.propertyvista.dto.communication.MessageDTO;

public class CommunicationCrudServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationThread, CommunicationThreadDTO> implements CommunicationCrudService {
    private final static I18n i18n = I18n.get(CommunicationCrudServiceImpl.class);

    public CommunicationCrudServiceImpl() {
        super(new CrudEntityBinder<CommunicationThread, CommunicationThreadDTO>(CommunicationThread.class, CommunicationThreadDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.id(), boProto.id());
                bind(toProto.subject(), boProto.subject());
                bind(toProto.allowedReply(), boProto.allowedReply());
                bind(toProto.status(), boProto.status());
                bind(toProto.category(), boProto.category());
                bind(toProto.content(), boProto.content());
                bind(toProto.userPolicy(), boProto.userPolicy());
                bind(toProto.associated(), boProto.associated());
                bind(toProto.deliveryMethod(), boProto.deliveryMethod());
                bind(toProto.specialDelivery(), boProto.specialDelivery());
            }
        });
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, CommunicationThread boProto, CommunicationThreadDTO toProto) {

        if (path.equals(toProto.category().categoryType().getPath().toString())) {
            return boProto.category().categoryType().getPath();
        }
        if (path.equals(toProto.owner().getPath().toString())) {
            return boProto.owner().getPath();
        }
        if (path.equals(toProto.category().dispatchers().$().user().getPath().toString())) {
            return boProto.category().dispatchers().$().user().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<CommunicationThread> boCriteria, EntityListCriteria<CommunicationThreadDTO> toCriteria) {

        PropertyCriterion recipientCiteria = toCriteria.getCriterion(toCriteria.proto().content().$().recipients().$().recipient());
        Employee e = CrmAppContext.getCurrentUserEmployee();
        if (recipientCiteria != null) {
            toCriteria.getFilters().remove(recipientCiteria);
            boCriteria.eq(boCriteria.proto().content().$().recipients().$().recipient(), e);
        }

        PropertyCriterion ownerCiteria = toCriteria.getCriterion(toCriteria.proto().viewScope());
        if (ownerCiteria != null && ownerCiteria.getValue() != null) {
            ViewScope critValue = (ViewScope) ownerCiteria.getValue();
            toCriteria.getFilters().remove(ownerCiteria);
            if (ViewScope.DispatchQueue.equals(critValue)) {
                boCriteria.eq(boCriteria.proto().content().$().recipients().$().recipient(), ServerSideFactory.create(CommunicationMessageFacade.class)
                        .getSystemEndpointFromCache(SystemEndpointName.Unassigned));
            } else if (ViewScope.Messages.equals(critValue)) {
                boCriteria.add(new OrCriterion(new OrCriterion(PropertyCriterion.eq(boCriteria.proto().content().$().sender(), e),//
                        PropertyCriterion.eq(boCriteria.proto().content().$().recipients().$().recipient(), e)),//
                        PropertyCriterion.eq(boCriteria.proto().owner(), e)));
            }
        }

        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected EntitySearchResult<CommunicationThread> query(EntityListCriteria<CommunicationThread> criteria) {
        return ServerSideFactory.create(CommunicationMessageFacade.class).query(criteria);
    }

    @Override
    public void listForHeader(AsyncCallback<EntitySearchResult<CommunicationThreadDTO>> callback) {
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

        EntityListCriteria<CommunicationThreadDTO> messageCriteria = EntityListCriteria.create(CommunicationThreadDTO.class);
        if (directThreads != null && directThreads.size() > 0) {
            messageCriteria.add(PropertyCriterion.in(messageCriteria.proto().id(), directThreads));
        } else {
            messageCriteria.notExists(messageCriteria.proto());
        }
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        messageCriteria.setPageSize(50);
        messageCriteria.setPageNumber(0);
        list(callback, messageCriteria);
    }

    @Override
    protected CommunicationThreadDTO init(InitializationData initializationData) {
        CommunicationThreadDTO result = super.init(initializationData);
        result.allowedReply().setValue(true);
        MessageDTO dto = EntityFactory.create(MessageDTO.class);
        result.representingMessage().set(dto);
        result.childMessages().add(dto);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.highImportance().setValue(false);
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        dto.senderDTO().set(communicationFacade.generateEndpointDTO(CrmAppContext.getCurrentUserEmployee()));

        if (initializationData instanceof MessageInitializationData) {
            MessageInitializationData data = (MessageInitializationData) initializationData;
            if (data.forwardedMessage() != null && !data.forwardedMessage().isNull()) {
                MessageDTO forwardedMessage = data.forwardedMessage();
                if (forwardedMessage != null) {
                    result.subject().setValue(communicationFacade.buildForwardSubject(forwardedMessage));
                    dto.content().setValue(communicationFacade.buildForwardText(forwardedMessage, result.subject().getValue()));
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
                result.category().set(data.messageCategory());
            }

            if (data.deliveryMethod() != null && !data.deliveryMethod().isNull()) {
                dto.deliveryMethod().set(data.deliveryMethod());
                result.allowedReply().setValue(false);
            }

            if (data.thread() != null && !data.thread().isNull()) {
                dto.thread().set(data.thread());
            }

            if (data.recipients() != null && data.recipients().size() > 0) {
                for (CommunicationEndpoint t : data.recipients()) {
                    dto.to().add(communicationFacade.generateEndpointDTO(t));
                }

            }
        }

        return result;

    }

    @Override
    protected boolean persist(CommunicationThread bo, CommunicationThreadDTO to) {

        Message message = EntityFactory.create(Message.class);
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        message.attachments().set(to.representingMessage().attachments());
        message.date().setValue(SystemDateManager.getDate());
        message.sender().set(CrmAppContext.getCurrentUserEmployee());
        message.onBehalf().set(to.representingMessage().onBehalf());
        message.onBehalfVisible().set(to.representingMessage().onBehalfVisible());
        message.content().set(to.representingMessage().content());
        message.highImportance().set(to.highImportance());
        communicationFacade.buildRecipientList(message, to.representingMessage(), null);

        if (to.category().isValueDetached()) {
            Persistence.service().retrieve(to.category());
        }

        boolean isTicket = CategoryType.Ticket.equals(to.category().categoryType().getValue());

        if (isTicket) {
            message.recipients().add(
                    communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), true));
        }

        //to.subject().set(to.subject());
        if (to.deliveryMethod() != null && !to.deliveryMethod().isNull()) {
            SpecialDelivery da = null;
            switch (to.deliveryMethod().getValue()) {
            case SMS:
                da = EntityFactory.create(SMSDelivery.class);
                if (to.deliveredText() != null && !to.deliveredText().isNull()) {
                    bo.subject().setValue(to.deliveredText().getValue().substring(0, Math.min(77, to.deliveredText().getValue().length())));
                }
                break;
            case IVR:
                da = EntityFactory.create(IVRDelivery.class);
                if (to.deliveredText() != null && !to.deliveredText().isNull()) {
                    bo.subject().setValue(to.deliveredText().getValue().substring(0, Math.min(77, to.deliveredText().getValue().length())));
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
            bo.deliveryMethod().set(to.deliveryMethod());
            bo.specialDelivery().set(da);
        }
        bo.allowedReply().set(to.allowedReply());
        bo.category().set(to.category());
        if (isTicket) {
            bo.status().setValue(ThreadStatus.Open);
        }
        bo.content().add(message);
        bo.owner().set(
                to.owner() == null || to.owner().isEmpty() || to.owner().isPrototype() || to.owner().isNull() ? communicationFacade
                        .getSystemEndpointFromCache(SystemEndpointName.Unassigned) : to.owner());

        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(CommunicationThread bo, CommunicationThreadDTO to, RetrieveTarget retrieveTarget) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceThreadDbo(bo, to, false, CrmAppContext.getCurrentUserEmployee());
    }

    @Override
    protected void enhanceListRetrieved(CommunicationThread bo, CommunicationThreadDTO to) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceThreadDbo(bo, to, true, CrmAppContext.getCurrentUserEmployee());
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationThreadDTO> callback, MessageDTO message, ThreadStatus threadStatus) {
        saveAndUpdate(callback, message, threadStatus, true);
    }

    private void saveAndUpdate(AsyncCallback<CommunicationThreadDTO> callback, MessageDTO message, ThreadStatus threadStatus, boolean updateOwner) {
        Employee currentUser = CrmAppContext.getCurrentUserEmployee();
        if (message.date().isNull()) {
            CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
            if (threadStatus != null && message.thread().status().getValue() != null) {
                String notification = i18n.tr("Status was changed from") + " '" + message.thread().status().getValue().toString() + "' " + i18n.tr("to") + " '"
                        + threadStatus.toString() + "'.\r\nReason: \r\n";
                message.content().setValue(notification + message.content().getValue(""));
            }
            Message m = communicationFacade.saveMessage(message, threadStatus, currentUser, updateOwner);
            retrieve(callback, message.thread().getPrimaryKey(), RetrieveTarget.View);
        } else {
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), currentUser);
            dhCriteria.eq(dhCriteria.proto().message(), message);
            DeliveryHandle dh = Persistence.retrieveUnique(dhCriteria, AttachLevel.Attached);
            dh.isRead().set(message.isRead());
            dh.star().set(message.star());
            Persistence.service().persist(dh);
            Persistence.service().commit();
        }
        retrieve(callback, message.thread().getPrimaryKey(), RetrieveTarget.View);
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
    }

    @Override
    public void assignOwnership(AsyncCallback<CommunicationThreadDTO> callback, CommunicationThreadDTO message, String additionalComment, IEntity employee) {
        CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.id().getValue());
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        CommunicationEndpointDTO prevOwner = null;
        if (thread.owner() != null && !thread.owner().isNull()) {
            Persistence.ensureRetrieve(thread.owner(), AttachLevel.Attached);
            prevOwner = communicationFacade.generateEndpointDTO(thread.owner());
        }
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

        message.owner().set(e == null ? null : e);

        message.status().set(thread.status());
        if (!CrmAppContext.getCurrentUserEmployee().equals(employee)) {
            MessageDTO dto = EntityFactory.create(MessageDTO.class);
            String systemNotification = i18n.tr("Ticket owner was changed to") + ": " + message.owner().name().getStringView() + ". ";
            dto.content().setValue(additionalComment == null ? systemNotification : systemNotification + additionalComment);
            dto.thread().set(thread);
            dto.isSystem().setValue(true);
            if (prevOwner != null && ContactType.Employee.equals(prevOwner.type().getValue())) {
                dto.to().add(prevOwner);
            }
            if (e != null || dto.to().size() < 1) {
                dto.to().add(communicationFacade.generateEndpointDTO(message.owner()));
            }
            saveAndUpdate(callback, dto, null, false);
        } else {
            ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
            retrieve(callback, message.getPrimaryKey(), RetrieveTarget.View);
        }
    }

    @Override
    public void hideUnhide(AsyncCallback<CommunicationThreadDTO> callback, CommunicationThreadDTO source) {

        Employee currentUser = CrmAppContext.getCurrentUserEmployee();
        EntityQueryCriteria<ThreadPolicyHandle> policyCriteria = EntityQueryCriteria.create(ThreadPolicyHandle.class);

        policyCriteria.add(PropertyCriterion.in(policyCriteria.proto().thread(), source));
        policyCriteria.eq(policyCriteria.proto().policyConsumer(), currentUser);

        ThreadPolicyHandle handle = Persistence.secureRetrieve(policyCriteria);
        if (handle == null) {
            handle = EntityFactory.create(ThreadPolicyHandle.class);
            handle.policyConsumer().set(currentUser);
            handle.thread().set(source);
            handle.hidden().setValue(true);
        } else {
            handle.hidden().setValue(!handle.hidden().getValue(false));
        }

        Persistence.service().persist(handle);
        Persistence.service().commit();
        retrieve(callback, source.getPrimaryKey(), RetrieveTarget.View);
    }
}