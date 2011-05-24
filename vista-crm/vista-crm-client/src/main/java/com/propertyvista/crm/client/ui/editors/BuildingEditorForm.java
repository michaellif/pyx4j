/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Phone;
import com.propertyvista.dto.BuildingDTO;

public class BuildingEditorForm extends CEntityForm<BuildingDTO> {

    private static I18n i18n = I18nFactory.getI18n(BuildingEditorForm.class);

    public BuildingEditorForm() {
        super(BuildingDTO.class, new CrmEditorsComponentFactory());
    }

    public BuildingEditorForm(IEditableComponentFactory factory) {
        super(BuildingDTO.class, factory);
    }

    @Override
    public boolean isEditable() {
        return (this.factory instanceof CrmEditorsComponentFactory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Information")));
        DecorationData decorData = new DecorationData(14d, 12);
        main.add(new VistaWidgetDecorator(inject(proto().info().name()), decorData));
        main.add(inject(proto().info().address()));
        main.add(new VistaWidgetDecorator(inject(proto().info().propertyCode()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().type()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().shape()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().structureType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().totalStories()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().residentialStories()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Details")));
        //main.add(new VistaWidgetDecorator(inject(proto().elevators()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().boilers()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().roof()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().parkings()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().lockers()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().amenities()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().constructionType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().foundationType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().centralAir()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().centralHeat()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().floorType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().landArea()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().info().waterSupply()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Marketing")));
        main.add(new VistaWidgetDecorator(inject(proto().marketing().name()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketing().description()), decorData));
//        main.add(new VistaWidgetDecorator(inject(proto().media()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Financials")));
        main.add(new VistaWidgetDecorator(inject(proto().financial().dateAquired()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().financial().purchasePrice()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().financial().marketPrice()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().financial().lastAppraisalDate()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().financial().lastAppraisalValue()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Contact Information")));
        main.add(new VistaWidgetDecorator(inject(proto().contacts().website()), decorData));
//        main.add(new VistaWidgetDecorator(inject(proto().email()), decorData));
        main.add(inject(proto().contacts().phoneList(), createPhonesListEditor()));
        // TODO - add this complex data processing later! :
//        main.add(new VistaWidgetDecorator(inject(proto().contactsList()), decorData));

// TODO - add this complex data processing later! :
//        main.add(new VistaWidgetDecorator(inject(proto().complex()), decorData));

        main.setWidth("100%");
        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Address.class)) {
            return createAddressEditor();
        } else {
            return super.create(member);
        }
    }

    private CEntityEditableComponent<Address> createAddressEditor() {
        return new CEntityEditableComponent<Address>(Address.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                AddressUtils.injectIAddress(main, proto(), this);
                main.add(inject(proto().addressType()), 12);
                main.add(new HTML());
                return main;
            }
        };
    }

    private CEntityFolder<Phone> createPhonesListEditor() {

        AppPlace placeToGo = (isEditable() ? new CrmSiteMap.Editors.Building() : new CrmSiteMap.Viewers.Building());
        return new CrmEntityFolder<Phone>(Phone.class, "Phone", isEditable(), placeToGo) {

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().phoneType(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().phoneNumber(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
                return columns;
            }
        };
    }
}
