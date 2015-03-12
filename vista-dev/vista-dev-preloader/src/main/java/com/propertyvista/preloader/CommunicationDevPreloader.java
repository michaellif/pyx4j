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
 */
package com.propertyvista.preloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.lease.Tenant;

public class CommunicationDevPreloader extends AbstractDataPreloader {

    private static final Logger log = LoggerFactory.getLogger(CommunicationDevPreloader.class);

    @Override
    public String create() {

//      Default logins:
//      CRM login as PM: m001@pyx4j.com
//      CRM login as EMP: e001@pyx4j.com
//      Resident login: t001@pyx4j.com, t002@pyx4j.com

        EntityQueryCriteria<Employee> criteriaEmployee = EntityQueryCriteria.create(Employee.class);
        List<Employee> listEmployees = Persistence.service().query(criteriaEmployee);
        log.info("There are {} crm usres ", listEmployees.size());

        // get a few existing CrmUsers:
        Employee m001 = null;
        Employee e001 = null;
        if (listEmployees != null) {
            String email;
            for (Employee crmUser : listEmployees) {
                email = crmUser.email().getStringView();
                if ("m001@pyx4j.com".equals(email)) {
                    m001 = crmUser;
                } else if ("e001@pyx4j.com".equals(email)) {
                    e001 = crmUser;
                }
            }
        }

        EntityQueryCriteria<Tenant> criteriaTenant = EntityQueryCriteria.create(Tenant.class);
        List<Tenant> listTenants = Persistence.service().query(criteriaTenant);
        log.info("\n\nThere are {} customer users", listTenants.size());

        // get a few existing CustomerUsers
        Tenant t001 = null;
        Tenant t002 = null;
        Tenant t003 = null;
        // Potential tenants
        List<Tenant> pTenants = new ArrayList<Tenant>();

        if (listTenants != null) {
            String email;
            for (Tenant customerUser : listTenants) {
                email = customerUser.customer().person().email().getStringView();
                if ("t001@pyx4j.com".equals(email)) {
                    t001 = customerUser;
                } else if ("t002@pyx4j.com".equals(email)) {
                    t002 = customerUser;
                } else if ("t003@pyx4j.com".equals(email)) {
                    t003 = customerUser;
                } else if (email.matches("p\\d{3}@propertyvista.com")) {
                    pTenants.add(customerUser);
                }
            }
        }

        EntityQueryCriteria<MessageCategory> criteriaMessageGroup = EntityQueryCriteria.create(MessageCategory.class);
        criteriaMessageGroup.eq(criteriaMessageGroup.proto().categoryType(), CategoryType.Message);
        MessageCategory mg = Persistence.service().retrieve(criteriaMessageGroup);

        EntityQueryCriteria<SystemEndpoint> criteriaSystemEndpoint = EntityQueryCriteria.create(SystemEndpoint.class);
        criteriaSystemEndpoint.eq(criteriaSystemEndpoint.proto().name(), SystemEndpoint.SystemEndpointName.Unassigned.toString());
        SystemEndpoint sep = Persistence.service().retrieve(criteriaSystemEndpoint);

        // message flow:
        // m001 -> t001 (read, important)
        // t001 -> m001 (reply unread)
        // t002 -> e001 (unread, important)
        // m001 -> e001 (unread)
        // TODO: add favorites messages too
        // t001-> t002  (unread)
        // t001-> t003  (read, important)
        // t003-> t001  (unread, important) - reply

        for (int i = 0; i < 1; i++) {
            CommunicationThread msg1 = createMessage(i, m001, t001, null, "No Water",
                    "It will no water in the whole year! Please go to a Sea sand bring water!", true, true, mg, sep);
            createMessage(i, t003, m001, msg1, "T03", "Hi,\n     Thanks for communication :)", true, false, mg, sep);
            createMessage(i, t003, m001, null, "T03", "Hi,\n     Bye)", true, false, mg, sep);
            createMessage(i, t001, m001, msg1, "No water", "Hi,\n     Thanks for communication, I went to Black Sea to become White :)", true, false, mg, sep);
            createMessage(i, t002, e001, null, "How to use this?",
                    "Hey,\n    I would like to know how to use this portal thing, stuff here, is there a quick tutorial?", true, false, mg, sep);
            createMessage(i, m001, e001, null, "We miss you", "Please come back from hollyday we miss you, mostly because there is to mucjh work for us.",
                    false, false, mg, sep);
            createMessage(i, m001, t002, null, "Happy New Year", "We wish you Happy New Year, all best", false, false, mg, sep);
            CommunicationThread msg6 = createMessage(i, m001, t001, null, "Late payment notification",
                    "Dear Kenneth Puent,\nWe inform you are late on payment. Please play it ASAP!\n\nRegards,\n", true, false, mg, sep);
            createMessage(
                    i,
                    t001,
                    m001,
                    msg6,
                    "Late payment notification",
                    "Dear Veronica W Canoy,\nThanks for reminder. My payment it will be delaying one more week, until that please accept my dinner invitation!:)",
                    true, false, mg, sep);

            // Welcome message to potential tenants
            for (Tenant potentialTenant : pTenants) {
                createMessage(i, m001, potentialTenant, null, "Welcome",
                        "We look forward to having you move to our building. If you have any questions, please do not hesitate to contact us", false, false,
                        mg, sep);
            }
        }
        return "persons and messages created";
    }

    @Override
    public String delete() {
        deleteAll(DeliveryHandle.class);
        return deleteAll(CommunicationThread.class);
    }

    private CommunicationThread createMessage(int i, CommunicationEndpoint from, CommunicationEndpoint to, CommunicationThread parent, String topic,
            String msgContent, boolean highImportance, boolean isRead, MessageCategory mg, SystemEndpoint owner) {

        if (from == null || to == null || topic == null || msgContent == null) {
            return null;
        }

        CommunicationThread thread = null;
        if (parent == null) {
            thread = EntityFactory.create(CommunicationThread.class);
            thread.subject().setValue(Integer.toString(i) + "_" + topic);
            thread.owner().set(owner);
            thread.allowedReply().setValue(true);
            thread.category().set(mg);

            PersistenceServicesFactory.getPersistenceService().persist(thread);

        } else {
            thread = parent;
        }

        Message c = EntityFactory.create(Message.class);
        c.sender().set(from);
        c.content().setValue(msgContent);
        c.date().setValue(new Date());
        c.highImportance().setValue(false);

        DeliveryHandle m = EntityFactory.create(DeliveryHandle.class);
        m.recipient().set(to);
        m.isRead().setValue(isRead);
        m.star().setValue(false);

        c.recipients().add(m);
        thread.content().add(c);
        PersistenceServicesFactory.getPersistenceService().persist(thread);

        return thread;
    }
}
