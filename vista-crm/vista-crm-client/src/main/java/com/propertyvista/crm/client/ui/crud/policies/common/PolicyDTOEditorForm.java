/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.common;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.crm.client.ui.policy.PolicyEditorFormFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.domain.policy.DefaultPoliciesNode;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class PolicyDTOEditorForm<P extends Policy, POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOEditorForm.class);

    private SimplePanel policyEditorFormPanel;

    private final Class<P> policyClass;

    private final Class<POLICY_DTO> policyDTOClass;

    @SuppressWarnings("unchecked")
    private static final List<NodeType<?>> NODE_TYPE_OPTIONS = Arrays.asList(//@formatter:off
                NodeType.wrap(AptUnit.class),
                NodeType.wrap(Floorplan.class),
                NodeType.wrap(Building.class),
                NodeType.wrap(Complex.class),
                NodeType.wrap(Province.class),
                NodeType.wrap(Country.class),
                NodeType.wrap(OrganizationPoliciesNode.class));//@formatter:on

    public PolicyDTOEditorForm(Class<P> policyClass, Class<POLICY_DTO> policyDTOClass, final IEditableComponentFactory factory) {
        super(policyDTOClass, factory);
        this.policyClass = policyClass;
        this.policyDTOClass = policyDTOClass;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        final CComboBox<NodeType<?>> selectPolicyScopeBox = new CComboBox<PolicyDTOEditorForm.NodeType<?>>();
        selectPolicyScopeBox.setEditable(isEditable());
        selectPolicyScopeBox.setOptions(NODE_TYPE_OPTIONS);
        selectPolicyScopeBox.setMandatory(true);
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeType<?>>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeType<?>> event) {
                if (event.getValue() != null) {
                    get(proto().node()).setVisible(true);

                    if (!event.getValue().getType().equals(getValue().node().getInstanceValueClass())) {
                        get(proto().node()).populate(EntityFactory.create(event.getValue().getType()));
                    }
                } else {
                    get(proto().node()).setVisible(false);
//                    discard();
                }
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                    NodeType<?> selectedPolicyScope = getValue() == null || getValue().node().isNull() ? null : NodeType
                            .wrap((Class<? extends PolicyNode>) getValue().node().getInstanceValueClass());
                    if (selectedPolicyScope == null || selectedPolicyScope.getType().equals(DefaultPoliciesNode.class)) {
                        selectPolicyScopeBox.setValue(null, true);
                    } else {
                        selectPolicyScopeBox.setValue(selectedPolicyScope, true);
                    }
                }
            }
        });

        content.setWidget(++row, 0, new DecoratorBuilder(selectPolicyScopeBox).customLabel(i18n.tr("Scope")).labelWidth(8).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().node(), new PolicyNodeEditor())).customLabel(i18n.tr("Applied to")).labelWidth(8)
                .componentWidth(15).build());

        policyEditorFormPanel = new SimplePanel();
        policyEditorFormPanel.setSize("100%", "100%");
        content.setWidget(++row, 0, policyEditorFormPanel);

        return content;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // create an editor for the specific policy class and propagate the value        
        final CEntityEditor<P> policyEditorForm = (CEntityEditor<P>) PolicyEditorFormFactory.createPolicyEditorForm(policyClass, isEditable());

        //adopt(policyEditorForm); // TODO review this: i'm not sure what it does, something with validation        
        policyEditorForm.initContent();

        // use value change handler in order to propagate the value of the polymorphic editor to the parent form
        policyEditorForm.addValueChangeHandler(new ValueChangeHandler<P>() {
            @Override
            public void onValueChange(ValueChangeEvent<P> event) {

                if (event.getValue() != null) {
                    // that's really cruel but I don't see another way to do it                
                    for (String memberName : policyEditorForm.getValue().getEntityMeta().getMemberNames()) {
                        getValue().setMemberValue(memberName, event.getValue().getMemberValue(memberName));
                    }
                    setValue(getValue(), true); // just to fire the event;
                }
            }
        });

        P policyEdtiorValue = ((getValue() != null & !getValue().isNull()) ? getValue().duplicate(policyClass) : EntityFactory.create(policyClass));
        policyEditorForm.populate(policyEdtiorValue);

        policyEditorFormPanel.clear();
        policyEditorFormPanel.setWidget(policyEditorForm);
    }

    private static class NodeType<T extends PolicyNode> {
        private final String toString;

        private final Class<T> nodeType;

        public static <TYPE extends PolicyNode> NodeType<TYPE> wrap(Class<TYPE> nodeType) {
            return new NodeType<TYPE>(nodeType);
        }

        private NodeType(Class<T> nodeType) {
            this.nodeType = nodeType;
            this.toString = EntityFactory.getEntityMeta(nodeType).getCaption();
        }

        @Override
        public String toString() {
            return toString;
        }

        public Class<T> getType() {
            return nodeType;
        }
    }

    /**
     * This component just shows the string view of a node and in edit mode allows to choose the node via dialog that shows a list of nodes.
     * 
     * @author ArtyomB
     */
    private class PolicyNodeEditor extends CEntityEditor<PolicyNode> {

        public PolicyNodeEditor() {
            super(PolicyNode.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();

            final CLabel label = new CLabel();
            int col = -1;

            content.setWidget(0, ++col, label);
            if (isEditable()) {
                content.setWidget(0, ++col, new CButton(i18n.tr("Select"), new Command() {
                    @Override
                    public void execute() {
                        OkCancelDialog selectDialog = selectDialogOf(getValue().getInstanceValueClass());
                        if (selectDialog != null) {
                            selectDialog.show();
                        } else {
                            Window.alert("NOT IMPLEMENTED YET: here be node selection dialog for node type: " + (getValue().getInstanceValueClass().getName()));
                        }
                    }
                }));
            }

            addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                        label.setValue(!getValue().isEmpty() ? getValue().cast().getStringView() : i18n.tr("Nothing selected"));
                    }
                }
            });

            addValueChangeHandler(new ValueChangeHandler<PolicyNode>() {
                @Override
                public void onValueChange(ValueChangeEvent<PolicyNode> event) {
                    label.setValue(!event.getValue().isEmpty() ? event.getValue().cast().getStringView() : i18n.tr("Nothing selected"));
                }
            });
            return content;
        }

        private <E extends IEntity> OkCancelDialog selectDialogOf(Class<E> policyNodeClass) {
            if (AptUnit.class.equals(policyNodeClass)) {
                return new ListerBasedSelectDialog<AptUnit>(AptUnit.class) {
                    @Override
                    public boolean onClickOk() {
                        PolicyNodeEditor.this.setValue((AptUnit) getSelectedItem().duplicate());
                        return true;
                    }
                };
            } else if (Building.class.equals(policyNodeClass)) {

                return new ListerBasedSelectDialog<Building>(Building.class) {
                    @Override
                    public boolean onClickOk() {
                        PolicyNodeEditor.this.setValue((Building) getSelectedItem().duplicate());
                        return true;
                    }
                };
            }
            return null;
        }
    }

    private abstract static class ListerBasedSelectDialog<E extends PolicyNode> extends OkCancelDialog {

        private final IListerView<E> listerView;

        public ListerBasedSelectDialog(Class<E> clazz) {
            super(i18n.tr("Select {0}...", EntityFactory.getEntityMeta(clazz).getCaption()));
            if (clazz.equals(AptUnit.class)) {
                IListerView<AptUnit> unitListerView = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister());
                new ListerActivityBase<AptUnit>(new CrmSiteMap.Settings.Policies(), unitListerView,
                        (AbstractCrudService<AptUnit>) GWT.create(SelectUnitCrudService.class), AptUnit.class);
                this.listerView = (IListerView<E>) unitListerView;
            } else if (clazz.equals(Building.class)) {
                IListerView<Building> buildingListerView = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister());
                (new ListerActivityBase<Building>(new CrmSiteMap.Settings.Policies(), buildingListerView,
                        (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class)).populate();
                this.listerView = (IListerView<E>) buildingListerView;

            } else {
                this.listerView = null;
            }
            if (this.listerView != null) {
                setBody(this.listerView.asWidget());
            }
        }

        public E getSelectedItem() {
            return listerView.getLister().getSelectedItem();
        }
    }

}
