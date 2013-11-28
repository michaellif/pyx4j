/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation.datagrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MultiSelectorCellModel {

    private final List<Object> presets;

    private final MultiSelectorState selectedState;

    private final Object selectedPreset;

    public static class MultiSelectorCellModelFactory {

        private final List<Object> presets;

        public MultiSelectorCellModelFactory(List<Object> presets) {
            this.presets = Collections.unmodifiableList(new ArrayList<Object>(presets));
        }

        public MultiSelectorCellModel makeAll() {
            return new MultiSelectorCellModel(MultiSelectorState.All, null, presets);
        }

        public MultiSelectorCellModel makeNone() {
            return new MultiSelectorCellModel(MultiSelectorState.None, null, presets);
        }

        public MultiSelectorCellModel makeSome() {
            return new MultiSelectorCellModel(MultiSelectorState.Some, null, presets);
        }

        public MultiSelectorCellModel makePreset(Object preset) {
            return new MultiSelectorCellModel(MultiSelectorState.Preset, preset, presets);
        }

        public MultiSelectorCellModel makeModel(MultiSelectorState state, Object preset) {
            return new MultiSelectorCellModel(state, state != MultiSelectorState.Preset ? null : preset, presets);
        }

    }

    private MultiSelectorCellModel(MultiSelectorState state, Object preset, List<Object> presets) {
        this.presets = presets;
        this.selectedState = state;
        this.selectedPreset = preset;
    }

    public MultiSelectorState getState() {
        return selectedState;
    }

    public Object getPreset() {
        return selectedPreset;
    }

    public List<Object> presets() {
        return presets;
    }

}
