/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoForm extends CEntityEditor<ResidentDTO> implements PersonalInfoView {

    private final DecorationData decor;

    private PersonalInfoView.Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(PersonalInfoForm.class);

    public PersonalInfoForm() {
        super(ResidentDTO.class, new VistaEditorsComponentFactory());
        decor = new DecorationData(10d, 20);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        //contact details
        container.add(new VistaHeaderBar(i18n.tr("Contact Details"), "100%"));
        container.add(new VistaWidgetDecorator(inject(proto().name().firstName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().middleName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().lastName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().homePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().workPhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().email()), decor));
        //Emergency Contacts
        container.add(new VistaHeaderBar(proto().emergencyContacts(), "100%"));
        container.add(inject(proto().emergencyContacts(), createEmergencyContactFolderEditor()));
        //Vehicles
        container.add(new VistaHeaderBar(i18n.tr("Vehicles"), "100%"));
        container.add(inject(proto().vehicles(), createVehicleFolderEditorColumns()));
        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(ResidentDTO personalInfo) {
        super.populate(personalInfo);

    }

    private CEntityFolder<EmergencyContact> createEmergencyContactFolderEditor() {

        return new CEntityFolder<EmergencyContact>(EmergencyContact.class) {

            @Override
            protected IFolderDecorator<EmergencyContact> createDecorator() {
                return new BoxFolderDecorator<EmergencyContact>(PortalImages.INSTANCE, i18n.tr("Add one more contact"));
            }

            @Override
            protected CEntityFolderBoxEditor<EmergencyContact> createItem() {
                return createEmergencyContactItem();
            }

            @Override
            public void populate(IList<EmergencyContact> value) {
                super.populate(value);
                if (value.isEmpty()) {
                    addItem(); // at least one Emergency Contact should be present!..
                }
            }
        };
    }

    private CEntityFolderBoxEditor<EmergencyContact> createEmergencyContactItem() {

        return new CEntityFolderBoxEditor<EmergencyContact>(EmergencyContact.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
                main.add(inject(proto().name().firstName()), 12);
                main.add(inject(proto().name().middleName()), 12);
                main.add(inject(proto().name().lastName()), 20);
                main.add(inject(proto().homePhone()), 15);
                main.add(inject(proto().mobilePhone()), 15);
                main.add(inject(proto().workPhone()), 15);
                AddressUtils.injectIAddress(main, proto().address(), this);
                main.add(new HTML());
                return main;
            }

            @Override
            public IFolderItemDecorator createDecorator() {
                return new BoxFolderItemDecorator(PortalImages.INSTANCE, i18n.tr("Remove contact"), !isFirst());
            }
        };
    }

    private CEntityFolder<Vehicle> createVehicleFolderEditorColumns() {
        return new CEntityFolder<Vehicle>(Vehicle.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().country(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "9em"));
            }

            @Override
            protected IFolderDecorator<Vehicle> createDecorator() {
                return new TableFolderDecorator<Vehicle>(columns, PortalImages.INSTANCE, i18n.tr("Add a vehicle"));
            }

            @Override
            protected CEntityFolderItemEditor<Vehicle> createItem() {
                return createVehicleRowEditor(columns);
            }

            private CEntityFolderItemEditor<Vehicle> createVehicleRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRowEditor<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public IFolderItemDecorator createDecorator() {
                        return new TableFolderItemDecorator(PortalImages.INSTANCE, i18n.tr("Remove vehicle"));
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        if (column.getObject() == proto().year() && comp instanceof CMonthYearPicker) {
                            ((CMonthYearPicker) comp).setYearRange(new Range(1900, TimeUtils.today().getYear() + 1));
                        }
                        return comp;
                    }

                    @Override
                    public void addValidations() {
                        ProvinceContryFilters.attachFilters(get(proto().province()), get(proto().country()), new OptionsFilter<Province>() {
                            @Override
                            public boolean acceptOption(Province entity) {
                                if (getValue() == null) {
                                    return true;
                                } else {
                                    Country country = getValue().country();
                                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                                }
                            }
                        });
                    }

                };
            }
        };
    }
}
