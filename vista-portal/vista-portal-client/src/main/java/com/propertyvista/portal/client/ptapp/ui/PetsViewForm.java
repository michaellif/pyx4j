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
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.validators.BirthdayDateValidator;
import com.propertyvista.portal.client.ptapp.ui.validators.RevalidationTrigger;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pet.WeightUnit;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.EditableComponentFactory;
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
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class PetsViewForm extends CEntityForm<Pets> {

    private static I18n i18n = I18nFactory.getI18n(PetsViewForm.class);

    private boolean summaryViewMode = false;

    private int maxPets;

    public PetsViewForm() {
        super(Pets.class);
    }

    public PetsViewForm(EditableComponentFactory factory) {
        super(Pets.class, factory);
        summaryViewMode = true;
    }

    public boolean isSummaryViewMode() {
        return summaryViewMode;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().pets(), createPetsEditorColumns()));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<Pets>() {

            @Override
            public boolean isValid(CEditableComponent<Pets, ?> component, Pets value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().pets());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Pets, ?> component, Pets value) {
                return i18n.tr("Duplicate pets specified");
            }
        });

        maxPets = proto().pets().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<Pets>() {

            @Override
            public boolean isValid(CEditableComponent<Pets, ?> component, Pets value) {
                int size = getValue().pets().size();
                return (size <= maxPets) && ((value.petsMaximum().isNull() || (size <= value.petsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Pets, ?> component, Pets value) {
                return i18n.tr("Exceeded number of allowed pets");
            }
        });
    }

    private CEntityFolder<Pet> createPetsEditorColumns() {

        return new CEntityFolder<Pet>(Pet.class) {

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
            protected FolderDecorator<Pet> createFolderDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<Pet>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderDecorator<Pet>(columns, SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(), i18n.tr("Add a pet"));
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
            protected CEntityFolderItem<Pet> createItem() {
                return new CEntityFolderRow<Pet>(Pet.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        } else {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove pet"));
                        }
                    }

                    @Override
                    public void addValidations() {
                        EditableValueValidator<Integer> weightValidator = new EditableValueValidator<Integer>() {

                            @Override
                            public boolean isValid(CEditableComponent<Integer, ?> component, Integer value) {
                                return DomainUtil.getWeightKg(getValue().weight(), getValue().weightUnit()) <= PetsViewForm.this.getValue().petWeightMaximum()
                                        .getValue();
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Integer, ?> component, Integer value) {
                                return i18n.tr("Max allowed weight {0} {1} ", DomainUtil.getWeightKgToUnit(PetsViewForm.this.getValue().petWeightMaximum(),
                                        getValue().weightUnit()), getValue().weightUnit().getStringView());
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
                ChargesSharedCalculation.calculatePetCharges(PetsViewForm.this.getValue().petChargeRule(), newEntity);
                super.createNewEntity(newEntity, callback);
            }

        };

    }
}
