package ca.wlu.gisql.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jdesktop.swingx.combobox.EnumComboBoxModel;

import ca.wlu.gisql.interactome.output.FileFormat;

class ExportAccessory extends JPanel {

	private static final long serialVersionUID = 6631121744181456433L;

	private final JComboBox format = new JComboBox();

	private final JLabel formatlabel = new JLabel("File format:");

	private final JSlider lowerbound = new JSlider();

	private final JLabel lowerboundlabel = new JLabel("Lower bound:");

	private EnumComboBoxModel<FileFormat> model = new EnumComboBoxModel<FileFormat>(
			FileFormat.class);

	private final JSlider upperbound = new JSlider();

	private final JLabel upperboundlabel = new JLabel("Upper bound:");

	public ExportAccessory() {
		GridBagConstraints gridBagConstraints;

		setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 5;
		add(upperboundlabel, gridBagConstraints);

		upperbound.setMajorTickSpacing(10);
		upperbound.setPaintTicks(true);
		upperbound.setValue(100);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(upperbound, gridBagConstraints);

		lowerboundlabel.setText("Lower bound:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		add(lowerboundlabel, gridBagConstraints);

		lowerbound.setMajorTickSpacing(10);
		lowerbound.setPaintTicks(true);
		lowerbound.setValue(0);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(lowerbound, gridBagConstraints);

		formatlabel.setText("Format:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipadx = 5;
		add(formatlabel, gridBagConstraints);

		format.setModel(model);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(format, gridBagConstraints);
	}

	public FileFormat getFormat() {
		return model.getSelectedItem();
	}

	public double getLowerbound() {
		return (Math.min(lowerbound.getValue(), upperbound.getValue())) / 100.0;
	}

	public double getUpperbound() {
		return (Math.max(lowerbound.getValue(), upperbound.getValue())) / 100.0;
	}
}
