/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 */
package com.propertyvista.crm.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.entity.core.IEntity;

public class BoardUpdateEvent extends GwtEvent<BoardUpdateHandler> {

    private static Type<BoardUpdateHandler> TYPE;

    private final Class<? extends IEntity> targetEntityType;

    public BoardUpdateEvent(Class<? extends IEntity> targetEntityType) {
        this.targetEntityType = targetEntityType;
    }

    public Class<? extends IEntity> getTargetEntityType() {
        return targetEntityType;
    }

    public static Type<BoardUpdateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<BoardUpdateHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<BoardUpdateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BoardUpdateHandler handler) {
        handler.onBoardUpdate(this);
    }
}