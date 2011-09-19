/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppEntityFolder;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppTableFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppTableFolderItemDecorator;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityForm<ApartmentInfoDTO> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);
        VistaDecoratorsFlowPanel part;

        main.add(new VistaHeaderBar(i18n.tr("General Info")));
        main.add(part = new VistaDecoratorsFlowPanel(true));
        part.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        part.add(inject(proto().name()), 20);

        main.add(new VistaLineSeparator(100, Unit.PCT));

        VistaDecoratorsSplitFlowPanel split;
        main.add(split = new VistaDecoratorsSplitFlowPanel(true, 10, 25));
        split.getElement().getStyle().setPaddingLeft(2, Unit.EM);

        split.getLeftPanel().add(inject(proto().suiteNumber()), 15);

        split.getRightPanel().add(inject(proto().bedrooms()), 15);
        split.getRightPanel().add(inject(proto().bathrooms()), 15);

        main.add(new VistaHeaderBar(i18n.tr("Lease Terms")));
        main.add(part = new VistaDecoratorsFlowPanel(true));
        part.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        part.add(inject(proto().leaseFrom()), 8);
        part.add(inject(proto().leaseTo()), 8);
        part.add(inject(proto().unitRent()), 8);

        main.add(new VistaHeaderBar(i18n.tr(i18n.tr("Promotions, Discounts and Concessions"))));
        main.add(inject(proto().concessions(), createConcessionsFolderEditor()));

        main.add(new VistaHeaderBar(i18n.tr("Utilities")));
        main.add(split = new VistaDecoratorsSplitFlowPanel(true, 10, 15));

        split.getLeftPanel().add(new HTML(HtmlUtils.h6(i18n.tr("Included:"))));
        split.getLeftPanel().add(inject(proto().includedUtilities(), createUtilitiesFolderEditor()));

        split.getRightPanel().add(new HTML(HtmlUtils.h6(i18n.tr("Excluded:"))));
        split.getRightPanel().add(inject(proto().excludedUtilities(), createUtilitiesFolderEditor()));

        main.add(new VistaHeaderBar(i18n.tr("Add-ons")));
        main.add(inject(proto().agreedAddOns(), createFeaturesFolderEditor()));
        main.add(inject(proto().availableAddOns(), createFeaturesFolderEditor()));

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        content.add(main);
        content.add(new BuildingPicture());
        return content;
    }

    private CEntityFolderEditor<ServiceItemType> createUtilitiesFolderEditor() {
        return new PtAppEntityFolder<ServiceItemType>(ServiceItemType.class, i18n.tr("Utility"), false) {
            private final PtAppEntityFolder<ServiceItemType> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "30"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<ServiceItemType> createFolderDecorator() {
                PtAppTableFolderDecorator<ServiceItemType> decor = new PtAppTableFolderDecorator<ServiceItemType>(columns(), parent);
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<ServiceItemType> createItem() {
                return new CEntityFolderRowEditor<ServiceItemType>(ServiceItemType.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceItemType> createFolderItemDecorator() {
                        return new PtAppTableFolderItemDecorator<ServiceItemType>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject() == proto().name()) {
                            return inject(column.getObject(), new CLabel());
                        }
                        return super.createCell(column);
                    }
                };
            }
        };
    }

    private CEntityFolderEditor<ServiceItem> createFeaturesFolderEditor() {
        return new PtAppEntityFolder<ServiceItem>(ServiceItem.class, i18n.tr("Item"), false) {
            private final PtAppEntityFolder<ServiceItem> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().price(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "30em"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<ServiceItem> createFolderDecorator() {
                PtAppTableFolderDecorator<ServiceItem> decor = new PtAppTableFolderDecorator<ServiceItem>(columns(), parent);
//                decor.setShowHeader(false);
                return decor;
            }
        };
    }

    private CEntityFolderEditor<Concession> createConcessionsFolderEditor() {
        return new PtAppEntityFolder<Concession>(Concession.class, i18n.tr("Concession"), false) {
            private final PtAppEntityFolder<Concession> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().term(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().condition(), "10em"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<Concession> createFolderDecorator() {
                PtAppTableFolderDecorator<Concession> decor = new PtAppTableFolderDecorator<Concession>(columns(), parent);
//                decor.setShowHeader(false);
                return decor;
            }
        };
    }
}
