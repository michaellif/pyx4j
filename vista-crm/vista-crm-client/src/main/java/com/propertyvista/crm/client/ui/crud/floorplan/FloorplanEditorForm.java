/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.media.CrmMediaListFolderEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanEditorForm extends CrmEntityForm<FloorplanDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public FloorplanEditorForm(IFormView<FloorplanDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public FloorplanEditorForm(IEditableComponentFactory factory, IFormView<FloorplanDTO> parentView) {
        super(FloorplanDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createGeneralTab(), i18n.tr("General"));

//        tabPanel.addDisable(((FloorplanView) getParentView()).getFeaturesListerView().asWidget(), i18n.tr("Features"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().name()), 15);
        split.getLeftPanel().add(inject(proto().marketingName()), 15);
        split.getLeftPanel().add(inject(proto().description()), 20);

        split.getRightPanel().add(inject(proto().floorCount()), 5);

        split.getRightPanel().add(inject(proto().bedrooms()), 5);
        split.getRightPanel().add(inject(proto().dens()), 5);
        split.getRightPanel().add(inject(proto().bathrooms()), 5);

        main.add(new CrmSectionSeparator(proto().amenities().getMeta().getCaption()));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        main.add(new CrmSectionSeparator(i18n.tr("Media:")));
        main.add(inject(proto().media(), new CrmMediaListFolderEditor(isEditable())));

        return new CrmScrollPanel(main);
    }

    private CEntityFolderEditor<FloorplanAmenity> createAmenitiesListEditor() {
        return new CrmEntityFolder<FloorplanAmenity>(FloorplanAmenity.class, i18n.tr("Amenity"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                return columns;
            }
        };
    }
}