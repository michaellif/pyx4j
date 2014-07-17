/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 17, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.CommunicationBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.CommunicationFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.MessageAttachmentUploadService;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.crm.server.security.access.MessageAccessRule;
import com.propertyvista.crm.server.security.access.MessageCategoryAccessRule;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.dto.MessageDTO;

class VistaCrmCommunicationAccessControlList extends UIAclBuilder {

    VistaCrmCommunicationAccessControlList() {
        grant(CommunicationBasic, new IServiceExecutePermission(MessageCategoryCrudService.class));

        grant(CommunicationBasic, new MessageCategoryAccessRule(), MessageCategory.class);
        grant(CommunicationBasic, new EntityPermission(SystemEndpoint.class, READ));
        grant(CommunicationBasic, new IServiceExecutePermission(MessageCrudService.class));
        grant(CommunicationBasic, new MessageAccessRule(), Message.class);
        grant(CommunicationBasic, new EntityPermission(Message.class, ALL));
        grant(CommunicationBasic, new EntityPermission(MessageAttachment.class, ALL));
        grant(CommunicationBasic, new EntityPermission(CommunicationThread.class, ALL));
        grant(CommunicationBasic, new IServiceExecutePermission(MessageAttachmentUploadService.class));
        grant(CommunicationBasic, new IServiceExecutePermission(SelectCommunicationEndpointListService.class));

        grant(CommunicationBasic, MessageCategory.class, READ);
        grant(CommunicationBasic, MessageDTO.class, ALL);

        grant(CommunicationFull, new EntityPermission(MessageCategory.class, ALL));
        grant(CommunicationFull, MessageCategory.class, ALL);
    }
}
