package ca.wlu.gisql.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class BusyDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -3519155352196497820L;

    private JButton cancel = new JButton("Abort");

    private JLabel label = new JLabel("Computing result set...");

    private JProgressBar progress = new JProgressBar();

    public BusyDialog(JFrame parent) {
	super(parent, "Working - gisQL", true);
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	setCursor(new Cursor(Cursor.WAIT_CURSOR));
	setResizable(false);

	progress.setIndeterminate(true);
	progress.setBorder(BorderFactory.createCompoundBorder(BorderFactory
		.createEmptyBorder(10, 10, 10, 10), progress.getBorder()));
	label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	getContentPane().setLayout(
		new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
	getContentPane().add(label);
	getContentPane().add(progress);
	getContentPane().add(cancel);

	pack();
    }

    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource() == cancel) {
	    System.exit(0);
	}

    }

}
