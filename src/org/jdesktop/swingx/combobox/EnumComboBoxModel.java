/*
 * $Id: EnumComboBoxModel.java,v 1.5 2006/04/18 23:43:30 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.combobox;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * @author joshy
 */
@SuppressWarnings("serial")
public class EnumComboBoxModel<E extends Enum<E>> extends AbstractListModel
		implements ComboBoxModel {
	private List<E> list;

	private E selected = null;

	public EnumComboBoxModel(Class<E> en) {
		EnumSet<E> ens = EnumSet.allOf(en);
		list = new ArrayList<E>(ens);
		selected = list.get(0);
	}

	public E getElementAt(int index) {
		return list.get(index);
	}

	public E getSelectedItem() {
		return selected;
	}

	public int getSize() {
		return list.size();
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(Object anItem) {
		try {
			selected = (E) anItem;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(anItem
					+ " not of the expected type", e);
		}
		this.fireContentsChanged(this, 0, getSize());
	}
}
