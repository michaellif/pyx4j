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

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.portal.domain.Address;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Phone;

public class BuildingEditorForm extends CEntityForm<Building> {

    private static I18n i18n = I18nFactory.getI18n(BuildingEditorForm.class);

    public BuildingEditorForm() {
        super(Building.class, new CrmEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Details")));

        DecorationData decorData = new DecorationData(14d, 12);
        main.add(new VistaWidgetDecorator(inject(proto().name()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketingName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketingDescription()), decorData));

// TODO - add this complex data processing later! :
//        main.add(new VistaWidgetDecorator(inject(proto().complex()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().propertyCode()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().type()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().structureDescription()), decorData));

        main.add(new VistaWidgetDecorator(inject(proto().website()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().email().emailAddress()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().email().emailType()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Contact Phones")));
        main.add(inject(proto().phoneList(), createPhonesListEditor()));

        main.add(new CrmHeaderDecorator(i18n.tr("Address")));

// TODO - add this complex data processing later! :
//        main.add(inject(proto().contactsList(), createPhonesFolderEditorColumns()));
//      main.add(inject(proto().propertyProfile(), createPhonesFolderEditorColumns()));

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
        return new CEntityFolder<Phone>(Phone.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().phoneType(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().phoneNumber(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
            }

            @Override
            protected FolderDecorator<Phone> createFolderDecorator() {
                return new TableFolderDecorator<Phone>(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add phone"));
            }

            @Override
            protected CEntityFolderItem<Phone> createItem() {
                return new CEntityFolderRow<Phone>(Phone.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove phone"));
                    }
                };
            }
        };
    }
}
