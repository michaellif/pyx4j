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
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.ProvincePolicyNode;

public abstract class PolicyDTOTabPanelBasedForm<POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOTabPanelBasedForm.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // This list MUST be ordered in descending order by the NodeType hierarchy
    private static final List<NodeType<?>> AVAILABLE_NODE_TYPES = Arrays.<NodeType<?>> asList( //
            new NodeType.Builder(OrganizationPoliciesNode.class).hasOnlyOneInstance().build(), //
            new NodeType.Builder(ProvincePolicyNode.class).build(), //
            new NodeType.Builder(Complex.class).build(), //
            new NodeType.Builder(Building.class).build() //
            );

    private final CComboBox<NodeType<?>> selectPolicyScopeBox = new CComboBox<NodeType<?>>(true);

    public PolicyDTOTabPanelBasedForm(Class<POLICY_DTO> policyDTOClass, final IPrimeFormView<POLICY_DTO, ?> view) {
        super(policyDTOClass, view);

        selectPolicyScopeBox.setMandatory(true);
        selectPolicyScopeBox.setViewable(isViewable());

        selectTab(addTab(createScopeTab(), i18n.tr("Scope")));
    }

    private IsWidget createScopeTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, selectPolicyScopeBox).decorate().componentWidth(200).customLabel(i18n.tr("Scope"));
        formPanel.append(Location.Dual, proto().node(), new PolicyNodeEditor());

        // add value change handler that resets the node when node type is changed
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeType<?>>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeType<?>> event) {
                get(proto().node()).reset();

                if (event.getValue() != null) {
                    Class<? extends PolicyNode> selectedNodeType = event.getValue().getType();
                    if (!selectedNodeType.equals(OrganizationPoliciesNode.class)) {
                        get(proto().node()).populate(EntityFactory.create(selectedNodeType));
                        get(proto().node()).setVisible(true);
                    }
                }
            }
        });

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (populate) {
            populateScopeSelector();
        }
    }

    private void populateScopeSelector() {
        NodeType<?> policyScope = null;
        Class<?> populatedType = null;
        if (getValue() != null && !getValue().node().isNull()) {
            populatedType = getValue().node().getInstanceValueClass();
        }
        for (NodeType<?> nodeType : AVAILABLE_NODE_TYPES) {
            if (nodeType.getType().equals(populatedType)) {
                policyScope = nodeType;
                break;
            }
        }
        selectPolicyScopeBox.reset();
        selectPolicyScopeBox.populate(policyScope);
        selectPolicyScopeBox.setOptions(assignableTypes());
        selectPolicyScopeBox.setEditable(isNewEntity());
    }

    private boolean isNewEntity() {
        return getValue().getPrimaryKey() == null;
    }

    private Collection<NodeType<?>> assignableTypes() {
        List<NodeType<?>> assignableTypes = null;

        if (getValue().lowestNodeType().isNull()) {
            assignableTypes = AVAILABLE_NODE_TYPES;
        } else {
            assignableTypes = new ArrayList<NodeType<?>>();
            for (NodeType<?> t : AVAILABLE_NODE_TYPES) {
                assignableTypes.add(t);
                if (t.getType().getName().equals(getValue().lowestNodeType().getValue())) {
                    break;
                }
            }
        }

        if (isNewEntity() && getValue().organizationNodeUsed().getValue(false)) {
            // remove Organization node type if already used:
            assignableTypes.remove(AVAILABLE_NODE_TYPES.get(0));
        }

        return assignableTypes;
    }

    @Override
    public boolean isValid() {
        if (selectPolicyScopeBox != null) {
            return (selectPolicyScopeBox.isValid() && super.isValid());
        }
        return super.isValid();
    }

    @Override
    public void setVisited(boolean visited) {
        if (selectPolicyScopeBox != null) {
            selectPolicyScopeBox.setVisited(visited);
        }
        super.setVisited(visited);
    }

    /**
     * A component that to choose the polymorphic PolicyNode
     *
     * @author ArtyomB
     */
    private class PolicyNodeEditor extends CForm<PolicyNode> {

        private final Map<Class<? extends PolicyNode>, CEntityComboBox<? extends PolicyNode>> nodeTypeToComponentMap = new HashMap<>();

        private CEntityComboBox<? extends PolicyNode> currentComp = null;

        public PolicyNodeEditor() {
            super(PolicyNode.class);
            setMandatory(true);
        }

        @Override
        protected IsWidget createContent() {
            prepareNodeComponents();

            FormPanel formPanel = new FormPanel(this);
            for (CEntityComboBox<? extends PolicyNode> nodeComponent : nodeTypeToComponentMap.values()) {
                formPanel.append(Location.Left, nodeComponent).decorate().componentWidth(200).customLabel(i18n.tr("Applied to"));
            }

            return formPanel;
        }

        @Override
        public void onReset() {
            super.onReset();

            setCurrentComponent(null);
            for (CEntityComboBox<? extends PolicyNode> nodeComponent : nodeTypeToComponentMap.values()) {
                nodeComponent.setVisible(false);
                nodeComponent.reset();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (populate && setCurrentComponent(getValue()) != null) {
                ((CEntityComboBox<PolicyNode>) currentComp).populate(getValue());
                currentComp.setEditable(PolicyDTOTabPanelBasedForm.this.isNewEntity());
                if (PolicyDTOTabPanelBasedForm.this.isNewEntity()) {
                    // remove already used nodes:
                    for (PolicyNode node : PolicyDTOTabPanelBasedForm.this.getValue().usedNodes()) {
                        ((CEntityComboBox<PolicyNode>) currentComp).removeOption(node);
                    }
                }
            }
        }

        @Override
        public boolean isValid() {
            if (currentComp != null) {
                currentComp.isValid();
            }
            return true;
        }

        @Override
        public void setVisited(boolean visited) {
            if (currentComp != null) {
                currentComp.setVisited(visited);
            }
            super.setVisited(visited);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private void prepareNodeComponents() {
            nodeTypeToComponentMap.clear();
            for (NodeType<?> nodeType : AVAILABLE_NODE_TYPES) {
                if (!nodeType.hasOnlyOneInstance()) {
                    CEntityComboBox<? extends PolicyNode> comboBox = new CEntityComboBox<>(nodeType.getType());

                    comboBox.setOptionsComparator(new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            if (o1 == null || o2 == null) {
                                return o1 == null ? -1 : (o2 == null) ? 1 : 0;
                            }
                            return ((PolicyNode) o1).getStringView().compareTo(((PolicyNode) o2).getStringView());
                        }
                    });
                    comboBox.addValueChangeHandler(new ValueChangeHandler() {
                        @Override
                        public void onValueChange(ValueChangeEvent event) {
                            if (event.getValue() != null) {
                                setSelectedValue((PolicyNode) event.getValue());
                            }
                        }
                    });
                    comboBox.setEditable(isEditable());
                    comboBox.setViewable(isViewable());
                    comboBox.setMandatory(true);
                    comboBox.setVisible(false);

                    nodeTypeToComponentMap.put(nodeType.getType(), comboBox);
                }
            }
        }

        private void setSelectedValue(PolicyNode value) {
            super.setValue(value);
        }

        private CEntityComboBox<? extends PolicyNode> setCurrentComponent(PolicyNode value) {
            if (currentComp != null) {
                currentComp.setVisible(false);
                abandon(currentComp);
                currentComp = null;
            }

            if (value != null) {
                @SuppressWarnings("unchecked")
                Class<? extends PolicyNode> curType = (Class<? extends PolicyNode>) value.getInstanceValueClass();
                for (Class<? extends PolicyNode> nodeType : nodeTypeToComponentMap.keySet()) {
                    if (nodeType.equals(curType)) {
                        adopt(currentComp = nodeTypeToComponentMap.get(nodeType));
                        currentComp.setVisible(true);
                        break;
                    }
                }
            }

            return currentComp;
        }
    }

    private static class NodeType<T extends PolicyNode> {

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
