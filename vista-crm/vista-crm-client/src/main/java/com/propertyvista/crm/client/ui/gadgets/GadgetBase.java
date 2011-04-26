/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;

import com.pyx4j.dashboard.client.IGadget;

public abstract class GadgetBase implements IGadget {

    protected final GadgetMetadata gadgetMetadata;

    public GadgetBase(GadgetMetadata gmd) {
        super();
        gadgetMetadata = gmd;
    }

    // info:

    public GadgetMetadata getGadgetMetadata() {
        return gadgetMetadata;
    }

    /*
     * Implement in derived class to represent desired gadget UI.
     */
    @Override
    public abstract Widget getWidget();

    @Override
    public String getName() {
        return gadgetMetadata.name().getValue();
    }

    @Override
    public String getDescription() {
        return gadgetMetadata.description().getValue();
    }

    // runtime scope:
    @Override
    public void start() {
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void stop() {
    }

    // flags:

    @Override
    public boolean isMaximizable() {
        return true;
    }

    @Override
    public boolean isMinimizable() {
        return true;
    }

    @Override
    public boolean isSetupable() {
        return false;
    }

    @Override
    public boolean isFullWidth() {
        return true;
    }

    // setup:

    @Override
    public ISetup getSetup() {
        return null;
    }

    // notifications:

    @Override
    public void onMaximize(boolean maximized_restored) {
    }

    @Override
    public void onMinimize(boolean minimized_restored) {
    }

    @Override
    public void onDelete() {
    }

}
