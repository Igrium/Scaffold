package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.metaversemedia.scaffold.level.entity.Entity;
import org.reflections.Reflections;

import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;

/**
 * Browser that can browse classes.
 * @author Sam54123
 * @param <T>
 */
public class ClassBrowser<T> extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField searchBar;
	
	private Class<T> parentClass;
	private Reflections reflection;
	private JList<String> classList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ClassBrowser<Entity> dialog = new ClassBrowser<Entity>(Entity.class);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param parentClass Only display children of this class.
	 */
	public ClassBrowser(Class<T> parentClass) {
		reflection = new Reflections();
		this.parentClass = parentClass;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			Box horizontalBox = Box.createHorizontalBox();
			contentPanel.add(horizontalBox, BorderLayout.NORTH);
			{
				searchBar = new JTextField();
				horizontalBox.add(searchBar);
				searchBar.setColumns(10);
			}
			{
				JButton btnSearch = new JButton("Search");
				horizontalBox.add(btnSearch);
			}
		}
		{
			classList = new JList<String>();
			contentPanel.add(classList, BorderLayout.CENTER);
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
			
			reload();
		}
	}
	
	/**
	 * Reload the classes being shown.
	 */
	public void reload() {
		Set<Class<? extends T>> classes = reflection.getSubTypesOf(parentClass);
		
		List<String> classStrings = new ArrayList<String>();
		for (Class<? extends T> c: classes) {
			classStrings.add(c.getSimpleName());
		}
		Collections.sort(classStrings);
		
		// Narrow down search
		if (!getSearchBar().getText().matches("")) {
			
		}
		
		classList.setListData(classStrings.toArray(new String[0]));
		repaint();
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

	protected JList<String> getClassList() {
		return classList;
	}
	protected JTextField getSearchBar() {
		return searchBar;
	}
}
