/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm.ZoominRequestHandler;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.BuildingGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataChangedHandler;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AbstractCounterGadgetBaseService;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class CounterGadgetInstanceBase<Data extends IEntity, Query, GadgetType extends CounterGadgetBaseMetadata> extends
        BuildingGadgetBase<GadgetType> implements IBuildingFilterContainer, IFilterDataProvider<CounterGadgetFilter> {

    public enum StyleNames implements IStyleName {

        CounterGadgetCaption;

    }

    public interface CounterDetailsFactory {

        Widget createDetailsWidget();

    }

    private static final I18n i18n = I18n.get(CounterGadgetInstanceBase.class);

    private static final String ACTIVE_DETALS_KEY_PREFIX = "ActiveDetails:";

    private final Class<Data> dataClass;

    private FlowPanel titlePanel;

    private HTML title;

    private final ZoomableViewForm<Data> summaryForm;

    private SimplePanel detailsPanel;

    private final Map<String, CounterDetailsFactory> detailsFactories;

    /** holds path of the member that was zoomed in (if browser doesn't support html5 session storage) */
    private String localActiveDetails;

    private static SimpleEventBus eventBus;

    public CounterGadgetInstanceBase(Class<Data> dataClass, final AbstractCounterGadgetBaseService<Data, Query> service, ZoomableViewForm<Data> summaryForm,
            GadgetMetadata metadata, Class<GadgetType> metadataClass) {
        this(dataClass, service, summaryForm, metadata, metadataClass, new CounterGadgetSetupForm<GadgetType>(metadataClass));
        this.localActiveDetails = null;
    }

    public CounterGadgetInstanceBase(Class<Data> dataClass, final AbstractCounterGadgetBaseService<Data, Query> service, ZoomableViewForm<Data> summaryForm,
            GadgetMetadata metadata, Class<GadgetType> metadataClass, CEntityForm<GadgetType> form) {
        super(metadata, metadataClass, form);
        this.dataClass = dataClass;

        this.detailsFactories = new HashMap<String, CounterGadgetInstanceBase.CounterDetailsFactory>();
        this.bindDetailsFactories();

        this.summaryForm = summaryForm;

        Set<String> paths = detailsFactories.keySet();
        IObject<?>[] zoomableMembers = new IObject<?>[paths.size()];
        int i = 0;
        for (String path : paths) {
            zoomableMembers[i++] = proto().getMember(new Path(path));
        }
        this.summaryForm.initZoomIn(new ZoominRequestHandler() {
            @Override
            public void onZoomIn(IObject<?> member) {
                switchToDisplayMode(member.getPath().toString());
            }
        }, zoomableMembers);
        this.summaryForm.initContent();

        setDefaultPopulator(new Populator() {

            @Override
            public void populate() {
                String activeDetails = activeDetails();
                boolean isZoomInModeActive = activeDetails != null;
                CounterGadgetInstanceBase.this.summaryForm.setVisible(!isZoomInModeActive);
                CounterGadgetInstanceBase.this.titlePanel.setVisible(isZoomInModeActive);
                CounterGadgetInstanceBase.this.detailsPanel.setVisible(isZoomInModeActive);

                if (!isZoomInModeActive) {
                    service.countData(new DefaultAsyncCallback<Data>() {
                        @Override
                        public void onSuccess(Data result) {
                            CounterGadgetInstanceBase.this.summaryForm.populate(result);
                            populateSucceded();
                        }
                    }, makeSummaryQuery());
                } else {
                    String currentDetailsCaption = EntityFactory.getEntityPrototype(CounterGadgetInstanceBase.this.dataClass)
                            .getMember(new Path(activeDetails)).getMeta().getCaption();
                    title.setHTML(new SafeHtmlBuilder().appendEscaped(currentDetailsCaption).toSafeHtml());
                    detailsPanel.setWidget(detailsFactories.get(activeDetails).createDetailsWidget());
                    populateSucceded();
                }
            }

        });
    }

    @Override
    public CounterGadgetFilter getFilterData() {
        return new CounterGadgetFilter(buildingsFilterContainer.getSelectedBuildingsStubs(), activeDetails());
    }

    @Override
    public void addFilterDataChangedHandler(final IFilterDataChangedHandler<CounterGadgetFilter> handler) {
        if (false) {
            buildingsFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    handler.handleFilterDataChange(new CounterGadgetFilter(event.getBuildings(), activeDetails()));
                }
            });
        }
    }

    // TODO think what to do with building change selection events propagation, this implementation is not nice
    @Override
    public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
        if (false) {
            if (eventBus == null) {
                eventBus = new SimpleEventBus();
            }
            return eventBus.addHandler(BuildingSelectionChangedEvent.TYPE, handler);
        }
        return null;

    }

    @Override
    public List<Building> getSelectedBuildingsStubs() {
        return buildingsFilterContainer.getSelectedBuildingsStubs();
    }

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
        super.setContainerBoard(board);
        board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                if (eventBus != null) {
                    eventBus.fireEvent(event);
                }
            }
        });
    }

    @Override
    protected Widget initContentPanel() {
        FlowPanel content = new FlowPanel();
        content.setWidth("100%");

        titlePanel = new FlowPanel();
        titlePanel.setWidth("100%");

        Button returnButton = new Button(i18n.tr("return to summary"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switchToDisplayMode(null);
            }
        });
        returnButton.getElement().getStyle().setFloat(Float.LEFT);
        returnButton.getElement().getStyle().setMarginRight(2, Unit.EM);
        titlePanel.add(returnButton);

        title = new HTML();
        title.setStyleName(StyleNames.CounterGadgetCaption.name());
        title.getElement().getStyle().setDisplay(Display.INLINE);
        title.getElement().getStyle().setFloat(Float.LEFT);

        titlePanel.add(title);

        content.add(titlePanel);

        summaryForm.asWidget().getElement().getStyle().setProperty("clear", "both");
        content.add(summaryForm);

        detailsPanel = new SimplePanel();
        detailsPanel.getElement().getStyle().setProperty("clear", "both");
        content.add(detailsPanel);

        return content;
    }

    protected abstract Query makeSummaryQuery();

    protected abstract void bindDetailsFactories();

    protected Data proto() {
        return EntityFactory.getEntityPrototype(dataClass);
    }

    protected void bindDetailsFactory(IObject<?> member, CounterDetailsFactory detailsFactory) {
        detailsFactories.put(member.getPath().toString(), detailsFactory);
    }

    boolean hasDetails(IObject<?> member) {
        return detailsFactories.containsKey(member.getPath().toString());
    }

    /**
     * Switches the gadget to either summary or to zoom-in to details mode.
     * 
     * @param activeDetailsMemberPath
     *            <code>null</code> to switch to zoom-in mode, or path of zoomed-in memember
     */
    void switchToDisplayMode(String activeDetailsMemberPath) {
        Storage storage = Storage.getSessionStorageIfSupported();
        if (storage != null) {
            if (activeDetailsMemberPath != null) {
                storage.setItem(activeDetailsKey(), activeDetailsMemberPath);
            } else {
                storage.removeItem(activeDetailsKey());
            }
        } else {
            localActiveDetails = activeDetailsMemberPath;
        }
        populate();
    }

    private String activeDetails() {
        Storage storage = Storage.getSessionStorageIfSupported();
        return storage != null ? storage.getItem(activeDetailsKey()) : localActiveDetails;
    }

    private String activeDetailsKey() {
        return ACTIVE_DETALS_KEY_PREFIX + getMetadata().gadgetId().getValue();
    }
}
