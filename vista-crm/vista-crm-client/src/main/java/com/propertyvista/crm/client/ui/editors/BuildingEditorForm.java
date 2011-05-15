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
import com.propertyvista.common.client.ui.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.domain.Address;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.property.asset.Building;

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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class BuildingEditorForm extends CEntityForm<Building> {

    private static I18n i18n = I18nFactory.getI18n(BuildingEditorForm.class);

    public BuildingEditorForm() {
        super(Building.class, new CrmEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Building Information")));
        DecorationData decorData = new DecorationData(14d, 12);
        main.add(new VistaWidgetDecorator(inject(proto().name()), decorData));
        main.add(inject(proto().address()));
        main.add(new VistaWidgetDecorator(inject(proto().propertyCode()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().type()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().shape()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().structureType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().totalStories()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().residentialStories()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Building Details")));
        //main.add(new VistaWidgetDecorator(inject(proto().elevators()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().boilers()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().roofData()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().parkings()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().lockers()), decorData));
        //main.add(new VistaWidgetDecorator(inject(proto().amenities()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().constructionType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().foundationType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().centralAir()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().centralHeat()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().floorType()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().landArea()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().waterSupply()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Marketing")));
        main.add(new VistaWidgetDecorator(inject(proto().marketingName()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketingDescription()), decorData));
//        main.add(new VistaWidgetDecorator(inject(proto().media()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Financials")));
        main.add(new VistaWidgetDecorator(inject(proto().dateAquired()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().purchasePrice()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().marketPrice()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().lastAppraisalDate()), decorData));
        main.add(new VistaWidgetDecorator(inject(proto().lastAppraisalValue()), decorData));

        main.add(new CrmHeaderDecorator(i18n.tr("Contact Information")));
        main.add(new VistaWidgetDecorator(inject(proto().website()), decorData));
//        main.add(new VistaWidgetDecorator(inject(proto().email()), decorData));
        main.add(inject(proto().phoneList(), createPhonesListEditor()));
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

    private abstract class CEntityFolderForm<E extends IEntity> extends CEntityFolder<E> {

        private final Class<E> clazz;

        private String remove = "";

        private String add = "";

        public CEntityFolderForm(Class<E> rowClass, String remove, String add) {
            super(rowClass);
            clazz = rowClass;
            this.remove = remove;
            this.add = add;
        }

        protected abstract List<EntityFolderColumnDescriptor> columns();

        @Override
        protected CEntityFolderItem<E> createItem() {
            return new CEntityFolderRow<E>(clazz, columns()) {

                @Override
                public FolderItemDecorator createFolderItemDecorator() {
                    return new TableFolderItemDecorator(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr(remove));
                }
            };
        }

        @Override
        protected FolderDecorator<E> createFolderDecorator() {
            return new TableFolderDecorator<E>(columns(), CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr(add));
        }

    }

    private CEntityFolder<Phone> createPhonesListEditor() {

        return new CEntityFolderForm<Phone>(Phone.class, "Add phone", "Remove Phone") {

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
