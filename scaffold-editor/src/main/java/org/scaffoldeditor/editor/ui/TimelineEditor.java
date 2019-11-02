package org.scaffoldeditor.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;
import org.scaffoldeditor.scaffold.logic.timeline.Timeline;
import org.scaffoldeditor.scaffold.logic.timeline.TimelineEvent;
import org.scaffoldeditor.scaffold.util.JSONUtils;

import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

/**
 * Dialog which can edit a timeline.
 */
public class TimelineEditor extends JDialog {
	
	/**
	 * Represents a single row, one event, in the Timeline Editor.
	 */
	protected class EventEditor extends JPanel implements Comparable<EventEditor> {

		private static final long serialVersionUID = 1L;
		
		/**
		 * Keep track of old event in case event was not updated.
		 */
		private TimelineEvent oldEvent;
		
		JTextField frame;
		JTextField name;
		
		/**
		 * Create an event editor.
		 * @param frame Default frame.
		 * @param name Default name.
		 */
		public EventEditor(int frame, String name) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
			this.frame = new JTextField(String.valueOf(frame));
			this.add(this.frame);
			
			this.name = new JTextField(name);
			this.add(this.name);
		};
		
		/**
		 * Create an event editor with an existing event.
		 * @param defaultEvent Default event.
		 */
		public EventEditor(TimelineEvent defaultEvent) {
			this(defaultEvent.frame, defaultEvent.name);
		}
		
		/**
		 * Generate a timeline event based on the values in the editor's fields.
		 * @return
		 */
		public TimelineEvent getEvent() {
			int frame = getFrame();
			String name = this.name.getText();
			
			if (oldEvent != null && frame == oldEvent.frame && name.equals(oldEvent.name)) {
				return oldEvent;
			} else {
				oldEvent = new TimelineEvent(frame, name);
				return oldEvent;
			}
		}
		
		public int getFrame() {
			return Integer.valueOf(this.frame.getText());
		}
		
		public String getName() {
			return this.name.getText();
		}

		@Override
		public int compareTo(EventEditor o) {
			return getFrame() - o.getFrame();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private Timeline timeline;
	
	protected List<EventEditor> eventEditors = new ArrayList<EventEditor>();
	private JPanel eventPanel;
	
	/**
	 * The file of the timeline.
	 */
	protected File sourceFile;

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
				eventPanel = new JPanel();
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
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						save();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Open the timeline editor.
	 * @param timeline Timeline to edit.
	 */
	public void open(Timeline timeline) {
		setTimeline(timeline);
		setVisible(true);
	}
	
	/**
	 * Open the timeline editor.
	 * @param file File to edit.
	 */
	public void open(File file) {
		setSourceFile(file);
		reload();
		setVisible(true);
	}
	
	/**
	 * Set the timeline this editor is editing.
	 * @param timeline New timeline.
	 */
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
		eventEditors.clear();
		
		for (TimelineEvent e : timeline) {
			eventEditors.add(new EventEditor(e));
		}
		
		resort();
	}
	
	/**
	 * Get the timeline this editor is editing.
	 * @return Timeline.
	 */
	public Timeline getTimeline() {
		return timeline;
	}
	
	/**
	 * Save the current editor data out to the timeline.
	 */
	public void save() {
		timeline.clear();
		for (EventEditor e : eventEditors) {
			timeline.put(e.getEvent());
		}
		
		// Save to file
		if (sourceFile != null) {
			try {
				if (!sourceFile.exists()) {
					sourceFile.createNewFile();
				}
				FileWriter writer = new FileWriter(sourceFile);
				timeline.serialize().write(writer);
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reload the timeline from the current source file.
	 */
	public void reload() {
		try {
			JSONObject timelineObj = JSONUtils.loadJSON(sourceFile.toPath());
			setTimeline(Timeline.unserialize(timelineObj));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the file the editor will save to without reloading the timeline.
	 * @param file Source file.
	 */
	public void setSourceFile(File file) {
		sourceFile = file;
	}
	
	/**
	 * Get the file the editor will save to.
	 * @return Source file.
	 */
	public File getSourceFile() {
		return sourceFile;
	}
	
	/**
	 * Reload the event panel.
	 */
	private void reloadPanel() {
		getEventPanel().removeAll();
		for (EventEditor e : eventEditors) {
			getEventPanel().add(e);
		}
	}
	
	/**
	 * Reorganize the events by frame order
	 */
	private void resort() {
		Collections.sort(eventEditors);
		reloadPanel();
	}
	
	protected JPanel getEventPanel() {
		return eventPanel;
	}
}
