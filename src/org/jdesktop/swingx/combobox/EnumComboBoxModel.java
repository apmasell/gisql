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
 * <p>
 * A ComboBoxModel implementation that safely wraps an Enum. It allows the
 * developer to directly use an enum as their model for a combobox without any
 * extra work, though the display can can be further customized.
 * </p>
 * 
 * <h4>Simple Usage</h4>
 * 
 * <p>
 * The simplest usage is to wrap an <code>enum</code> inside the
 * <code>EnumComboBoxModel</code> and then set it as the model on the combo box.
 * The combo box will then appear on screen with each value in the
 * <code>enum</code> as a value in the combobox.
 * </p>
 * <p>
 * ex:
 * </p>
 * 
 * <pre>
 * &lt;code&gt;
 *  enum MyEnum { GoodStuff, BadStuff };
 *  ...
 *  JComboBox combo = new JComboBox();
 *  combo.setModel(new EnumComboBoxModel(MyEnum.class));
 * &lt;/code&gt;
 * </pre>
 * 
 * <h4>Type safe access</h4>
 * <p>
 * By using generics and co-variant types you can make accessing elements from
 * the model be completely typesafe. ex:
 *</p>
 * 
 *<pre>
 * &lt;code&gt;
 *  EnumComboBoxModel&lt;MyEnum&gt; enumModel = new EnumComboBoxModel&lt;MyEnum1&gt;(MyEnum1.class);
 *  MyEnum first = enumModel.getElement(0);
 *  MyEnum selected = enumModel.getSelectedItem();
 * /code&gt;
 * </pre>
 * 
 * <h4>Advanced Usage</h4>
 * <p>
 * Since the exact <code>toString()</code> value of each enum constant may not
 * be exactly what you want on screen (the values won't have spaces, for
 * example) you can override to toString() method on the values when you declare
 * your enum. Thus the display value is localized to the enum and not in your
 * GUI code. ex:
 * 
 * <pre>
 * &lt;code&gt;
 *    private enum MyEnum {GoodStuff, BadStuff;
 *        public String toString() {
 *           switch(this) {
 *               case GoodStuff: return &quot;Some Good Stuff&quot;;
 *               case BadStuff: return &quot;Some Bad Stuff&quot;;
 *           }
 *           return &quot;ERROR&quot;;
 *        }
 *    };
 * &lt;/code&gt;
 * </pre>
 * 
 * 
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
