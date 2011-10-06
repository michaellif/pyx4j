/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.ListerBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.StringHolder;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.SortEntity;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    /**
     * Refresh interval for the list when in development mode in milliseconds (relevant when refreshing is activated)
     */
    private static final RefreshInterval DEFAULT_REFRESH_INTERVAL = RefreshInterval.Never;

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    protected final EnhancedListerBase<E> listerBase;

    protected ListerGadgetBaseSettings settings = null;

    private final RefreshTimer refreshTimer;

    private final ListerActivityBase<E> listerActivity;

    public ListerGadgetBase(GadgetMetadata gmd, AbstractCrudService<E> service, Class<E> entityClass) {
        super(gmd);

        // TODO add more civilised (use isSettingsOk() method) when IEntity.isInstance() works well
        try {
            settings = gadgetMetadata.settings().cast();
        } catch (Throwable eh) {
            settings = EntityFactory.create(ListerGadgetBaseSettings.class);
            resetToDefault(settings);
            gadgetMetadata.settings().set(settings);
        }

        refreshTimer = new RefreshTimer();

        listerBase = new EnhancedListerBase<E>(entityClass, null);
        listerActivity = new ListerActivityBase<E>(listerBase, service, entityClass);

        // TODO enable filters when they can be persisted
        listerBase.setFiltersVisible(false);

        listerBase.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    // FIXME possible memory leak???
                    // commented out because when the application returns to the gadget's page it seems to restart the page refreshing
                    // but draws another instance of ListerGadget, i.e: on the new gadget's setup, the refresh
                    // interval is "Never", but the "loading..." message on top of the screen begins to appear at regular period

                    // start()
                } else {
                    ListerGadgetBase.this.stop();
                }
            }
        });
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        AbstractGadgetSettings settings = createSettings();
        initDefaultSettings(settings);
        gmd.settings().set(settings);
    }

    protected boolean isSettingsInstanceOk(AbstractGadgetSettings abstractSettings) {
        // TODO warning! not to be used because it looks like isInstanceOf doesn't work properly
        return abstractSettings.isInstanceOf(ListerGadgetBaseSettings.class);
    }

    protected AbstractGadgetSettings createSettings() {
        ListerGadgetBaseSettings settings = EntityFactory.create(ListerGadgetBaseSettings.class);
        assert settings != null : "Failed to instantiate ListerGadgetBaseSettings class";
        return settings;
    }

    protected void initDefaultSettings(AbstractGadgetSettings abstractSettings) {
        ListerGadgetBaseSettings settings = null;
        if (abstractSettings.isInstanceOf(ListerGadgetBaseSettings.class)) {
            settings = abstractSettings.cast();
            resetToDefault(settings);
        } else {
            // TODO maybe better to throw an exception?
        }
    }

    private void resetToDefault(ListerGadgetBaseSettings settings) {
        settings.refreshInterval().setValue(DEFAULT_REFRESH_INTERVAL);
        settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE);
        settings.currentPage().setValue(0);
        settings.columnPaths().clear();
    }

    protected EnhancedListerBase<E> getListerBase() {
        return listerBase;
    }

    /*
     * Implement in derived class to set default table structure.
     * Note, that it's called from within constructor!
     */
    protected abstract void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    protected void setRefreshInterval(RefreshInterval refreshInterval) {
        settings.refreshInterval().setValue(refreshInterval);
        int refreshIntervalMillis;

        if (refreshInterval != RefreshInterval.Never) {
            if (GWT.isProdMode()) {
                refreshIntervalMillis = refreshInterval.value() * 60000;
            } else {
                refreshIntervalMillis = refreshInterval.value() * 1000;
            }
        } else {
            refreshIntervalMillis = -1;
        }
        refreshTimer.setRefreshInterval(refreshIntervalMillis);
    }

    @Override
    public GadgetMetadata getGadgetMetadata() {
        // store the state of ListerBase in the settings in order to provide up to date meta data
        storeSettings();

        // in normal situation settings property should be referenced by gadget metadata
        return this.gadgetMetadata;
    }

    public static SortEntity SortToEntity(Sort sort) {
        SortEntity entity = EntityFactory.create(SortEntity.class);
        entity.propertyName().setValue(sort.getPropertyName());
        entity.descending().setValue(sort.isDescending());
        return entity;
    }

    public static Sort EntityToSort(SortEntity entity) {
        Sort sort = new Sort(entity.propertyName().getValue(), entity.descending().getValue());
        return sort;
    }

    //
    // IGadget:
    @Override
    public Widget asWidget() {
        return listerBase.asWidget();
    }

    @Override
    public void start() {
        super.start();
        applySettings();
        listerActivity.populate(settings.currentPage().getValue());
    }

    @Override
    public void stop() {
        super.stop();
        refreshTimer.deactivate();
    }

    @Override
    public void suspend() {
        super.suspend();
        refreshTimer.deactivate();
    }

    @Override
    public void resume() {
        super.resume();
        refreshTimer.reactivate();
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        return new SetupLister();
    }

    private void storeSettings() {
        // COLUMNS:
        settings.columnPaths().clear();
        for (ColumnDescriptor<E> columnDescriptor : getListerBase().getSelectedColumnDescriptors()) {
            StringHolder columnName = EntityFactory.create(StringHolder.class);
            columnName.stringValue().setValue(columnDescriptor.getColumnName());
            settings.columnPaths().add(columnName);
        }

        // SORTING:
        settings.sorting().clear();
        List<Sort> sorting = getListerBase().getSorting();
        for (Sort sort : sorting) {
            SortEntity sortEntity = SortToEntity(sort);
            settings.sorting().add(sortEntity);
        }

        // CURRENT PAGE:
        Integer currentPage = getListerBase().getLister().getPageNumber();
        settings.currentPage().setValue(currentPage);

        // TODO FILTERING: current problem: filter value cannot be persisted (needs to be serialised in some way)
    }

    private void applySettings() {
        getListerBase().setPageSize(settings.itemsPerPage().getValue());
        setRefreshInterval(settings.refreshInterval().getValue());
        refreshTimer.reactivate();

        // apply columns
        ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
        if (settings.columnPaths().isEmpty()) {
            this.fillDefaultColumnDescriptors(columnDescriptors, getListerBase().proto());
        } else {
            for (StringHolder columnName : settings.columnPaths()) {
                ColumnDescriptor<E> columnDescriptor = ColumnDescriptorFactory.createColumnDescriptor(getListerBase().proto(), getListerBase().proto()
                        .getMember(new Path(columnName.stringValue().getValue())));
                columnDescriptors.add(columnDescriptor);
            }

        }
        getListerBase().setColumnDescriptors(columnDescriptors);

        // apply sorting
        List<Sort> sorting = new LinkedList<Sort>();
        for (SortEntity sortEntity : settings.sorting()) {
            sorting.add(EntityToSort(sortEntity));
        }
        getListerBase().setSorting(sorting);

        // TODO: apply filtering
    }

    private class EnhancedListerBase<T> extends ListerBase<E> {

        public EnhancedListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass) {
            super(clazz, itemOpenPlaceClass);
        }

        @Override
        public HandlerRegistration addAttachHandler(AttachEvent.Handler handler) {
            return getListPanel().addAttachHandler(handler);
        }

        public E proto() {
            return getListPanel().proto();
        }

        public void setPageSize(int itemsPerPage) {
            getListPanel().setPageSize(itemsPerPage);
        }

        public void setColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors) {
            getListPanel().getDataTable().getDataTableModel().setColumnDescriptors(columnDescriptors);
        }

        public List<ColumnDescriptor<E>> getSelectedColumnDescriptors() {
            return getListPanel().getDataTable().getDataTableModel().getColumnDescriptors();
        }

        @Override
        protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto) {
            ListerGadgetBase.this.fillDefaultColumnDescriptors(columnDescriptors, proto);
        }

        @Override
        protected void onPrevPage() {
            super.onPrevPage();
            refreshTimer.reactivate();
        }

        @Override
        protected void onNextPage() {
            super.onNextPage();
            refreshTimer.reactivate();
        }

    }

    /**
     * This class provides refresh logic
     */
    private class RefreshTimer {
        private final Timer timer;

        private boolean isActive;

        private int refreshInterval;

        RefreshTimer() {
            isActive = false;
            refreshInterval = 0;
            timer = new Timer() {
                @Override
                public void run() {
                    storeSettings();

                    // try to reload the page and if the page is empty try to load the previous one
                    int pageToReload = getListerBase().getPageNumber();
                    do {
                        listerActivity.populate(pageToReload--);
                    } while ((listerBase.getPageNumber() > 0) && (listerBase.getPageSize() == 0));
                }
            };
        }

        /**
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

        public boolean isActive() {
            return isActive;
        }

        /**
         * Restart the count down if the refresh interval of the timer is greater than 0, else stop
         */
        public void reactivate() {
            deactivate();
            if (refreshInterval > 0) {
                timer.scheduleRepeating(refreshInterval);
                isActive = true;
            }
        }

        public void deactivate() {
            timer.cancel();
            isActive = false;
        }
    }

    //
    // Setup UI implementation:
    class SetupLister implements ISetup {

        protected final HorizontalPanel setupPanel = new HorizontalPanel();

        protected final TextBox itemsPerPage = new TextBox();

        protected final ListBox intervalList = new ListBox(false);

        protected SetupLister() {
            super();

            VerticalPanel addition = new VerticalPanel();

            addition.add(new HTML("&nbsp"));
            HorizontalPanel items = new HorizontalPanel();
            items.add(new Label(i18n.tr("Items per page:")));

            itemsPerPage.setText(String.valueOf(getListerBase().getPageSize()));
            itemsPerPage.setWidth("100%");
            items.add(itemsPerPage);
            items.setCellHorizontalAlignment(itemsPerPage, HasHorizontalAlignment.ALIGN_RIGHT);

            items.setSpacing(4);
            items.setWidth("100%");
            addition.add(items);

            HorizontalPanel refresh = new HorizontalPanel();
            refresh.add(new Label(i18n.tr("Refresh interval:")));

            for (RefreshInterval i : RefreshInterval.values()) {
                intervalList.addItem(i.toString());
                intervalList.setValue(intervalList.getItemCount() - 1, i.name());
                if (settings.refreshInterval().getValue() == i) {
                    intervalList.setSelectedIndex(intervalList.getItemCount() - 1);
                }
            }
            intervalList.setWidth("100%");

            refresh.add(intervalList);
            refresh.setCellHorizontalAlignment(intervalList, HasHorizontalAlignment.ALIGN_RIGHT);

            refresh.setSpacing(4);
            refresh.setWidth("100%");
            addition.add(refresh);
            addition.getElement().getStyle().setPaddingLeft(10, Unit.PX);

            setupPanel.add(addition);
            setupPanel.getElement().getStyle().setPadding(3, Unit.PX);
        }

        @Override
        public Widget asWidget() {
            return setupPanel;
        }

        @Override
        public boolean onStart() {
            suspend();
            return true;
        }

        @Override
        public boolean onOk() {
            int itemsPerPageCount = settings.itemsPerPage().getValue();
            try {
                itemsPerPageCount = Integer.parseInt(itemsPerPage.getText());
            } catch (Throwable e) {
                // TODO ignore? show an error message? return false? make control validate the input?
            }
            settings.itemsPerPage().setValue(itemsPerPageCount);

            if (intervalList.getSelectedIndex() != -1) {
                settings.refreshInterval().setValue(RefreshInterval.valueOf(intervalList.getValue(intervalList.getSelectedIndex())));
            }

            storeSettings();

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
