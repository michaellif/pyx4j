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
package com.propertyvista.crm.client.ui.gadgets.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.dashboard.GadgetMetadataService;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class GadgetInstanceBase<T extends GadgetMetadata> implements IGadgetInstance {

    private static final I18n i18n = I18n.get(GadgetInstanceBase.class);

    private static final GadgetMetadataService GADGET_METADATA_SERVICE = GWT.create(GadgetMetadataService.class);

    private boolean isRunning;

    protected IBuildingFilterContainer containerBoard;

    private final RefreshTimer refreshTimer;

    private VerticalPanel gadgetPanel;

    private Panel errorPanel;

    private Panel loadingPanel;

    private Widget contentPanel;

    private boolean isLoading;

    private Populator defaultPopulator;

    private CContainer<?, T, ?> setupForm;

    private final T metadata;

    // TODO metadataClass argument is needed only for creation of the default metatada, remove when default metadata creation is implemented on server side
    @SuppressWarnings("unchecked")
    public GadgetInstanceBase(GadgetMetadata metadata, Class<T> metadataClass, CContainer<?, T, ?> setupForm) {
        assert metadata != null;
        this.metadata = (T) metadata;
        this.setupForm = setupForm;
        if (setupForm != null) {
            this.setupForm.init();
        }

        this.isRunning = false;
        this.refreshTimer = new RefreshTimer();
        this.defaultPopulator = null;
    }

    public GadgetInstanceBase(GadgetMetadata metadata, Class<T> metadataClass) {
        this(metadata, metadataClass, null);
    }

    @Override
    public T getMetadata() {
        return metadata;
    }

    /** @deprecated this is not used at all!!! */
    // FIXME ask Misha or VladL if it's safe to get rid of this (now description is stored on server and is retrieved by GadgetMetataService.listAvailableGadgets) 
    @Deprecated
    @Override
    public final String getDescription() {
        return null;
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

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
        this.containerBoard = board;
    }

    /** this function was made final in order to ensure the validation of permissions */
    protected final void saveMetadata() {
        if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey())) {
            GADGET_METADATA_SERVICE.saveGadgetMetadata(new DefaultAsyncCallback<GadgetMetadata>() {

                @Override
                public void onSuccess(GadgetMetadata result) {
                    getMetadata().set(result);
                }

            }, getMetadata());
        }
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

    /**
     * Implement in derived class to represent desired gadget UI.
     */
    protected abstract Widget initContentPanel();

    protected final Widget initView() {
        if (errorPanel == null) {
            errorPanel = initErrorPanel();
        }
        errorPanel.setVisible(false);
        if (loadingPanel == null) {
            loadingPanel = initLoadingPanel();
        }
        loadingPanel.setVisible(false);
        contentPanel = initContentPanel();
        contentPanel.setVisible(false);

        if (gadgetPanel == null) {
            gadgetPanel = createPanel();
        } else {
            gadgetPanel.clear();
        }
        gadgetPanel.add(contentPanel);
        gadgetPanel.add(loadingPanel);
        gadgetPanel.add(errorPanel);
        return gadgetPanel;
    }

    @Override
    public Widget asWidget() {
        return gadgetPanel;
    }

    @Override
    public String getName() {
        return metadata.getEntityMeta().getCaption();
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

    /** Implement in derived class to set up custom height: i.e. for graphs that have absolute height. */
    protected String defineHeight() {
        return "100%";
    }

    protected void populate(final Populator populator, boolean showLoadingPanel) {
        if (populator == null) {
            if (!GWT.isProdMode()) {
                throw new Error("no populator provided (use setDefaultPopulator() or execute populateStart() with non null parameter!");
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

        contentPanel.setVisible(false);
        // TODO create separate class for error panel

        errorPanel.clear();
        errorPanel.setVisible(true);
        errorPanel.setPixelSize(contentPanel.getElement().getClientWidth(), contentPanel.getElement().getClientHeight());

        errorPanel.add(new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Error:")).toSafeHtml()));
        HTML errorMessage = new HTML(new SafeHtmlBuilder().appendEscaped(error.getMessage() == null ? i18n.tr("Uknown Error") : error.getLocalizedMessage())
                .toSafeHtml());
        errorMessage.setWidth("100%");
        errorMessage.getElement().getStyle().setPaddingTop(1, Unit.EM);
        errorMessage.getElement().getStyle().setPaddingBottom(2, Unit.EM);
        errorPanel.add(errorMessage);
        errorPanel.add(new Button(i18n.tr("Try to reload"), new Command() {
            @Override
            public void execute() {
                populate();
            }
        }));
        isLoading = false;
    }

    // runtime:
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void start() {

        initView();
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
        return setupForm != null;
    }

    @Override
    public boolean isFullWidth() {
        return true;
    }

    // setup:

    @Override
    public ISetup getSetup() {
        if (setupForm != null) {
            return new SetupFormWrapper(setupForm);
        } else {
            throw new IllegalStateException("this gadget is not setupable");
        }
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
     * Override to implement population. Finish population with call to {@link #populateSucceded()} or {@link #populateFailed(Throwable)}.
     */
    protected abstract class Populator {

        public abstract void populate();

    }

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

    protected class SetupFormWrapper implements ISetup {

        private final CContainer<?, T, ?> form;

        public SetupFormWrapper(CContainer<?, T, ?> form) {
            assert form != null;
            this.form = form;
        }

        @Override
        public Widget asWidget() {
            return form.asWidget();
        }

        @Override
        public boolean onStart() {
            suspend();
            form.populate(getMetadata().<T> duplicate());
            return true;
        }

        @Override
        public boolean onOk() {
            T metadata = form.getValue();
            Key key = getMetadata().id().getValue();
            getMetadata().set(metadata);
            getMetadata().id().setValue(key);
            saveMetadata();

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
