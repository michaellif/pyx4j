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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata.RefreshInterval;

public abstract class GadgetInstanceBase<T extends GadgetMetadata> implements IGadgetInstanceBase {
    private static final I18n i18n = I18n.get(GadgetInstanceBase.class);

    private boolean isRunning;

    protected IGadgetInstancePresenter presenter;

    private final RefreshTimer refreshTimer;

    private VerticalPanel gadgetPanel;

    private Panel errorPanel;

    private Panel loadingPanel;

    private Widget contentPanel;

    private boolean isLoading;

    private Populator defaultPopulator;

    private final T metadata;

    protected final Class<T> metadataClass;

    @SuppressWarnings("unchecked")
    public GadgetInstanceBase(GadgetMetadata gmd, Class<T> metadataClass) {
        assert metadataClass != null;
        this.metadataClass = metadataClass;

        metadata = (gmd == null) ? createDefaultSettings(metadataClass) : (T) gmd.cast();

        refreshTimer = new RefreshTimer();
        defaultPopulator = null;
        isRunning = false;
    }

    protected Panel initLoadingPanel() {
        VerticalPanel loadingPanel = createPanel();
        Label label = new Label(i18n.tr("Loading") + "...");
        loadingPanel.add(label);
        return loadingPanel;
    }

    protected Panel initErrorPanel() {
        VerticalPanel errorPanel = createPanel();
        Label label = new Label(i18n.tr("Error") + ":(");
        errorPanel.add(label);
        return errorPanel;
    }

