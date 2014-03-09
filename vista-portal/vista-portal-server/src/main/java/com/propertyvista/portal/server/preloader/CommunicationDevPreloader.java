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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationThread;
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
        CommunicationThread msg1 = createMessage(m001, t001, null, "No Water", "It will no water in the whole year! Please go to a Sea sand bring water!",
                true, true);
        CommunicationThread msg2 = createMessage(t001, m001, msg1, "No water", "Hi,\n     Thanks for communication, I went to Black Sea to become White :)",
                true, false);
        CommunicationThread msg3 = createMessage(t002, e001, null, "How to use this?",
                "Hey,\n    I would like to know how to use this portal thing, stuff here, is there a quick tutorial?", true, false);
        CommunicationThread msg4 = createMessage(m001, e001, null, "We miss you",
                "Please come back from hollyday we miss you, mostly because there is to mucjh work for us.", false, false);
        CommunicationThread msg5 = createMessage(m001, t002, null, "Happy New Year", "We wish you Happy New Year, all best", false, false);
        CommunicationThread msg6 = createMessage(m001, t001, null, "Late payment notification",
                "Dear Kenneth Puent,\nWe inform you are late on payment. Please play it ASAP!\n\nRegards,\n", true, false);
        CommunicationThread msg7 = createMessage(t001, m001, msg6, "Late payment notification",
                "Dear Veronica W Canoy,\nThanks for reminder. My payment it will be delaying one more week, until that please accept my dinner invitation!:)",
                true, false);
        return "persons and messages created";
    }

    @Override
    public String delete() {
        deleteAll(CommunicationMessage.class);
        return deleteAll(CommunicationThread.class);
    }

    private CommunicationThread createMessage(AbstractPmcUser from, AbstractPmcUser to, CommunicationThread parent, String topic, String msgContent,
            boolean highImportance, boolean isRead) {
        if (from == null || to == null || topic == null || msgContent == null) {
            return null;
        }

        CommunicationThread thread = null;
        if (parent == null) {
            thread = EntityFactory.create(CommunicationThread.class);
            thread.subject().setValue(topic);
            PersistenceServicesFactory.getPersistenceService().persist(thread);
        } else
            thread = parent;

        CommunicationMessage m = EntityFactory.create(CommunicationMessage.class);
        m.isHighImportance().setValue(highImportance);
        m.to().add(to);
        m.sender().set(from);
        m.text().setValue(msgContent);
        m.date().setValue(new Date());
        m.isRead().setValue(isRead);
        m.thread().set(thread);
        PersistenceServicesFactory.getPersistenceService().persist(m);
        thread.content().add(m);
        PersistenceServicesFactory.getPersistenceService().persist(thread);

        return thread;
    }
}
