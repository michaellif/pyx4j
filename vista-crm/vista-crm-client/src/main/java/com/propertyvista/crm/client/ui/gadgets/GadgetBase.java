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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.widgets.client.dashboard.IGadget;

import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class GadgetBase implements IGadget {

    protected static I18n i18n = I18nFactory.getI18n(GadgetBase.class);

    protected final GadgetMetadata gadgetMetadata;

    public GadgetBase(GadgetMetadata gmd) {
        super();

        if (gmd == null) {
            gmd = EntityFactory.create(GadgetMetadata.class);
            assert (gmd != null);
            selfInit(gmd);

        }
        gadgetMetadata = gmd;
    }

    /*
     * This method is called in case of null GadgetMetadata in constructor.
     * That means on-the-fly gadget creation (Add Gadget), without storage.
     * implement it in derived class in order to set meaningful gadget
     * name/description/type/etc...
     * Note, that it's called from within constructor!
     */
    protected abstract void selfInit(GadgetMetadata gmd);

    // info:

    public GadgetMetadata getGadgetMetadata() {
        return gadgetMetadata;
    }

    /*
     * Implement in derived class to represent desired gadget UI.
     */
    @Override
    public abstract Widget asWidget();

    @Override
    public String getName() {
        return (gadgetMetadata.name().isNull() ? "" : gadgetMetadata.name().getValue());
    }

    @Override
    public String getDescription() {
        return (gadgetMetadata.description().isNull() ? "" : gadgetMetadata.description().getValue());
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
