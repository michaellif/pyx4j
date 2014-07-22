/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.ProvincePolicyNode;

public abstract class PolicyDTOTabPanelBasedForm<POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOTabPanelBasedForm.class);

    private CComboBox<NodeType> selectPolicyScopeBox;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // This list MUST be ordered in descending order by the NodeType hierarchy
    private static final List<NodeType> AVAILABLE_NODE_TYPES = Arrays.asList( //
            new NodeType.Builder(OrganizationPoliciesNode.class).hasOnlyOneInstance().build(), //
            new NodeType.Builder(ProvincePolicyNode.class).build(), //
            new NodeType.Builder(Complex.class).build(), //
            new NodeType.Builder(Building.class).build() //
            );

    public PolicyDTOTabPanelBasedForm(Class<POLICY_DTO> policyDTOClass, final IForm<POLICY_DTO> view) {
        super(policyDTOClass, view);

        selectTab(addTab(createScopeTab(), i18n.tr("Scope")));

    }

    private IsWidget createScopeTab() {
        FormPanel formPanel = new FormPanel(this);

        selectPolicyScopeBox = new CComboBox<NodeType>();
        selectPolicyScopeBox.setMandatory(true);
        selectPolicyScopeBox.setOptions(AVAILABLE_NODE_TYPES);
        // add value change handler that resets the node when node type is changed
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeType> event) {
                if (event.getValue() != null) {
                    // disable node selector if it's a root node (Organization/PMC)
                    Class<? extends PolicyNode> selectedNodeType = event.getValue().getType();
                    boolean isOrganizationPoliciesNodeSelected = selectedNodeType.equals(OrganizationPoliciesNode.class);
                    get(proto().node()).setVisible(!isOrganizationPoliciesNodeSelected);

                    // if selected node was changed, populate the polymorphic node editor with the empty node of the correct type
                    if (isOrganizationPoliciesNodeSelected) {
                        getValue().node().set(EntityFactory.create(OrganizationPoliciesNode.class));
                    } else if (!selectedNodeType.equals(getValue().node().getInstanceValueClass())) {
                        get(proto().node()).populate(EntityFactory.create(selectedNodeType));
                    }
                } else {
                    get(proto().node()).setVisible(false);
                }
            }
        });

        formPanel.append(Location.Dual, selectPolicyScopeBox).decorate().componentWidth(200).customLabel(i18n.tr("Scope"));
        formPanel.append(Location.Dual, proto().node(), new PolicyNodeEditor());
        selectPolicyScopeBox.setEditable(false);
        selectPolicyScopeBox.setViewable(isViewable());
        get(proto().node()).setEditable(false);
        get(proto().node()).setViewable(isViewable());

        // register handler that propagates scope/node type to the scope box on form population
        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                    NodeType<?> policyScope = null;
                    Class<? extends PolicyNode> populatedType = OrganizationPoliciesNode.class;
                    if (getValue() != null && !getValue().node().isNull()) {
                        populatedType = (Class<? extends PolicyNode>) getValue().node().getInstanceValueClass();
                    }
                    for (NodeType<?> nodeType : AVAILABLE_NODE_TYPES) {
                        if (nodeType.getType().equals(populatedType)) {
                            policyScope = nodeType;
                            break;
                        }
                    }
                    if (policyScope == null) {
                        throw new Error("got unsupported or unknown policy scope:" + getValue().getInstanceValueClass().getName());
                    }

                    selectPolicyScopeBox.setValue(policyScope, true);
                    selectPolicyScopeBox.setEditable(isNewEntity());
                }
            }
        });

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        selectPolicyScopeBox.setOptions(assignableTypes(AVAILABLE_NODE_TYPES));
        selectPolicyScopeBox.setEditable(isNewEntity() && selectPolicyScopeBox.getOptions().size() > 1);

        get(proto().node()).setEditable(isNewEntity());
        if (isNewEntity()) {
            get(proto().node()).populate(EntityFactory.create(OrganizationPoliciesNode.class));
        }
    }

    private boolean isNewEntity() {
        return getValue().getPrimaryKey() == null;
    }

    private Collection<NodeType> assignableTypes(List<NodeType> availableNodeTypes) {
        List<NodeType> assignableTypes = null;
        if (getValue().lowestNodeType().isNull()) {
            assignableTypes = availableNodeTypes;
        } else {
            String lowestNodeType = getValue().lowestNodeType().getValue();
            assignableTypes = new ArrayList<PolicyDTOTabPanelBasedForm.NodeType>();
            for (NodeType<?> t : availableNodeTypes) {
                assignableTypes.add(t);
                if (t.getType().getName().equals(lowestNodeType)) {
                    break;
                }
            }
        }
        return assignableTypes;
    }

    /**
     * A component that to choose the polymorphic PolicyNode
     * 
     * @author ArtyomB
     */
    private static class PolicyNodeEditor extends CForm<PolicyNode> {

        private Map<Class<? extends PolicyNode>, CField<? extends PolicyNode, ?>> nodeTypeToComponentMap;

        public PolicyNodeEditor() {
            super(PolicyNode.class);
        }

        @Override
        public void applyEditabilityRules() {
            super.applyEditabilityRules();
            if (getValue() != null) {
                Class<? extends PolicyNode> nodeType = (Class<? extends PolicyNode>) getValue().getInstanceValueClass();
                CComponent<?, PolicyNode, ?> comp = (CComponent<?, PolicyNode, ?>) nodeTypeToComponentMap.get(nodeType);
                if (comp != null) {
                    comp.setEditable(isEditable());
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected IsWidget createContent() {
            FormPanel internalFormPanel = new FormPanel(this);

            nodeTypeToComponentMap = new HashMap<Class<? extends PolicyNode>, CField<? extends PolicyNode, ?>>();
            for (NodeType<?> nodeType : AVAILABLE_NODE_TYPES) {
                if (!nodeType.hasOnlyOneInstance()) {
                    CField<? extends PolicyNode, ?> comp = null;
                    CEntityComboBox<? extends PolicyNode> comboBox = new CEntityComboBox(nodeType.getType());
                    comboBox.addValueChangeHandler(new ValueChangeHandler() {
                        @Override
                        public void onValueChange(ValueChangeEvent event) {
                            setValue((PolicyNode) event.getValue());
                        }
                    });
                    comboBox.setOptionsComparator(new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            if (o1 == null || o2 == null) {
                                return o1 == null ? -1 : (o2 == null) ? 1 : 0;
                            }
                            return ((PolicyNode) o1).getStringView().compareTo(((PolicyNode) o2).getStringView());
                        }
                    });
                    comp = comboBox;
                    comp.setEditable(false);
                    comp.setViewable(isViewable());
                    nodeTypeToComponentMap.put(nodeType.getType(), comp);
                }
            }

            for (CField<? extends PolicyNode, ?> nodeComponent : nodeTypeToComponentMap.values()) {
                internalFormPanel.append(Location.Left, nodeComponent).decorate().componentWidth(200).customLabel(i18n.tr("Applied to"));
            }

            addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                        Class<? extends PolicyNode> curType = (Class<? extends PolicyNode>) getValue().getInstanceValueClass();
                        for (Class<? extends PolicyNode> nodeType : nodeTypeToComponentMap.keySet()) {
                            CComponent<?, ?, ?> comp = nodeTypeToComponentMap.get(nodeType);
                            boolean isCurType = curType.equals(nodeType);
                            comp.setVisible(isCurType);
                            comp.setMandatory(isCurType);
                            comp.setEditable(isCurType && isEditable());
                            if (isCurType) {
                                ((CComponent<?, PolicyNode, ?>) comp).setValue((PolicyNode) getValue().cast());
                            }
                        }
                    }
                }
            });

            return internalFormPanel;
        }
    }

    public static class NodeType<T extends PolicyNode> {

        private final String toString;

        private final Class<T> nodeType;

        private final boolean hasOnlyOneInstance;

        private NodeType(Class<T> nodeType, boolean hasOnlyOneInstance) {
            this.nodeType = nodeType;
            this.hasOnlyOneInstance = hasOnlyOneInstance;
            this.toString = EntityFactory.getEntityMeta(nodeType).getCaption();
        }

        @Override
        public String toString() {
            return toString;
        }

        public Class<T> getType() {
            return nodeType;
        }

        public boolean hasOnlyOneInstance() {
            return this.hasOnlyOneInstance;
        }

        public static class Builder<P extends PolicyNode> {

            private final Class<P> nodeType;

            private boolean hasOnlyOneInstance;

            public Builder(Class<P> nodeType) {
                this.nodeType = nodeType;
                this.hasOnlyOneInstance = false;
            }

            public Builder<P> hasOnlyOneInstance() {
                this.hasOnlyOneInstance = true;
                return this;
            }

            public Builder<P> hasManyInstances() {
                this.hasOnlyOneInstance = false;
                return this;
            }

            public NodeType<P> build() {
                return new NodeType<P>(nodeType, hasOnlyOneInstance);
            }
        }
    }
}
