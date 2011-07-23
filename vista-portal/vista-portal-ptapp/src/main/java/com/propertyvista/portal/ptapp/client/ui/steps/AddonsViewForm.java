/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
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
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Pet.WeightUnit;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.dto.AddOnsDTO;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class AddonsViewForm extends CEntityForm<AddOnsDTO> {

    private static I18n i18n = I18nFactory.getI18n(AddonsViewForm.class);

    private boolean summaryViewMode = false;

    private int maxPets;

    public AddonsViewForm() {
        super(AddOnsDTO.class);
    }

    public AddonsViewForm(IEditableComponentFactory factory) {
        super(AddOnsDTO.class, factory);
        summaryViewMode = true;
    }

    public boolean isSummaryViewMode() {
        return summaryViewMode;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new VistaHeaderBar(proto().pets().pets()));
        main.add(inject(proto().pets().pets(), createPetsEditorColumns()));

        main.add(new VistaHeaderBar(i18n.tr("Vehicles/Parking")));
        main.add(inject(proto().vehicles(), createVehicleEditorColumns()));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<AddOnsDTO>() {

            @Override
            public boolean isValid(CEditableComponent<AddOnsDTO, ?> component, AddOnsDTO value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().pets().pets());
            }

            @Override
            public String getValidationMessage(CEditableComponent<AddOnsDTO, ?> component, AddOnsDTO value) {
                return i18n.tr("Duplicate pets specified");
            }
        });

        maxPets = proto().pets().pets().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<AddOnsDTO>() {

            @Override
            public boolean isValid(CEditableComponent<AddOnsDTO, ?> component, AddOnsDTO value) {
                int size = getValue().pets().pets().size();
                return (size <= maxPets) && ((value.pets().petsMaximum().isNull() || (size <= value.pets().petsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<AddOnsDTO, ?> component, AddOnsDTO value) {
                return i18n.tr("Exceeded number of allowed pets");
            }
        });

        get(proto().vehicles()).addValueValidator(new EditableValueValidator<List<Map<String, Object>>>() {

            @Override
            public boolean isValid(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().vehicles());
            }

            @Override
            public String getValidationMessage(CEditableComponent<List<Map<String, Object>>, ?> component, List<Map<String, Object>> value) {
                return i18n.tr("Duplicate vehicles specified");
            }
        });
    }

    private CEntityFolderEditor<Pet> createPetsEditorColumns() {

        return new CEntityFolderEditor<Pet>(Pet.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "14em"));
                columns.add(new EntityFolderColumnDescriptor(proto().color(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto().breed(), "13em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weight(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().weightUnit(), "4em"));
                columns.add(new EntityFolderColumnDescriptor(proto().birthDate(), "8.2em"));
                if (!isSummaryViewMode()) {
                    columns.add(new EntityFolderColumnDescriptor(proto().chargeLine(), "7em"));
                }
            }

            @Override
            protected IFolderEditorDecorator<Pet> createFolderDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<Pet>() {
                        @Override
                        public void setFolder(CEntityFolderEditor<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderEditorDecorator<Pet>(columns, PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(),
                            i18n.tr("Add a pet"));
                }

            }

            @Override
            public CEditableComponent<?, ?> create(IObject<?> member) {
                if (member instanceof ChargeLine) {
                    return new CEntityLabel();
                } else {
                    return super.create(member);
                }
            }

            @Override
            protected CEntityFolderItemEditor<Pet> createItem() {
                return new CEntityFolderRowEditor<Pet>(Pet.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator<Pet> createFolderItemDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator<Pet>(false);
                        } else {
                            return new TableFolderItemEditorDecorator<Pet>(PortalImages.INSTANCE.delRow(), PortalImages.INSTANCE.delRowHover(),
                                    i18n.tr("Remove pet"));
                        }
                    }

                    @Override
                    public void addValidations() {
                        EditableValueValidator<Integer> weightValidator = new EditableValueValidator<Integer>() {

                            @Override
                            public boolean isValid(CEditableComponent<Integer, ?> component, Integer value) {
                                return (value == null)
                                        || DomainUtil.getWeightKg(value, getValue().weightUnit().getValue()) <= AddonsViewForm.this.getValue().pets()
                                                .petWeightMaximum().getValue();
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Integer, ?> component, Integer value) {
                                return i18n.tr("Max allowed weight {0} {1} ",
                                        DomainUtil.getWeightKgToUnit(AddonsViewForm.this.getValue().pets().petWeightMaximum(), getValue().weightUnit()),
                                        getValue().weightUnit().getStringView());
                            }
                        };

                        get(proto().weight()).addValueValidator(weightValidator);
                        get(proto().weightUnit()).addValueChangeHandler(new RevalidationTrigger<WeightUnit>(get(proto().weight())));

                        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
                    }

                };
            }

            @Override
            protected void createNewEntity(Pet newEntity, AsyncCallback<Pet> callback) {
                newEntity.weightUnit().setValue(WeightUnit.lb);
                ChargesSharedCalculation.calculatePetCharges(AddonsViewForm.this.getValue().pets().petChargeRule(), newEntity);
                super.createNewEntity(newEntity, callback);
            }
        };
    }

    private CEntityFolderEditor<Vehicle> createVehicleEditorColumns() {
        return new CEntityFolderEditor<Vehicle>(Vehicle.class) {

            private List<EntityFolderColumnDescriptor> columns;

            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().plateNumber(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().year(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().make(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().model(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().country(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().province(), "16em"));
                columns.add(new EntityFolderColumnDescriptor(proto().parkingSpot(), "13em"));
                //  TODO : filter that parking spot on available spots only and from current building!..                  
            }

            @Override
            protected IFolderEditorDecorator<Vehicle> createFolderDecorator() {
                return new TableFolderEditorDecorator<Vehicle>(columns, PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(),
                        i18n.tr("Add a vehicle"));
            }

            @Override
            protected CEntityFolderItemEditor<Vehicle> createItem() {
                return createVehicleRowEditor(columns);
            }

            private CEntityFolderItemEditor<Vehicle> createVehicleRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRowEditor<Vehicle>(Vehicle.class, columns) {

                    @Override
                    public IFolderItemEditorDecorator<Vehicle> createFolderItemDecorator() {
                        return new TableFolderItemEditorDecorator<Vehicle>(PortalImages.INSTANCE.delRow(), PortalImages.INSTANCE.delRowHover(),
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
