/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2014
 * @author smolka
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.domain.communication.CommunicationAssociation;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestPriority.PriorityLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.communication.MessageDTO;

public class CommunicationAssociationManager {
    private final static I18n i18n = I18n.get(CommunicationAssociationManager.class);

    public static boolean association2Importance(CommunicationAssociation ca) {
        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                if (mr.priority() != null && mr.priority().level() != null) {
                    return PriorityLevel.EMERGENCY.equals(mr.priority().level().getValue()) ? true : false;
                }
            }
        }

        return false;
    }

    public static String association2Subject(CommunicationAssociation ca) {
        String result = ca.getStringView();
        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                result = mr.summary().getValue();
            }
        }

        if (result == null) {
            return i18n.tr("New") + " " + i18n.tr("Communication" + " " + i18n.tr("has been created."));
        }
        return result.length() > 77 ? result.substring(0, 77) : result;
    }

    public static String association2Body(CommunicationAssociation ca) {
        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                if (MaintenanceRequestStatus.StatusPhase.Resolved.equals(mr.status().phase().getValue())) {
                    return mr.resolution().getValue();
                }
                if (MaintenanceRequestStatus.StatusPhase.Cancelled.equals(mr.status().phase().getValue())) {
                    return i18n.tr("Maintenance Request") + " " + i18n.tr("has been cancelled.");
                }
                if (MaintenanceRequestStatus.StatusPhase.Scheduled.equals(mr.status().phase().getValue())) {
                    return i18n.tr("Maintenance Request") + " " + i18n.tr("has been scheduled.");
                }
                return mr.description().getValue();
            }
        }

        return i18n.tr("New") + " " + i18n.tr("Communication" + " " + i18n.tr("has been created."));
    }

    public static CommunicationThread.ThreadStatus association2Status(CommunicationAssociation ca) {
        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                if (MaintenanceRequestStatus.StatusPhase.Resolved.equals(mr.status().phase().getValue())) {
                    return CommunicationThread.ThreadStatus.Resolved;
                }
                if (MaintenanceRequestStatus.StatusPhase.Cancelled.equals(mr.status().phase().getValue())) {
                    return CommunicationThread.ThreadStatus.Resolved;
                }
            }
        }

        return CommunicationThread.ThreadStatus.Open;
    }

    public static CommunicationEndpoint association2Sender(CommunicationAssociation ca, CommunicationEndpoint currentUser) {
        CommunicationMessageFacade facade = ServerSideFactory.create(CommunicationMessageFacade.class);

        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                CommunicationEndpoint t = mr.reporter();
                if (currentUser == null && t != null && !t.isNull()) {
                    return t;
                }
            }
        }
        return currentUser == null ? facade.getSystemEndpointFromCache(SystemEndpointName.Unassigned) : currentUser;
    }

    public static void association2Recipient(Message message, CommunicationAssociation ca, CommunicationEndpoint currentUser) {
        CommunicationMessageFacade facade = ServerSideFactory.create(CommunicationMessageFacade.class);

        if (ca != null) {
            if (ca instanceof MaintenanceRequest) {
                MaintenanceRequest mr = (MaintenanceRequest) ca;
                CommunicationEndpoint t = mr.reporter();
                AptUnit unit = mr.unit();
                if (currentUser != null && currentUser instanceof Employee) {
                    if (unit != null && !unit.isNull() && MaintenanceRequestStatus.StatusPhase.Scheduled.equals(mr.status().phase().getValue())) {
                        facade.buildRecipientsList4UnitLeaseParticipants(message, unit, false);
                    } else if (t != null && !t.isNull()) {
                        message.recipients().add(facade.createDeliveryHandle(t, false));
                        if (MaintenanceRequestStatus.StatusPhase.Submitted.equals(mr.status().phase().getValue())) {
                            message.onBehalf().set(t);
                            message.onBehalfVisible().setValue(true);
                        }
                    }
                }
            }
        }

        if (message.recipients().size() < 1) {
            message.recipients().add(facade.createDeliveryHandle(facade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), false));
        }
    }

    public static Message association2Message(CommunicationAssociation ca) {
        if (ca != null) {
            EntityQueryCriteria<Message> messageCriteria = EntityQueryCriteria.create(Message.class);
            messageCriteria.eq(messageCriteria.proto().thread().associated(), ca);
            final List<Message> associatedMessage = Persistence.secureQuery(messageCriteria, AttachLevel.IdOnly);
            if (associatedMessage != null && associatedMessage.size() > 0) {
                Message result = associatedMessage.get(0);
                Persistence.ensureRetrieve(result.thread(), AttachLevel.Attached);
                Persistence.ensureRetrieve(result.thread().category(), AttachLevel.Attached);
                Persistence.ensureRetrieve(result.thread().owner(), AttachLevel.Attached);
                Persistence.ensureRetrieve(result.recipients(), AttachLevel.Attached);
                return result;
            }
        }
        return null;
    }

    public static CommunicationThread association2Thread(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody) {

        Message m = EntityFactory.create(Message.class);
        m.date().setValue(SystemDateManager.getDate());
        CommunicationMessageFacade facade = ServerSideFactory.create(CommunicationMessageFacade.class);
        m.highImportance().setValue(association2Importance(ca));
        m.sender().set(association2Sender(ca, currentUser));
        m.content().setValue(messageBody == null ? association2Body(ca) : messageBody);
        association2Recipient(m, ca, currentUser);

        CommunicationThread t = EntityFactory.create(CommunicationThread.class);
        t.subject().setValue(association2Subject(ca));
        t.allowedReply().setValue(true);
        t.status().setValue(ThreadStatus.Open);
        t.category().set(facade.getMessageCategoryFromCache(TicketType.Maintenance));
        t.content().add(m);
        t.owner().set(facade.getSystemEndpointFromCache(SystemEndpointName.Unassigned));
        t.associated().set(ca);
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        Persistence.secureSave(t);
        return t;
    }

    public static Message associationChange2Message(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody) {
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        if (ca != null) {
            Message m = association2Message(ca);
            if (m == null) {
                CommunicationThread t = association2Thread(ca, currentUser, null);
                m = t.content().get(0);
            }
            MessageDTO dto = EntityFactory.create(MessageDTO.class);
            dto.thread().set(m.thread());
            dto.date().setValue(SystemDateManager.getDate());
            dto.isRead().setValue(false);
            dto.highImportance().setValue(false);
            dto.content().setValue(messageBody == null ? association2Body(ca) : messageBody);
            Message newMessage = communicationFacade.saveMessage(dto, association2Status(ca), association2Sender(ca, currentUser), true);
            association2Recipient(newMessage, ca, currentUser);
            Persistence.service().persist(newMessage);
            return newMessage;
        }
        return null;
    }
}
