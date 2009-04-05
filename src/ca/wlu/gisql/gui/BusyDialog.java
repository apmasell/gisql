package ca.wlu.gisql.gui;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class BusyDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -3519155352196497820L;

    private JButton cancel = new JButton("Abort");

    private JLabel label = new JLabel();

    private JProgressBar progress = new JProgressBar();

    public BusyDialog(JFrame parent) {
	super(parent, "Working - gisQL", true);
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	setCursor(new Cursor(Cursor.WAIT_CURSOR));
	setResizable(false);

	progress.setIndeterminate(true);
	cancel.addActionListener(this);

	GridBagConstraints gridBagConstraints;

	getContentPane().setLayout(new GridBagLayout());

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 0;
	gridBagConstraints.ipadx = 10;
	gridBagConstraints.ipady = 10;
	getContentPane().add(label, gridBagConstraints);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 1;
	gridBagConstraints.ipadx = 10;
	gridBagConstraints.ipady = 10;
	gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
	getContentPane().add(progress, gridBagConstraints);

	gridBagConstraints = new GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 2;
	gridBagConstraints.ipadx = 10;
	gridBagConstraints.ipady = 10;
	getContentPane().add(cancel, gridBagConstraints);

	pack();
    }

    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource() == cancel) {
	    System.exit(0);
	}

    }

    public void start(String message) {
	label.setText(message);
	setVisible(true);
    }

    public void stop() {
	setVisible(false);
    }

}
