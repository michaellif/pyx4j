/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dialog.OkDialog;
import com.pyx4j.widgets.client.dialog.OkOptionText;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.crm.client.ui.components.KeywordsBox;
import com.propertyvista.crm.rpc.dto.dashboard.GadgetDescriptorDTO;
import com.propertyvista.crm.rpc.services.dashboard.GadgetMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class AddGadgetDialog extends OkDialog implements OkOptionText {

    public static final String ADD_GADGET_DIALOG_STYLE = "-vista-AddGadgetDialog";

    public enum StyleSuffix implements IStyleName {

        GadgetDescriptionsList, GadgetDescriptionBox, GadgetNameLabel, GadgetDescriptionText, GadgetDescriptionDecorator,

    }

    @Transient
    public interface AddGadgetGadgetDescriptor extends IEntity {

        IPrimitive<String> name();

        IPrimitive<String> description();

        GadgetMetadata gadgetMetadataProto();
    }

    @Transient
    public interface AddGadgetGadgetDescriptorContainer extends IEntity {

        IList<AddGadgetGadgetDescriptor> descriptors();
    }

    public class GadgetDescriptorsListForm extends CForm<AddGadgetGadgetDescriptorContainer> {

        public GadgetDescriptorsListForm() {
            super(AddGadgetGadgetDescriptorContainer.class);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();
            contentPanel.add(inject(proto().descriptors(), new GadgetDescriptorFolder()));
            contentPanel.addStyleName(ADD_GADGET_DIALOG_STYLE + StyleSuffix.GadgetDescriptionsList);
            return contentPanel;
        }

    }

    public class GadgetDescriptorFolder extends CFolder<AddGadgetGadgetDescriptor> {

        public class GadgetDescriptorDecorator extends Composite implements IFolderItemDecorator<AddGadgetGadgetDescriptor> {

            private final SimplePanel componentPanel;

            public GadgetDescriptorDecorator() {
                FlowPanel decoratorPanel = new FlowPanel();
                decoratorPanel.setWidth("100%");
                decoratorPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                decoratorPanel.setStyleName(ADD_GADGET_DIALOG_STYLE + StyleSuffix.GadgetDescriptionDecorator);

                componentPanel = new SimplePanel();
                componentPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                decoratorPanel.add(componentPanel);

                initWidget(decoratorPanel);
            }

            @Override
            public void init(final CFolderItem<AddGadgetGadgetDescriptor> folderItem) {
            }

            @Override
            public void setContent(IsWidget content) {
                componentPanel.setWidget(content);
            }

            @Override
            public FolderImages getImages() {
                return VistaImages.INSTANCE;
            }

            @Override
            public void setActionsState(boolean remove, boolean up, boolean down) {

            }

            @Override
            public void adoptItemActionsBar() {

            }

            @Override
            public void onSetDebugId(IDebugId parentDebugId) {

            }

        }

        public class GadgetDescriptorForm extends CForm<AddGadgetGadgetDescriptor> {

            public GadgetDescriptorForm() {
                super(AddGadgetGadgetDescriptor.class);
            }

            @Override
            protected IsWidget createContent() {
                ClickHandler onAddGadgetRequested = new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        AddGadgetDialog.this.addGadget((GadgetMetadata) EntityFactory.getEntityPrototype(GadgetDescriptorForm.this.getValue()
                                .gadgetMetadataProto().getInstanceValueClass()));
                    }
                };

                com.pyx4j.forms.client.ui.panels.FormPanel formPanel = new com.pyx4j.forms.client.ui.panels.FormPanel(this);
                formPanel.addStyleName(ADD_GADGET_DIALOG_STYLE + StyleSuffix.GadgetDescriptionBox);
                formPanel.append(Location.Dual, proto().name(), new CLabel<>());
                get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
                get(proto().name()).asWidget().getElement().getStyle().setTextAlign(TextAlign.LEFT);
                get(proto().name()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);
                get(proto().name()).asWidget().addDomHandler(onAddGadgetRequested, ClickEvent.getType());

                formPanel.append(Location.Dual, proto().description(), new CLabel<>());
                get(proto().description()).asWidget().getElement().getStyle().setTextAlign(TextAlign.JUSTIFY);
                get(proto().description()).asWidget().getElement().getStyle().setWidth(100, Unit.PCT);
                get(proto().description()).asWidget().addDomHandler(onAddGadgetRequested, ClickEvent.getType());

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                String tooltip = i18n.tr("Click to add \"{0}\" gadget", getValue().name().getValue());
                get(proto().name()).asWidget().setTitle(tooltip);
                get(proto().description()).asWidget().setTitle(tooltip);
            }
        }

        public GadgetDescriptorFolder() {
            super(AddGadgetGadgetDescriptor.class);
            setAddable(false);
        }

        @Override
        protected IFolderDecorator<AddGadgetGadgetDescriptor> createFolderDecorator() {
            return new BoxFolderDecorator<AddGadgetGadgetDescriptor>(VistaImages.INSTANCE);
        }

        @Override
        public IFolderItemDecorator<AddGadgetGadgetDescriptor> createItemDecorator() {
            return new GadgetDescriptorDecorator();
        }

        @Override
        protected CForm<AddGadgetGadgetDescriptor> createItemForm(IObject<?> member) {
            return new GadgetDescriptorForm();
        }
    }

    private static final I18n i18n = I18n.get(AddGadgetDialog.class);

    private final GadgetMetadataService gadgetMetadataService;

    private final VerticalPanel body;

    private final List<GadgetDescriptorDTO> descriptors;

    private final GadgetDescriptorsListForm descriptorsListForm;

    public AddGadgetDialog(DashboardMetadata.DashboardType boardType) {
        super(i18n.tr("Available Gadgets"));
        gadgetMetadataService = GWT.<GadgetMetadataService> create(GadgetMetadataService.class);

        descriptors = new ArrayList<GadgetDescriptorDTO>();

        body = new VerticalPanel();
        body.setSize("100%", "100%");

        HTML keywordSelectionBoxLabel = new HTML(i18n.tr("Filter by keywords:"));
        keywordSelectionBoxLabel.getElement().getStyle().setProperty("fontWeight", "bold");

        body.add(keywordSelectionBoxLabel);
        final KeywordsBox keywordsSelectionBox = new KeywordsBox() {
            @Override
            protected void onKeywordsChanged(Set<String> keywords) {
                AddGadgetDialog.this.updateDescriptorsForm(keywords);
            }
        };
        body.add(keywordsSelectionBox);

        descriptorsListForm = new GadgetDescriptorsListForm();
        descriptorsListForm.init();
        ScrollPanel descriptorListScroller = new ScrollPanel();
        descriptorListScroller.setSize("100%", "300px");
        descriptorListScroller.setWidget(descriptorsListForm);
        body.add(descriptorListScroller);

        gadgetMetadataService.listAvailableGadgets(new DefaultAsyncCallback<Vector<GadgetDescriptorDTO>>() {
            @Override
            public void onSuccess(Vector<GadgetDescriptorDTO> result) {
                descriptors.clear();
                descriptors.addAll(result);

                Set<String> keywords = new HashSet<String>();
                for (GadgetDescriptorDTO descriptor : result) {
                    keywords.addAll(descriptor.getKeywords());
                }
                keywordsSelectionBox.setKeywords(keywords, true);
            }
        }, boardType);
        setBody(body);
        setDialogPixelWidth(600);
    }

    @Override
    public boolean onClickOk() {
        return true;
    }

    @Override
    public String optionTextOk() {
        return i18n.tr("Close");
    }

    protected abstract void onAddGadget(GadgetMetadata gadgetMetadata);

    private void addGadget(GadgetMetadata proto) {
        GWT.<GadgetMetadataService> create(GadgetMetadataService.class).createGadgetMetadata(new DefaultAsyncCallback<GadgetMetadata>() {

            @Override
            public void onSuccess(GadgetMetadata result) {
                onAddGadget(result);
            }

        }, proto);
    }

    private void updateDescriptorsForm(Set<String> keywords) {
        List<GadgetDescriptorDTO> filteredDescriptors = new ArrayList<GadgetDescriptorDTO>();
        if (keywords == null || keywords.isEmpty()) {
            filteredDescriptors.addAll(descriptors);
        } else {
            for (GadgetDescriptorDTO descriptor : descriptors) {
                boolean isFiltered = true;
                for (String keyword : keywords) {
                    if (!descriptor.getKeywords().contains(keyword)) {
                        isFiltered = false;
                        break;
                    }
                }
                if (isFiltered) {
                    filteredDescriptors.add(descriptor);
                }
            }
        }

        AddGadgetGadgetDescriptorContainer descriptorsContainerEntity = EntityFactory.create(AddGadgetGadgetDescriptorContainer.class);
        for (GadgetDescriptorDTO descriptor : filteredDescriptors) {
            AddGadgetGadgetDescriptor descriptorEntity = EntityFactory.create(AddGadgetGadgetDescriptor.class);
            descriptorEntity.name().setValue(descriptor.getName());
            descriptorEntity.description().setValue(descriptor.getDescription());
            descriptorEntity.gadgetMetadataProto().set(descriptor.getProto());
            descriptorsContainerEntity.descriptors().add(descriptorEntity);
        }
        descriptorsListForm.populate(descriptorsContainerEntity);
    }
}
