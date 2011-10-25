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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractListService;

import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings;
import com.propertyvista.domain.dashboard.gadgets.ListerGadgetBaseSettings.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.SortEntity;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    /**
     * Refresh interval for the list when in development mode in milliseconds (relevant when refreshing is activated)
     */
    private static final RefreshInterval DEFAULT_REFRESH_INTERVAL = RefreshInterval.Never;

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    private final EnhancedListerBase<E> listerBase;

    private ListerGadgetBaseSettings settings;

    protected final AbstractListService<E> service;

    public ListerGadgetBase(GadgetMetadata gmd, AbstractListService<E> service, Class<E> entityClass) {
        super(gmd);
        this.service = service;

        // validate that we got correct settings class 'cause our subclasses could not be trusted (they may have messed something) 
        if (isSettingsInstanceOk(gadgetMetadata.settings())) {
            settings = gadgetMetadata.settings().cast();
        } else {
            settings = createSettings();
            gadgetMetadata.settings().set(settings);
        }

        listerBase = new EnhancedListerBase<E>(entityClass, null);
        new ListerActivityBase<E>(listerBase, service, entityClass);

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

    protected boolean isSettingsInstanceOk(AbstractGadgetSettings abstractSettings) {
        return abstractSettings.isInstanceOf(ListerGadgetBaseSettings.class);
    }

    @Override
    protected ListerGadgetBaseSettings createSettings() {
        ListerGadgetBaseSettings settings = EntityFactory.create(ListerGadgetBaseSettings.class);
        assert settings != null : "Failed to instantiate ListerGadgetBaseSettings class";
        settings.refreshInterval().setValue(DEFAULT_REFRESH_INTERVAL);
        settings.itemsPerPage().setValue(DEFAULT_ITEMS_PER_PAGE);
        settings.currentPage().setValue(0);
        settings.columnPaths().clear();
        return settings;
    }

    protected EnhancedListerBase<E> getListerBase() {
        return listerBase;
    }

    @Override
    protected void onRefreshTimer() {
        storeSettings();

        // try to reload the page and if the page is empty try to load the previous one
        int pageToReload = getListerBase().getPageNumber();
        do {
            getListerBase().getPresenter().populate(pageToReload--);
        } while ((getListerBase().getPageNumber() > 0) && (getListerBase().getPageSize() == 0));
    }

    /*
     * Implement in derived class to set default table structure.
     * Note, that it's called from within constructor!
     */
    protected abstract void fillDefaultColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    protected abstract void fillAvailableColumnDescripors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

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
        getRefreshTimer().setRefreshInterval(refreshIntervalMillis);
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
        return getListerBase().asWidget();
    }

    @Override
    public void start() {
        super.start();
        applySettings();
        getListerBase().getPresenter().populate(settings.currentPage().getValue());
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public ISetup getSetup() {
        return new SetupLister();
    }

    /**
     * Gather the state of the lister and store it into the settings member (note: this method doens't persist the settings to the DB)
     */
    private void storeSettings() {
        // COLUMNS:
        settings.columnPaths().clear();
        // FIXME currently pollutes db with strings on each save hence we are not going to enable this feature
        // don't forget to fix applySettings() when enableing this
//        Collection<ColumnDescriptor<E>> descriptors = getListerBase().getSelectedColumnDescriptors();
//        for (ColumnDescriptor<E> columnDescriptor : descriptors) {
//            StringHolder columnName = EntityFactory.create(StringHolder.class);
//            columnName.stringValue().setValue(columnDescriptor.getColumnName());
//            settings.columnPaths().add(columnName);
//        }

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
        getRefreshTimer().reactivate();

        // apply columns
        // FIXME see storeSettings for more info
        ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
        fillDefaultColumnDescriptors(columnDescriptors, getListerBase().proto());
//        if (settings.columnPaths().isEmpty()) {
//            this.fillDefaultColumnDescriptors(columnDescriptors, getListerBase().proto());
//        } else {
//            for (StringHolder columnName : settings.columnPaths()) {
//                ColumnDescriptor<E> columnDescriptor = ColumnDescriptorFactory.createColumnDescriptor(getListerBase().proto(), getListerBase().proto()
//                        .getMember(new Path(columnName.stringValue().getValue())));
//                columnDescriptors.add(columnDescriptor);
//            }
//
//        }
//        getListerBase().setColumnDescriptors(columnDescriptors);

        // apply sorting
        List<Sort> sorting = new LinkedList<Sort>();
        for (SortEntity sortEntity : settings.sorting()) {
            sorting.add(EntityToSort(sortEntity));
        }
        getListerBase().setSorting(sorting);

        // TODO: apply filtering
    }

    protected class EnhancedListerBase<T> extends ListerBase<E> {

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
        protected void fillAvailableColumnDescriptors(java.util.List<com.pyx4j.entity.client.ui.datatable.ColumnDescriptor<E>> columnDescriptors, E proto) {
            ListerGadgetBase.this.fillAvailableColumnDescripors(columnDescriptors, proto);
        };

        @Override
        protected void onPrevPage() {
            super.onPrevPage();
            getRefreshTimer().reactivate();
        }

        @Override
        protected void onNextPage() {
            super.onNextPage();
            getRefreshTimer().reactivate();
        }
    }

    //
    // Setup UI implementation:
    class SetupLister implements ISetup {

        protected final FlexTable setupPanel = new FlexTable();

        protected final TextBox itemsPerPage = new TextBox();

        protected final ListBox intervalList = new ListBox(false);

        protected SetupLister() {
            super();

            setupPanel.setWidget(0, 0, new Label(i18n.tr("Items per page") + ":"));

            itemsPerPage.setText(String.valueOf(getListerBase().getPageSize()));
            itemsPerPage.setWidth("100%");
            setupPanel.setWidget(0, 1, itemsPerPage);

            setupPanel.setWidget(1, 0, new Label(i18n.tr("Refresh interval") + ":"));

            for (RefreshInterval i : RefreshInterval.values()) {
                intervalList.addItem(i.toString(), i.name());
                if (settings.refreshInterval().getValue() == i) {
                    intervalList.setSelectedIndex(intervalList.getItemCount() - 1);
                }
            }
            intervalList.setWidth("100%");
            setupPanel.setWidget(1, 1, intervalList);

            setupPanel.getElement().getStyle().setPaddingLeft(2, Unit.EM);
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
