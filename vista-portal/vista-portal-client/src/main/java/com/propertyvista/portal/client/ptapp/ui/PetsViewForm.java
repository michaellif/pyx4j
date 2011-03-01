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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pet.WeightUnit;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

@Singleton
public class PetsViewForm extends CEntityForm<Pets> {

    public PetsViewForm() {
        super(Pets.class);
    }

    public PetsViewForm(EntityFormComponentFactory factory) {
        super(Pets.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(create(proto().pets(), this));
        return main;
    }

    @Override
    public CComponent<?> create(IObject<?> member, CEntityEditableComponent<?> parent) {
        if (member instanceof ChargeLine) {
            CComponent<?> comp = new CEntityLabel();
            parent.bind((CEditableComponent<?, ?>) comp, member);
            return comp;
        } else {
            return super.create(member, parent);
        }
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().pets())) {
            return createPetsEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    private CEntityFolder<Pet> createPetsEditorColumns() {
        return new CEntityFolder<Pet>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                Pet proto = EntityFactory.getEntityPrototype(Pet.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.type(), "7em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.name(), "14em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.color(), "6em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.breed(), "7em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.weight(), "7em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.weightUnit(), "5em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "7em", "0.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto.chargeLine(), "7em", "0.5em"));
            }

            @Override
            protected FolderDecorator<Pet> createFolderDecorator() {
                return new TableFolderDecorator<Pet>(columns, SiteImages.INSTANCE.addRow(), "Add a pet");
            }

            @Override
            protected CEntityFolderItem<Pet> createItem() {
                return new CEntityFolderRow<Pet>(Pet.class, columns, PetsViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove pet");
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

    private boolean hasDuplicates() {
        for (int i = 0; i < getValue().pets().size(); i++) {
            Pet pet = getValue().pets().get(i);
            for (int j = i + 1; j < getValue().pets().size(); j++) {
                Pet otherPet = getValue().pets().get(j);
                if (pet.name().equals(otherPet.name()) && pet.type().equals(otherPet.type()) && pet.birthDate().equals(otherPet.birthDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        boolean result = super.isValid();
        return result && !hasDuplicates();
    }

    @Override
    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        validationResults.appendValidationErrors(super.getValidationResults());
        if (hasDuplicates()) {
            validationResults.appendValidationError("Duplicate pets specifyed");
        }
        return validationResults;
    }
}
