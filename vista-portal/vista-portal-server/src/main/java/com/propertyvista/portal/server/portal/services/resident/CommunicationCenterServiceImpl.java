/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationPerson;
import com.propertyvista.domain.communication.CommunicationPerson.PersonType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;
import com.propertyvista.portal.server.ptapp.util.Converter;
import com.propertyvista.server.common.security.VistaContext;

public class CommunicationCenterServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationMessage, CommunicationCenterDTO> implements
        CommunicationCenterService {

    private final Logger log = LoggerFactory.getLogger(CommunicationCenterService.class);

    public CommunicationCenterServiceImpl() {
        super(CommunicationMessage.class, CommunicationCenterDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void listMyMessages(AsyncCallback<Vector<CommunicationCenterDTO>> callback) {
        if (callback == null) {
            return;
        }

        Vector<CommunicationCenterDTO> dtoList = new Vector<CommunicationCenterDTO>();

        List<CommunicationMessage> myMessages = getMyMessages();
        if (myMessages != null) {
            //collect all senders and get it once from database:
            ArrayList<CommunicationPerson> senders = new ArrayList<CommunicationPerson>(myMessages.size());
            for (CommunicationMessage myMessage : myMessages) {
                senders.add(myMessage.sender());
            }

            LinkedHashMap<Long, ArrayList<Object>> sendersById = getSendersById(senders);

            for (CommunicationMessage myMessage : myMessages) {
                Long senderUserId = myMessage.sender().userId().getValue();
                ArrayList<Object> sendersSystemData = sendersById.get(senderUserId);
                String senderName = null;
                Object systemSender = null;
                if (sendersSystemData.size() == 1) {
                    systemSender = sendersSystemData.get(0);
                } else {
                    for (Object curSystemSender : sendersSystemData) {
                        if (myMessage.sender().type().getValue() == CommunicationPerson.PersonType.CrmUser //
                                && curSystemSender.getClass().equals(CrmUser.class)) {
                            systemSender = curSystemSender;
                            break;

                        } else if (myMessage.sender().type().getValue() == CommunicationPerson.PersonType.CustomerUser //
                                && curSystemSender.getClass().equals(CustomerUser.class)) {
                            systemSender = curSystemSender;
                            break;
                        } else {
                            log.error("Don't know the curSystemSender class: {0}", curSystemSender.getClass().getName());
                        }
                    }
                }
                if (systemSender instanceof AbstractPmcUser) {
                    AbstractPmcUser abstractPmcUser = (AbstractPmcUser) systemSender;
                    senderName = abstractPmcUser.name().getValue();
                } else {
                    log.error("Don't know the systemSender class: {0}", systemSender.getClass().getName());
                }
                //TODO add favorites
                CommunicationCenterDTO dto = Converter.convert(myMessage, senderName);
                dtoList.add(dto);
            }
        }

        callback.onSuccess(dtoList);
    }

    private List<CommunicationMessage> getMyMessages() {

        AbstractUser loginedUser = VistaContext.getCurrentUser();// can throw UnRecoverableRuntimeException, if no logined user
        long loginedUserId = loginedUser.id().getValue().asLong();//190427 ( t001@...

        EntityQueryCriteria<CommunicationMessage> criteria = EntityQueryCriteria.create(CommunicationMessage.class);
        PropertyCriterion criterionSender = PropertyCriterion.eq(criteria.proto().sender().userId(), loginedUserId);
        PropertyCriterion criterionDestination = PropertyCriterion.eq(criteria.proto().destination().userId(), loginedUserId);
        criteria = criteria.or(criterionSender, criterionDestination);

        List<CommunicationMessage> allMessages = Persistence.service().query(criteria);

        Collections.sort(allMessages, new Comparator<CommunicationMessage>() {
            @Override
            public int compare(CommunicationMessage msg1, CommunicationMessage msg2) {
                return (int) (msg2.created().getValue().getTime() - msg1.created().getValue().getTime());
            }
        });

        return allMessages;
    }

    /**
     * @param senders
     * @return from multiple tables we can have the same id,that's why ArrayList<Object> is the value, the key is the id
     */
    private LinkedHashMap<Long, ArrayList<Object>> getSendersById(ArrayList<CommunicationPerson> senders) {
        LinkedHashMap<Long, ArrayList<Object>> result = new LinkedHashMap<Long, ArrayList<Object>>();
        if (senders == null || senders.size() == 0) {
            return result;
        }

        ArrayList<Long> sendersCrmUserIds = new ArrayList<Long>();
        ArrayList<Long> sendersCustomerUserIds = new ArrayList<Long>();

        for (CommunicationPerson sender : senders) {
            if (CommunicationPerson.PersonType.CrmUser == sender.type().getValue()) {
                sendersCrmUserIds.add(sender.userId().getValue());
            } else if (CommunicationPerson.PersonType.CustomerUser == sender.type().getValue()) {
                sendersCustomerUserIds.add(sender.userId().getValue());
            } else {
                log.warn("Unknown sender type {0}, please update it ", sender.type().getValue().toString());
            }
        }
        if (sendersCrmUserIds.size() > 0) {
            EntityQueryCriteria<CrmUser> criteria = EntityQueryCriteria.create(CrmUser.class);
            PropertyCriterion criterionId = PropertyCriterion.in(criteria.proto().id(), sendersCrmUserIds);
            criteria.add(criterionId);
            List<CrmUser> allCrmUsers = Persistence.service().query(criteria);
            for (CrmUser crmUser : allCrmUsers) {
                Long id = crmUser.id().getValue().asLong();
                ArrayList<Object> values = result.get(id);
                if (values == null) {
                    values = new ArrayList<Object>(0);
                }
                values.add(crmUser);
                result.put(id, values);
            }
        }
        if (sendersCustomerUserIds.size() > 0) {
            EntityQueryCriteria<CustomerUser> criteria = EntityQueryCriteria.create(CustomerUser.class);
            PropertyCriterion criterionId = PropertyCriterion.in(criteria.proto().id(), sendersCustomerUserIds);
            criteria.add(criterionId);
            List<CustomerUser> allCustomerUsers = Persistence.service().query(criteria);
            for (CustomerUser customerUser : allCustomerUsers) {
                Long id = customerUser.id().getValue().asLong();
                ArrayList<Object> values = result.get(id);
                if (values == null) {
                    values = new ArrayList<Object>(0);
                }
                values.add(customerUser);
                result.put(id, values);
            }
        }

        return result;
    }

    @Override
    public void createAndSendMessage(AsyncCallback<VoidSerializable> callback, String topic, String messageContent, boolean isHighImportance,
            AbstractUser[] destinations) {

        AbstractUser loginedUser = VistaContext.getCurrentUser();// can throw UnRecoverableRuntimeException, if no logined user

        CommunicationPerson loginedPerson = getPersonForUser(loginedUser);// this will be the sender of the message
        if (loginedPerson == null) {
            loginedPerson = createPersonForUser(loginedUser);
        }
        if (destinations == null) {//for a to fast demo: send a message to self...
            CommunicationPerson personTo = loginedPerson;
            createMessage(loginedPerson, personTo, null, topic, messageContent, isHighImportance, false);
        } else {
            for (AbstractUser destination : destinations) {
                CommunicationPerson personTo = getPersonForUser(destination);
                if (personTo == null) {
                    personTo = createPersonForUser(destination);
                }
                createMessage(loginedPerson, personTo, null, topic, messageContent, isHighImportance, false);
            }
        }

        PersistenceServicesFactory.getPersistenceService().commit();

        callback.onSuccess(null);
    }

    // helper methods:

    private CommunicationPerson getPersonForUser(AbstractUser user) {
        if (user == null) {
            return null;
        }

        long loginedUserId = user.id().getValue().asLong();

        CommunicationPerson loginedPerson = null;
//TODO check which type is he
        EntityQueryCriteria<CommunicationPerson> criteriaPerson = EntityQueryCriteria.create(CommunicationPerson.class);
        List<CommunicationPerson> listCommunicationPerson = Persistence.service().query(criteriaPerson);
        for (CommunicationPerson person : listCommunicationPerson) {
            if (person.userId().getValue().longValue() == loginedUserId) {
                loginedPerson = person;
                break;
            }
        }

        return loginedPerson;
    }

    private CommunicationPerson createPersonForUser(AbstractUser loginedUser) {
        if (loginedUser == null) {
            return null;
        }

        //long loginedUserId = loginedUser.id().getValue().asLong();
        CommunicationPerson person = EntityFactory.create(CommunicationPerson.class);
        if (loginedUser instanceof CustomerUser) {//mostly this will be
            person.type().setValue(PersonType.CustomerUser);
        } else if (loginedUser instanceof CrmUser) {
            person.type().setValue(PersonType.CrmUser);
        } else {
            throw new RuntimeException("Not allowed to create the user type: " + loginedUser.getClass().getName());
        }
        person.userId().setValue(loginedUser.getPrimaryKey().asLong());
        PersistenceServicesFactory.getPersistenceService().persist(person);

        return person;

    }

    private CommunicationMessage createMessage(CommunicationPerson from, CommunicationPerson to, CommunicationMessage parent, String topic, String msgContent,
            boolean highImportance, boolean isRead) {
        if (from == null || to == null || topic == null || msgContent == null) {
            return null;
        }

        CommunicationMessage msg = EntityFactory.create(CommunicationMessage.class);

        msg.parent().set(parent);
        msg.sender().set(from);
        msg.destination().set(to);
        msg.topic().setValue(topic);
        msg.content().setValue(msgContent);
        msg.isHighImportance().setValue(highImportance);
        msg.isRead().setValue(isRead);

        PersistenceServicesFactory.getPersistenceService().persist(msg);

        return msg;
    }

    @Override
    public void sendReply(AsyncCallback<VoidSerializable> callback, String topic, String messageContent, boolean isHighImportance,
            CommunicationCenterDTO parentMessage) {

        AbstractUser loginedUser = VistaContext.getCurrentUser();// can throw UnRecoverableRuntimeException, if no logined user

        CommunicationPerson loginedPerson = getPersonForUser(loginedUser);// this will be the sender of the message
        if (loginedPerson == null) {
            loginedPerson = createPersonForUser(loginedUser);//shouldn't be this case, ever
        }

        CommunicationPerson personTo = loginedPerson;// a not null value
        if (parentMessage != null) {
            personTo = parentMessage.sender();
        }
        createMessage(loginedPerson, personTo, parentMessage, topic, messageContent, isHighImportance, false);

        PersistenceServicesFactory.getPersistenceService().commit();

        callback.onSuccess(null);
    }

}