    private VerticalPanel createPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.setSize("100%", defineHeight());
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        return panel;
    }

    /**
     * Construct instance of class that supposed to store Gadget settings and initializes it with default values. This method is called from the constructor of
     * {@link GadgetInstanceBase} when no gadget metadata is supplied or gadget metadata doesn't contain settings. The settings created by this class are stored
     * in
     * gadget metadata. Subclasses of {@link GadgetInstanceBase} have to override this method when they wish to provide their own settings class.
     * 
     * @return instance of settings (cannot be <code>null</code>)
     */
    protected T createDefaultSettings(Class<T> metadataClass) {
        T settings = EntityFactory.create(metadataClass);
        settings.refreshInterval().setValue(RefreshInterval.Never);
        return settings;
    };

    @Override
    public void setPresenter(IGadgetInstancePresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Persisting helper - can be used from within derived class in order to load gadget settings,
     * stored in GadgetMetadata separately. Can be called within/after setPresenter invoked!
     */
    protected void saveMetadata() {
        assert (presenter != null) : "Failed to to save settings: no presenter was available";
        presenter.save(getMetadata());
    }

    /**
     * Persisting helper - can be used from within derived class in order to load gadget settings,
     * stored in GadgetMetadata separately. Can be called within/after setPresenter invoked!
     */
    protected void loadSettings() {

        // FIXME review Gadget Metadata saving and loading: currently this function is not used anywhere
//        assert (presenter != null);
//        presenter.retrieve(gadgetMetadata.getPrimaryKey(), new AsyncCallback<GadgetSettings>() {
//            @Override
//            public void onSuccess(GadgetSettings result) {
//                gadgetMetadata.settings().set(result);
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                throw new UnrecoverableClientError(caught);
//            }
//        });
    }

    /**
     * Override in child in order to perform recurring actions bound to refresh timer interval.
     */
    protected void onRefreshTimer() {
        populate();
    }

    protected RefreshTimer getRefreshTimer() {
        return refreshTimer;
    }

    @Override
    public T getMetadata() {
        return metadata;
    }

    /**
     * Implement in derived class to represent desired gadget UI.
     */
    public abstract Widget initContentPanel();

    protected final Widget initView() {
        errorPanel = initErrorPanel();
        errorPanel.setVisible(false);
        loadingPanel = initLoadingPanel();
        loadingPanel.setVisible(false);
        contentPanel = initContentPanel();
        contentPanel.setVisible(false);

        gadgetPanel = createPanel();
        gadgetPanel.add(contentPanel);
        gadgetPanel.add(loadingPanel);
        gadgetPanel.add(errorPanel);
        return gadgetPanel;
    }

    /** Implement in derived class to set up custom height: i.e. for graphs that have absolute height. */
    protected String defineHeight() {
        return "100%";
    }

    @Override
    public Widget asWidget() {
        return gadgetPanel;
    }

    @Override
    public String getName() {
        return metadata.getEntityMeta().getCaption();
    }

    @Override
    public String getDescription() {
        // FIXME talk to someone about this: do we really need to keep this inside the gadget
        return "";
    }

    /** Set a callback that will run on each time refresh period and when {@link #populate(boolean)} or {@link #populate()} are exectuted. */
    protected void setDefaultPopulator(Populator populator) {
        this.defaultPopulator = populator;
    }

    protected void populate() {
        populate(defaultPopulator, true);
    }

    protected void populate(boolean showLoadingPanel) {
        populate(defaultPopulator, showLoadingPanel);
    }

    protected void populate(final Populator populator, boolean showLoadingPanel) {
        if (populator == null) {
            if (!GWT.isProdMode()) {
                throw new Error("no populator provided (use setPopulator() or execute populateStart() with non null parameter!");
            }
        }
        if (isRunning() & asWidget().isVisible() & asWidget().isAttached() & !isLoading) {
            refreshTimer.deactivate();
            if (showLoadingPanel) {
                loadingPanel.setPixelSize(contentPanel.getElement().getClientWidth(), contentPanel.getElement().getClientHeight());
                loadingPanel.setVisible(true);
                contentPanel.setVisible(false);
            }
            errorPanel.setVisible(false);
            isLoading = true;

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    populator.populate();
                }
            });
        }
    }

    /** Should be called at the end of successful population */
    // TODO consider creating generic gadgets that parametrized by DTO Entity that is used to keep the gadgets data.
    protected final void populateSucceded() {
        refreshTimer.reactivate();
        loadingPanel.setVisible(false);
        contentPanel.setVisible(true);
        isLoading = false;
    }

    /** Should be called and the end of unsuccessful population */
    protected final void populateFailed(Throwable error) {
        refreshTimer.reactivate();
        loadingPanel.setVisible(false);

        // TODO create separate class for error panel
        errorPanel.clear();
        errorPanel.setPixelSize(contentPanel.getElement().getClientWidth(), contentPanel.getElement().getClientHeight());
        errorPanel.add(new Label(error.getLocalizedMessage()));
    }

    // runtime:
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void start() {
        isRunning = true;
        populate(defaultPopulator, true);
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
    public void onResize() {
    }

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
     * {@link GadgetInstanceBase#onRefreshTimer()}.
     */
    protected class RefreshTimer {
        private final Timer timer;

        private boolean isActive;

        RefreshTimer() {
            isActive = false;
            timer = new Timer() {
                @Override
                public void run() {
                    onRefreshTimer();
                }
            };
        }

        /**
         * Return if the timer is supposed to launch {@link GadgetInstanceBase#onRefreshTimer()} periodically as was set using
         * {@link RefreshTimer#setRefreshInterval(int)}.
         * 
         * @return <code>true</code> if timer executes periodically {@link GadgetInstanceBase#onRefreshTimer()}, <code>false</code> if it doesn't.
         */
        public boolean isActive() {
            return isActive;
        }

        /**
         * Restart the count down if the refresh interval of the timer is greater than 0, else stop.
         */
        public void reactivate() {
            deactivate();
            if ((!isActive() & !getMetadata().refreshInterval().isNull()) && (getMetadata().refreshInterval().getValue().value() > 0)) {
                if (GWT.isProdMode()) {
                    timer.scheduleRepeating(getMetadata().refreshInterval().getValue().value());
                } else {
                    timer.scheduleRepeating(getMetadata().refreshInterval().getValue().value() / 60);
                }
                isActive = true;
            }
        }

        /**
         * Shut down the timer: {@link ListerGadgetInstanceBase#onRefreshTimer()} will not be launched
         */
        public void deactivate() {
            timer.cancel();
            isActive = false;
        }
    }

    /**
     * Override to implement population. Finish population with call to {@link #populateSucceded()} or {@link #populateFailed(Throwable)}.
     */
    protected abstract class Populator {
        public abstract void populate();
    }

    protected class SetupForm implements ISetup {
        private final CEntityDecoratableEditor<T> form;

        public SetupForm(CEntityDecoratableEditor<T> form) {
            assert form != null;

            this.form = form;
            this.form.initContent();
        }

        @Override
        public Widget asWidget() {
            return form.asWidget();
        }

        @Override
        public boolean onStart() {
            suspend();
            form.populate(getMetadata().clone(metadataClass));
            return true;
        }

        @Override
        public boolean onOk() {
            T metadata = form.getValue();
            Key key = getMetadata().id().getValue();
            getMetadata().set(metadata);
            getMetadata().id().setValue(key);

            // restart the gadget:            
            stop();
            start();
            return true;
        }

        @Override
        public void onCancel() {
            resume();
        }
    }
}
