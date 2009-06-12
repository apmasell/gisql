package ca.wlu.gisql.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.Interactome;

public class CommandBox extends JToolBar implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1202534133559622272L;

	private final JTextField command = new JTextField();

	private final Environment environment;
	private Interactome interactome = null;

	private ActionListener listener = null;

	private final JButton run = new JButton("Run");

	private final Separator seperator = new Separator();

	public CommandBox(Environment environment) {
		this.environment = environment;

		this.setFloatable(false);
		this.setRollover(true);

		this.add(new JLabel("Query: "));
		command.addKeyListener(this);
		this.add(command);
		this.add(seperator);

		run.setFocusable(false);
		run.addActionListener(this);
		this.add(run);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == run)
			prepareCommand();
	}

	public void clearCommand() {
		command.setText("");
		interactome = null;
	}

	public Interactome getInteractome() {
		return interactome;
	}

	public void keyPressed(KeyEvent evt) {
		if (evt.getSource() == command && evt.getKeyCode() == KeyEvent.VK_ENTER) {
			prepareCommand();
		}
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
	}

	private void prepareCommand() {
		if (command.getText().trim().length() == 0) {
			return;
		}
		Parser parser = new Parser(environment, command.getText());
		Interactome interactome = parser.get();

		if (interactome == null) {
			JOptionPane.showMessageDialog(this, parser.getErrors(),
					"Expression Error - gisQL", JOptionPane.ERROR_MESSAGE);
			return;
		}

		command.setText(interactome.show(new StringBuilder()).toString());

		this.interactome = interactome;

		if (listener != null) {
			listener
					.actionPerformed(new ActionEvent(this, 0, command.getText()));
		}

	}

	public void setActionListener(ActionListener listener) {
		this.listener = listener;
	}

}
