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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;
import com.propertyvista.portal.server.ptapp.util.Converter;
import com.propertyvista.server.common.security.VistaContext;

public class CommunicationCenterServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationMessage, CommunicationCenterDTO> implements
        CommunicationCenterService {

    public CommunicationCenterServiceImpl() {
        super(CommunicationMessage.class, CommunicationCenterDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void listMyMessages(AsyncCallback<Vector<CommunicationCenterDTO>> callback) {
        if (callback == null) {
            return;
        }

        Vector<CommunicationCenterDTO> dtoList = new Vector<CommunicationCenterDTO>();

        List<CommunicationMessage> myMessages = getMyMessages();
        if (myMessages != null) {
            for (CommunicationMessage myMessage : myMessages) {
                //TODO add favorites
                CommunicationCenterDTO dto = Converter.convert(myMessage);
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
                return (int) (msg1.created().getValue().getTime() - msg2.created().getValue().getTime());
            }
        });

        return allMessages;

    }

}
