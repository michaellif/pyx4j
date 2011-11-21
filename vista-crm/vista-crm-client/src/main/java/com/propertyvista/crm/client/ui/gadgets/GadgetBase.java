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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.AbstractGadgetSettings;

public abstract class GadgetBase implements IGadgetBase {
    protected static I18n i18n = I18n.get(GadgetBase.class);

    private boolean isRunning = true;

    protected final GadgetMetadata gadgetMetadata;

    protected IGadgetPresenter presenter;

    private final RefreshTimer refreshTimer = new RefreshTimer();

    public GadgetBase(GadgetMetadata gmd) {

        if (gmd == null) {
            gmd = EntityFactory.create(GadgetMetadata.class);
            assert (gmd != null);
            selfInit(gmd);
        }

        if (gmd.settings().isNull()) {
            gmd.settings().set(createSettings());
        }

        gadgetMetadata = gmd;
    }

    /**
     * This method is called in case of null GadgetMetadata in constructor. That means on-the-fly gadget creation (Add Gadget), without storage.
     * implement it in derived class in order to set meaningful gadget name/description/type/etc... <br/>
     * Note, that it's called from within constructor!
     */
    protected abstract void selfInit(GadgetMetadata gmd);

    /**
     * Construct instance of class that supposed to store Gadget settings and initializes it with default values. This method is called from the constructor of
     * {@link GadgetBase} when no gadget metadata is supplied or gadget metadata doesn't contain settings. The settings created by this class are stored in
     * gadget metadata. Subclasses of {@link GadgetBase} have to override this method when they wish to provide their own settings class.
     * 
     * @return instance of settings (can be <code>null</code>)
     */
    protected AbstractGadgetSettings createSettings() {
        return null;
    };

    @Override
    public void setPresenter(IGadgetPresenter presenter) {
        this.presenter = presenter;
    }

    /*
     * Persisting helpers - can be used from within derived class in order to save/load gadget setting,
     * stored in GadgetMetadata separately. Can be called within/after setPresenter invoked!..
     */
    protected void saveSettings() {
        assert (presenter != null);
        presenter.save(gadgetMetadata.getPrimaryKey(), gadgetMetadata.settings());
    }

    protected void loadSettings() {
        assert (presenter != null);
        presenter.retrieve(gadgetMetadata.getPrimaryKey(), new AsyncCallback<AbstractGadgetSettings>() {
            @Override
            public void onSuccess(AbstractGadgetSettings result) {
                gadgetMetadata.settings().set(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

    /**
     * Override in child in order to perform recurring actions bound to refresh timer interval.
     */
    protected void onRefreshTimer() {
    }

    protected RefreshTimer getRefreshTimer() {
        return refreshTimer;
    }

    // info:

    @Override
    public GadgetMetadata getGadgetMetadata() {
        return gadgetMetadata;
    }

    /**
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

    // runtime:
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void start() {
        isRunning = true;
        getRefreshTimer().reactivate();
    }

    @Override
    public void suspend() {
        isRunning = false;
        getRefreshTimer().deactivate();
    }

    @Override
    public void resume() {
        isRunning = true;
        getRefreshTimer().reactivate();
    }

    @Override
    public void stop() {
        isRunning = false;
        getRefreshTimer().deactivate();
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

    //
    // Internal classes:
    //

    /**
     * This class provides refresh support for the gadget. A gadget that wishes to perform refresh operations has to override
     * {@link GadgetBase#onRefreshTimer()}.
     */
    protected class RefreshTimer {
        private final Timer timer;

        private boolean isActive;

        private int refreshInterval;

        RefreshTimer() {
            isActive = false;
            refreshInterval = 0;

            timer = new Timer() {
                @Override
                public void run() {
                    GadgetBase.this.onRefreshTimer();
                }
            };
        }

        /**
         * Set interval to be used in order to execute {@link GadgetBase#onRefreshTimer()}, that has to be overridden in a subclass in order to
         * provide desired functionality.
         * 
         * @param refreshInterval
         *            refresh interval in milliseconds (if the interval is not positive, the timer stops)
         */
        public void setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
            if (isActive()) {
                reactivate();
            }
        }

        /**
         * Return if the timer is supposed to launch {@link GadgetBase#onRefreshTimer()} periodically as was set using
         * {@link RefreshTimer#setRefreshInterval(int)}.
         * 
         * @return <code>true</code> if timer executes periodically {@link GadgetBase#onRefreshTimer()}, <code>false</code> if it doesn't.
         */
        public boolean isActive() {
            return isActive;
        }

        /**
         * Restart the count down if the refresh interval of the timer is greater than 0, else stop.
         */
        public void reactivate() {
            deactivate();
            if (refreshInterval > 0) {
                timer.scheduleRepeating(refreshInterval);
                isActive = true;
            }
        }

        /**
         * Shut down the timer: {@link ListerGadgetBase#onRefreshTimer()} will not be launched
         */
        public void deactivate() {
            timer.cancel();
            isActive = false;
        }
    }
}
