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
package com.propertyvista.portal.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;

import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.domain.ptapp.Vehicle;

public class PersonalInfoForm extends CEntityForm<ResidentDTO> implements PersonalInfoView {

    private final DecorationData decor;

    private PersonalInfoView.Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(PersonalInfoForm.class);

    public PersonalInfoForm() {
        super(ResidentDTO.class);
        decor = new DecorationData(10d, 20);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        //contact details
        container.add(new VistaHeaderDecorator(i18n.tr("Contact Details"), "100%"));
        container.add(new VistaWidgetDecorator(inject(proto().name().firstName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().middleName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().name().lastName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().homePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().mobilePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().workPhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().email()), decor));
        //Emergency Contact
        container.add(new VistaHeaderDecorator(i18n.tr("Emergency Contact"), "100%"));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().name().firstName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().name().middleName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().name().lastName()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().homePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().mobilePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().mobilePhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().workPhone()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().address().streetNumber()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().address().city()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().address().province()), decor));
        container.add(new VistaWidgetDecorator(inject(proto().emergencyContact().address().postalCode()), decor));
        //Vehicles
        container.add(new VistaHeaderDecorator(i18n.tr("Vehicles"), "100%"));
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

    private CEntityFolderEditor<Vehicle> createVehicleFolderEditorColumns() {
        return new CEntityFolderEditor<Vehicle>(Vehicle.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().country(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "17em"));
            }

            @Override
            protected IFolderEditorDecorator<Vehicle> createFolderDecorator() {
                return new TableFolderEditorDecorator<Vehicle>(columns, PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(),
                        i18n.tr("Add a vehicle"), true);
            }

            @Override
            protected CEntityFolderItemEditor<Vehicle> createItem() {
                return createVehicleRowEditor(columns);
            }

            private CEntityFolderItemEditor<Vehicle> createVehicleRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRowEditor<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator createFolderItemDecorator() {
                        return new TableFolderItemEditorDecorator(PortalImages.INSTANCE.delRow(), PortalImages.INSTANCE.delRowHover(),
                                i18n.tr("Remove vehicle"));
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
