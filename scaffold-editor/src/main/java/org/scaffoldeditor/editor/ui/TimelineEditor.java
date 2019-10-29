package org.scaffoldeditor.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.scaffoldeditor.scaffold.logic.timeline.TimelineEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.TextField;

import javax.swing.JList;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

/**
 * Dialog which can edit a timeline.
 */
public class TimelineEditor extends JDialog {
	
	/**
	 * Represents a single row, one event, in the Timeline Editor.
	 */
	protected class EventEditor extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private TimelineEvent oldEvent;
		
		JTextField frame;
		JTextField name;

		public EventEditor(int frame, String name) {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			this.frame = new JTextField(String.valueOf(frame));
			this.add(this.frame);
			
			this.name = new JTextField(name);
			this.add(this.name);
		};
		
		public EventEditor(TimelineEvent defaultEvent) {
			this(defaultEvent.frame, defaultEvent.name);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			TimelineEditor dialog = new TimelineEditor();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public TimelineEditor() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				JPanel eventPanel = new JPanel();
				scrollPane.setViewportView(eventPanel);
				eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
				{
					JPanel panel = new JPanel();
					FlowLayout flowLayout = (FlowLayout) panel.getLayout();
					flowLayout.setAlignment(FlowLayout.LEFT);
					eventPanel.add(panel);
					{
						textField = new JTextField();
						panel.add(textField);
						textField.setColumns(10);
					}
				}
			}
			{
				Box horizontalBox = Box.createHorizontalBox();
				scrollPane.setColumnHeaderView(horizontalBox);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
