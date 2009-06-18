package ca.wlu.gisql.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.combobox.EnumComboBoxModel;

import ca.wlu.gisql.interactome.output.FileFormat;

class ExportAccessory extends JPanel {

	private static final long serialVersionUID = 6631121744181456433L;

	private final JComboBox format = new JComboBox();

	private final JLabel formatlabel = new JLabel("File format:");

	private EnumComboBoxModel<FileFormat> model = new EnumComboBoxModel<FileFormat>(
			FileFormat.class);

	public ExportAccessory() {
		GridBagConstraints gridBagConstraints;

		setLayout(new GridBagLayout());

		formatlabel.setText("Format:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 5;
		add(formatlabel, gridBagConstraints);

		format.setModel(model);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(format, gridBagConstraints);
	}

	public FileFormat getFormat() {
		return model.getSelectedItem();
	}
}
