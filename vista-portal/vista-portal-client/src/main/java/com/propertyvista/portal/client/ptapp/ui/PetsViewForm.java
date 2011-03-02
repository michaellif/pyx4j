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
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pet.WeightUnit;
import com.propertyvista.portal.domain.pt.Pets;
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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

@Singleton
public class PetsViewForm extends CEntityForm<Pets> {

    private static I18n i18n = I18nFactory.getI18n(PetsViewForm.class);

    private boolean summaryViewMode = false;

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
        main.add(inject(proto().pets()));
        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeLine) {
            return new CEntityLabel();
        } else if (member == proto().pets()) {
            return createPetsEditorColumns();
        } else {
            return super.create(member);
        }
    }

    private CEntityFolder<Pet> createPetsEditorColumns() {
        return new CEntityFolder<Pet>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                Pet proto = EntityFactory.getEntityPrototype(Pet.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                if (isSummaryViewMode()) {
                    columns.add(new EntityFolderColumnDescriptor(proto.type(), "4em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.name(), "14em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.color(), "6em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.breed(), "10em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.weight(), "7em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.weightUnit(), "5em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "7em", "0.5em"));

                } else {
                    columns.add(new EntityFolderColumnDescriptor(proto.type(), "4em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.name(), "14em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.color(), "6em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.breed(), "10em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.weight(), "7em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.weightUnit(), "5em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "7em", "0.5em"));
                    columns.add(new EntityFolderColumnDescriptor(proto.chargeLine(), "7em", "0.5em"));
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
                    return new TableFolderDecorator<Pet>(columns, SiteImages.INSTANCE.addRow(), i18n.tr("Add a pet"));
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
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), i18n.tr("Remove pet"));
                        }
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
        // TODO use business Equals
        for (Pet item : getValue().pets()) {
            for (Pet otherItem : getValue().pets()) {
                if ((!item.equals(otherItem)) && item.name().equals(otherItem.name()) && item.type().equals(otherItem.type())
                        && item.birthDate().equals(otherItem.birthDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && !hasDuplicates();
    }

    @Override
    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        validationResults.appendValidationErrors(super.getValidationResults());
        if (hasDuplicates()) {
            validationResults.appendValidationError(i18n.tr("Duplicate pets specified"));
        }
        return validationResults;
    }
}
