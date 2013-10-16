/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-21
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationPerson;
import com.propertyvista.domain.communication.CommunicationPerson.PersonType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;

public class CommunicationDevPreloader extends AbstractDataPreloader {

    private static final Logger log = LoggerFactory.getLogger(CommunicationDevPreloader.class);

    @Override
    public String create() {

//      Default logins:
//      CRM login as PM: m001@pyx4j.com
//      CRM login as EMP: e001@pyx4j.com
//      Resident login: t001@pyx4j.com, t002@pyx4j.com 

        EntityQueryCriteria<CrmUser> criteriaCrmUser = EntityQueryCriteria.create(CrmUser.class);
        List<CrmUser> listCrmUsers = Persistence.service().query(criteriaCrmUser);
        log.info("There are {} crm usres ", listCrmUsers.size());

        // get a few existing CrmUsers:
        CrmUser m001 = null;
        CrmUser e001 = null;
        if (listCrmUsers != null) {
            String email;
            for (CrmUser crmUser : listCrmUsers) {
                email = crmUser.email().getStringView();
                if ("m001@pyx4j.com".equals(email)) {
                    m001 = crmUser;
                } else if ("e001@pyx4j.com".equals(email)) {
                    e001 = crmUser;
                }
            }
        }

        EntityQueryCriteria<CustomerUser> criteriaCustomerUser = EntityQueryCriteria.create(CustomerUser.class);
        List<CustomerUser> listCustomerUser = Persistence.service().query(criteriaCustomerUser);
        log.info("\n\nThere are {} customer users", listCustomerUser.size());

        // get a few existing CustomerUsers
        CustomerUser t001 = null;
        CustomerUser t002 = null;
        CustomerUser t003 = null;
        if (listCustomerUser != null) {
            String email;
            for (CustomerUser customerUser : listCustomerUser) {
                email = customerUser.email().getStringView();
                if ("t001@pyx4j.com".equals(email)) {
                    t001 = customerUser;
                } else if ("t002@pyx4j.com".equals(email)) {
                    t002 = customerUser;
                } else if ("t003@pyx4j.com".equals(email)) {
                    t003 = customerUser;
                }
            }
        }

        // create this persons in communication module
        CommunicationPerson personM001 = createPerson(m001);
        CommunicationPerson personE001 = createPerson(e001);
        CommunicationPerson personT001 = createPerson(t001);
        CommunicationPerson personT002 = createPerson(t002);
        CommunicationPerson personT003 = createPerson(t003);

        if (personM001 == null || personE001 == null || personT001 == null || personT002 == null) {
            return "Couldn't create the desired CommunicationPersons!";
        }

        // message flow:
        // m001 -> t001 (read, important)
        // t001 -> m001 (reply unread)
        // t002 -> e001 (unread, important)
        // m001 -> e001 (unread)
        // TODO: add favorites messages too
        // t001-> t002  (unread)
        // t001-> t003  (read, important)
        // t003-> t001  (unread, important) - reply

        //(CommunicationPerson from, CommunicationPerson to, CommunicationMessage parent, String topic, String msgContent,boolean highImportance, boolean isRead)
        CommunicationMessage msg1 = createMessage(personM001, personT001, null, "No water",
                "It will no water in the whole year! Please go to a Sea sand bring water!", true, true);

        if (msg1 == null) {
            return "Couldn't create the first CommunicationMessage!";
        }

        CommunicationMessage msg2 = createMessage(personT001, personM001, msg1, "No water",
                "Hi,\n     Thanks for communication, I went to Black Sea to become White :)", true, false);
        if (msg2 == null) {
            return "Couldn't create the second CommunicationMessage!";
        }
        CommunicationMessage msg3 = createMessage(personT002, personE001, null, "How to use this?",
                "Hey,\n    I would like to know how to use this portal thing, stuff here, is there a quick tutorial?", true, false);
        if (msg3 == null) {
            return "Couldn't create the third CommunicationMessage!";
        }
        CommunicationMessage msg4 = createMessage(personM001, personE001, null, "We miss you",
                "Please come back from hollyday we miss you, mostly because there is to mucjh work for us.", false, false);
        if (msg4 == null) {
            return "Couldn't create the forth CommunicationMessage!";
        }
        CommunicationMessage msg5 = createMessage(personT001, personT002, null, "Happy New Year", "We wish you Happy New Year, all best", false, false);
        if (msg5 == null) {
            return "Couldn't create the fifth CommunicationMessage!";
        }
        CommunicationMessage msg6 = createMessage(personT001, personT003, null, "Late payment notification",
                "Dear Kenneth Puent,\nWe inform you are late on payment. Please play it ASAP!\n\nRegards,\n", true, true);
        if (msg6 == null) {
            return "Couldn't create the sixth CommunicationMessage!";
        }
        CommunicationMessage msg7 = createMessage(personT003, personT001, msg6, "Late payment notification",
                "Dear Veronica W Canoy,\nThanks for reminder. My payment it will be delaying one more week, until that please accept my dinner invitation!:)",
                true, false);
        if (msg7 == null) {
            return "Couldn't create the seventh CommunicationMessage!";
        }

        return "persons and messages created";
    }

    @Override
    public String delete() {
        return deleteAll(CommunicationPerson.class);
    }

    private CommunicationPerson createPerson(AbstractPmcUser user) {
        if (user == null) {
            return null;
        }
        CommunicationPerson person = null;
        person = EntityFactory.create(CommunicationPerson.class);
        if (user instanceof CustomerUser) {
            person.type().setValue(PersonType.CustomerUser);
        } else if (user instanceof CrmUser) {
            person.type().setValue(PersonType.CrmUser);
        } else {
            throw new RuntimeException("Not allowed to create the user type: " + user.getClass().getName());
        }
        person.userId().setValue(user.getPrimaryKey().asLong());
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
}
