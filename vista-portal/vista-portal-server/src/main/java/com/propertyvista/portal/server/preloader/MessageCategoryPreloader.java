/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.security.CrmRole;

public class MessageCategoryPreloader extends AbstractDataPreloader {
    private final boolean isProduction;

    public MessageCategoryPreloader(boolean isProduction) {
        this.isProduction = isProduction;
    }

    @Override
    public String create() {

        createCategory(MessageGroupCategory.Ticket, TicketType.Tenant, TicketType.Tenant.toString(), CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.Ticket, TicketType.Landlord, TicketType.Landlord.toString(), CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.Ticket, TicketType.Vendor, TicketType.Vendor.toString(), CrmRolesPreloader.getDefaultRole());
        createCategory(MessageGroupCategory.Message, TicketType.NotTicket, "General Message", CrmRolesPreloader.getDefaultRole());
        if (!isProduction) {
            createCategory(MessageGroupCategory.IVR, TicketType.NotTicket, "General IVR", CrmRolesPreloader.getDefaultRole());
            createCategory(MessageGroupCategory.SMS, TicketType.NotTicket, "General SMS", CrmRolesPreloader.getDefaultRole());
            createCategory(MessageGroupCategory.Notification, TicketType.NotTicket, "General Notification", CrmRolesPreloader.getDefaultRole());
        }
        return null;
    }

    private void createCategory(MessageGroupCategory category, TicketType ticketType, String topic, CrmRole... defaultRole) {
        MessageCategory mg = EntityFactory.create(MessageCategory.class);
        mg.category().setValue(category);
        mg.ticketType().setValue(ticketType);
        mg.topic().setValue(topic);
        if (defaultRole != null && defaultRole.length > 0) {
            for (int i = 0; i < defaultRole.length; ++i)
                mg.roles().add(defaultRole[i]);
        }
        PersistenceServicesFactory.getPersistenceService().persist(mg);
    }

    @Override
    public String delete() {
        return null;
    }
}
