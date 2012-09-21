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

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory.IFilterDataChangedHandler;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AbstractCounterGadgetBaseService;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class CounterGadgetInstanceBase<Data extends IEntity, Query, GadgetType extends CounterGadgetBaseMetadata> extends
        BuildingGadgetBase<GadgetType> implements IBuildingFilterContainer, IFilterDataProvider<CounterGadgetFilter> {

    public interface CounterDetailsFactory {

        Widget createDetailsWidget();

    }

    private static final I18n i18n = I18n.get(CounterGadgetInstanceBase.class);

    private final Class<Data> dataClass;

    private FlowPanel titlePanel;

    private HTML title;

    private final ZoomableViewForm<Data> summaryForm;

    private SimplePanel detailsPanel;

    private final Map<String, CounterDetailsFactory> detailsFactories;

    private static SimpleEventBus eventBus;

    public CounterGadgetInstanceBase(Class<Data> dataClass, final AbstractCounterGadgetBaseService<Data, Query> service, ZoomableViewForm<Data> summaryForm,
            GadgetMetadata metadata, Class<GadgetType> metadataClass) {
        this(dataClass, service, summaryForm, metadata, metadataClass, new CounterGadgetSetupForm<GadgetType>(metadataClass));
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
                displayDetails(member);
            }
        }, zoomableMembers);
        this.summaryForm.initContent();

        setDefaultPopulator(new Populator() {

            @Override
            public void populate() {
                CounterGadgetInstanceBase.this.titlePanel.setVisible(!getMetadata().activeDetails().isNull());
                CounterGadgetInstanceBase.this.summaryForm.setVisible(getMetadata().activeDetails().isNull());
                CounterGadgetInstanceBase.this.detailsPanel.setVisible(!getMetadata().activeDetails().isNull());

                if (getMetadata().activeDetails().isNull()) {

                    service.countData(new DefaultAsyncCallback<Data>() {

                        @Override
                        public void onSuccess(Data result) {
                            CounterGadgetInstanceBase.this.summaryForm.populate(result);
                            populateSucceded();
                        }

                    }, prepareSummaryQuery());
                } else {
                    Path detailsPath = new Path(getMetadata().activeDetails().getValue());
                    title.setHTML(new SafeHtmlBuilder().appendEscaped(
                            EntityFactory.getEntityPrototype(CounterGadgetInstanceBase.this.dataClass).getMember(detailsPath).getMeta().getCaption())
                            .toSafeHtml());

                    detailsPanel.setWidget(detailsFactories.get(detailsPath.toString()).createDetailsWidget());
                    populateSucceded();
                }
            }

        });
    }

    @Override
    public CounterGadgetFilter getFilterData() {
        return new CounterGadgetFilter(buildingsFilterContainer.getSelectedBuildingsStubs(), getMetadata().activeDetails().getValue());
    }

    @Override
    public void addFilterDataChangedHandler(final IFilterDataChangedHandler<CounterGadgetFilter> handler) {
        if (false) {
            buildingsFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    handler.handleFilterDataChange(new CounterGadgetFilter(event.getBuildings(), getMetadata().activeDetails().getValue()));
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
                getMetadata().activeDetails().setValue(null);
                saveMetadata();
                populate();
            }
        });
        returnButton.getElement().getStyle().setFloat(Float.LEFT);
        titlePanel.add(returnButton);

        title = new HTML();
        title.getElement().getStyle().setFloat(Float.LEFT);
        title.getElement().getStyle().setMarginLeft(2, Unit.EM);
        titlePanel.add(title);

        content.add(titlePanel);

        summaryForm.asWidget().getElement().getStyle().setProperty("clear", "both");
        content.add(summaryForm);

        detailsPanel = new SimplePanel();
        detailsPanel.getElement().getStyle().setProperty("clear", "both");
        content.add(detailsPanel);

        return content;
    }

    protected abstract Query prepareSummaryQuery();

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

    void displayDetails(IObject<?> member) {
        getMetadata().activeDetails().setValue(member.getPath().toString());
        saveMetadata();
        populate();
    }

}
