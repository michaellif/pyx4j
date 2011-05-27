/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 26, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

public class ApartmentDetailsForm extends CEntityForm<PropertyDetailsDTO> implements ApartmentDetailsView {

    private ApartmentDetailsView.Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(ApartmentDetailsForm.class);

    public ApartmentDetailsForm() {
        super(PropertyDetailsDTO.class);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(PropertyDetailsDTO property) {
        super.populate(property);
    }

    @Override
    public IsWidget createContent() {

        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        DecorationData readOnlyDecor = new DecorationData(14d, 12);
        readOnlyDecor.editable = false;
        container.add(new VistaWidgetDecorator(inject(proto().address().street1()), readOnlyDecor));
        container.add(new VistaWidgetDecorator(inject(proto().address().city()), readOnlyDecor));
        container.add(new VistaWidgetDecorator(inject(proto().price()), readOnlyDecor));
        //TODO new decorator is required
        // container.add(inject(proto().details()));
        //  container.add(inject(proto().floorplans()));

        return container;

    }

    public Presenter getPresenter() {
        return presenter;

    }

/*
 * private CEntityFolderItem<FloorplanDTO> createUnitRowViewer(final
 * List<EntityFolderColumnDescriptor> columns) {
 * return new CEntityFolderRow<FloorplanDTO>(FloorplanDTO.class, columns) {
 * 
 * @Override
 * public FolderItemDecorator createFolderItemDecorator() {
 * return new TableFolderItemDecorator(PortalImages.INSTANCE.delRow(),
 * PortalImages.INSTANCE.delRowHover(), i18n.tr("Details"));
 * }
 * 
 * };
 * }
 */
}
