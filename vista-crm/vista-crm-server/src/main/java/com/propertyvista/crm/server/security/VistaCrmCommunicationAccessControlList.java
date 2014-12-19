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
 */
package com.propertyvista.crm.server.security;

import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.MessageAttachmentUploadService;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.rpc.services.selections.SelectCommunicationEndpointListService;
import com.propertyvista.crm.server.security.access.CommunicationThreadAccessRule;
import com.propertyvista.crm.server.security.access.MessageAccessRule;
import com.propertyvista.crm.server.security.access.MessageCategoryAccessRule;
import com.propertyvista.crm.server.security.access.ThreadPolicyHandleAccessRule;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.IVRDelivery;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.NotificationDelivery;
import com.propertyvista.domain.communication.SMSDelivery;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.ThreadPolicyHandle;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.dto.MessageDTO;

class VistaCrmCommunicationAccessControlList extends UIAclBuilder {

    VistaCrmCommunicationAccessControlList() {
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(MessageCategoryCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new MessageCategoryAccessRule(), MessageCategory.class);
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(SystemEndpoint.class, READ));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(MessageCrudService.class));
        grant(VistaAccessGrantedBehavior.CRM, new MessageAccessRule(), Message.class);
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(Message.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(MessageAttachment.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(CommunicationThread.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(ThreadPolicyHandle.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(NotificationDelivery.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(SMSDelivery.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new EntityPermission(IVRDelivery.class, ALL));
        grant(VistaAccessGrantedBehavior.CRM, new CommunicationThreadAccessRule(), CommunicationThread.class);
        grant(VistaAccessGrantedBehavior.CRM, new ThreadPolicyHandleAccessRule(), ThreadPolicyHandle.class);
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(MessageAttachmentUploadService.class));
        grant(VistaAccessGrantedBehavior.CRM, new IServiceExecutePermission(SelectCommunicationEndpointListService.class));

        grant(VistaAccessGrantedBehavior.CRM, MessageCategory.class, READ);
        grant(VistaAccessGrantedBehavior.CRM, MessageDTO.class, ALL);

        // Administration is granted in VistaCrmAdministrationAccessControlList
    }
}
