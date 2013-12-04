/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.mock.updater;

import com.propertyvista.test.mock.MockEvent;

public class RentableItemTypeUpdateEvent extends MockEvent<RentableItemTypeUpdateEvent.Handler> {

    private final RentableItemTypeUpdater updater;

    public interface Handler {

        void addOrUpdateRentableItemType(RentableItemTypeUpdateEvent event);

        void removeRentableItemType(RentableItemTypeUpdateEvent event);

    }

    public RentableItemTypeUpdateEvent(RentableItemTypeUpdater updater) {
        super();
        this.updater = updater;
    }

    @Override
    protected final void dispatch(Handler handler) {
        if (updater.remove) {
            handler.removeRentableItemType(this);
        } else {
            handler.addOrUpdateRentableItemType(this);
        }
    }

    public RentableItemTypeUpdater getUpdater() {
        return updater;
    }

}
