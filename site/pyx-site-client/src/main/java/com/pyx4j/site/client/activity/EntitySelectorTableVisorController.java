package com.pyx4j.site.client.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;
import com.pyx4j.site.client.ui.visor.AbstractVisorPane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public abstract class EntitySelectorTableVisorController<E extends IEntity> extends AbstractVisorController implements IShowable {

    private static final I18n i18n = I18n.get(EntitySelectorTableVisorController.class);

    private final ListerController<E> listerController;

    private final SelectEntityLister lister;

    private final AbstractVisorPane entityListVisorView;

    private final Class<E> entityClass;

    private final List<E> alreadySelected;

    private boolean isMultiselect;

    private Button btnOk;

    private Button btnCancel;

    public EntitySelectorTableVisorController(IPane parentView, Class<E> entityClass, boolean isMultiselect, String caption) {
        this(parentView, entityClass, false, isMultiselect, null, caption);
    }

    public EntitySelectorTableVisorController(IPane parentView, Class<E> entityClass, boolean isMultiselect, List<E> alreadySelected, String caption) {
        this(parentView, entityClass, false, isMultiselect, alreadySelected, caption);
    }

    public EntitySelectorTableVisorController(IPane parentView, Class<E> entityClass, boolean isVersioned, boolean isMultiselect, List<E> alreadySelected,
            final String caption) {
        super(parentView);

        this.entityClass = entityClass;
        this.isMultiselect = isMultiselect;
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<E> emptyList());

        lister = new SelectEntityLister(entityClass, isVersioned);
        listerController = new ListerController<E>(lister, getSelectService(), entityClass);

        // add control buttons
        btnOk = new Button(i18n.tr("OK"), new Command() {
            @Override
            public void execute() {
                onClickOk();
                hide();
            }
        });
        btnOk.setEnabled(false);
        btnOk.setEnabled(false);
        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                hide();
            }
        });

        entityListVisorView = new AbstractVisorPane(this) {
            {
                // initialize
                setCaption(caption);
                setContentPane(new ScrollPanel(lister.asWidget()));
                getElement().getStyle().setProperty("padding", "6px");

                // add control buttons
                addFooterToolbarItem(btnOk);
                addFooterToolbarItem(btnCancel);
            }
        };
        // add OK button control
        if (this.isMultiselect) {
            lister.getDataTablePanel().getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {
                @Override
                public void onCheck(boolean isAnyChecked) {
                    getOkButton().setEnabled(isAnyChecked);
                }
            });
        } else {
            lister.getDataTablePanel().getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
                @Override
                public void onSelect(int selectedRow) {
                    getOkButton().setEnabled(lister.getSelectedItem() != null);
                }
            });
        }
        // handle already selected items
        setFilters(createRestrictionFilterForAlreadySelected());
    }

    protected abstract AbstractListService<E> getSelectService();

    protected abstract List<ColumnDescriptor> defineColumnDescriptors();

    protected abstract void onClickOk();

    protected E getSelectedItem() {
        if (isMultiselect) {
            List<E> items = lister.getCheckedItems();
            return items.isEmpty() ? null : items.get(0);
        } else {
            return lister.getSelectedItem();
        }
    }

    @SuppressWarnings("unchecked")
    protected List<E> getSelectedItems() {
        if (isMultiselect) {
            return lister.getCheckedItems();
        } else {
            E item = lister.getSelectedItem();
            return item == null ? Collections.EMPTY_LIST : Arrays.asList(item);
        }
    }

    @Override
    public void show() {
        listerController.populate();
        getParentView().showVisor(entityListVisorView);
    }

    public Button getOkButton() {
        return btnOk;
    }

    public Button getCancelButton() {
        return btnCancel;
    }

    protected E proto() {
        return EntityFactory.getEntityPrototype(entityClass);
    }

    public List<Sort> getDefaultSorting() {
        return null;
    }

    protected void setParentFiltering(Key parentID) {
        lister.getDataSource().setParentFiltering(parentID);
    }

    protected void setParentFiltering(Key parentID, Class<? extends IEntity> parentClass) {
        lister.getDataSource().setParentFiltering(parentID, parentClass);
    }

    protected void addFilter(Criterion filter) {
        lister.getDataSource().addPreDefinedFilter(filter);
    }

    protected void addFilters(List<Criterion> filters) {
        lister.getDataSource().addPreDefinedFilters(filters);
    }

    /**
     * Called from within constructor.
     * In order to add additional filters - overwrite it in your class
     * and use addFilter(s) AFTER call to super.setFilters(filters)!..
     * 
     * @param filters
     */
    protected void setFilters(List<Criterion> filters) {
        lister.getDataSource().setPreDefinedFilters(filters);
    }

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        E proto = EntityFactory.getEntityPrototype(entityClass);

        for (E entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    @com.pyx4j.i18n.annotations.I18n(context = "Version Display Mode")
    public enum VersionDisplayMode {
        displayDraft, displayFinal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    class SelectEntityLister extends AbstractLister<E> {

        private VersionDisplayMode versionDisplayMode = VersionDisplayMode.displayFinal;

        private final CRadioGroupEnum<VersionDisplayMode> displayModeButton = new CRadioGroupEnum<VersionDisplayMode>(VersionDisplayMode.class,
                Layout.HORISONTAL);
        {
            displayModeButton.setValue(versionDisplayMode);
            displayModeButton.addValueChangeHandler(new ValueChangeHandler<VersionDisplayMode>() {
                @Override
                public void onValueChange(ValueChangeEvent<VersionDisplayMode> event) {
                    onVersionDisplayModeChange(event.getValue());
                }
            });
        }

        public SelectEntityLister(Class<E> clazz, boolean isVersioned) {
            super(clazz);

            if (EntitySelectorTableVisorController.this.isMultiselect) {
                setHasCheckboxColumn(true);
            } else {
                setSelectable(true);
            }

            getDataTablePanel().setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM }));
            getDataTablePanel().setPageSize(PAGESIZE_SMALL);
            if (isVersioned) {
                getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
            }

            setColumnDescriptors(EntitySelectorTableVisorController.this.defineColumnDescriptors());
            setAllowZoomIn(true);
        }

        public VersionDisplayMode getVersionDisplayMode() {
            return versionDisplayMode;
        }

        @Override
        public List<Sort> getDefaultSorting() {
            List<Sort> sort = EntitySelectorTableVisorController.this.getDefaultSorting();
            if (sort == null) {
                sort = super.getDefaultSorting();
            }
            return sort;
        }

        protected void onVersionDisplayModeChange(VersionDisplayMode mode) {
            versionDisplayMode = mode;
            obtain(0);
        }

        @Override
        protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
            switch (getVersionDisplayMode()) {
            case displayDraft:
                criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
                break;
            case displayFinal:
                criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
                break;
            }
            return super.updateCriteria(criteria);
        }
    }
}
