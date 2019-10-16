package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
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
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.lang.reflect.Modifier;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Browser that can browse classes.
 * @author Sam54123
 * @param <T>
 */
public class ClassBrowser<T> extends JDialog {

	public interface ClassSelectListener<T> extends EventListener {
		public void classSelected(Class<? extends T> selectedClass);
	}
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField searchBar;
	
	private Class<T> parentClass;
	private Reflections reflection;
	private JList<ClassEntry<T>> classList;
	private JButton okButton;
	
	private ClassSelectListener<T> classListener;

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
		setTitle("Select Class");
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
				btnSearch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reload();
					}
				});
				horizontalBox.add(btnSearch);
			}
		}
		{
			classList = new JList<ClassEntry<T>>();
			classList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					okButton.setEnabled(classList.getSelectedValue() != null);
				}
			});
			contentPanel.add(classList, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
						triggerClassSelect();
					}
				});
				okButton.setEnabled(false);
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
			
			reload();
		}
	}
	
	/**
	 * Reload the classes being shown.
	 */
	@SuppressWarnings("unchecked")
	public void reload() {
		Set<Class<? extends T>> classes = reflection.getSubTypesOf(parentClass);
		
		List<ClassEntry<T>> classStrings = new ArrayList<ClassEntry<T>>();
		for (Class<? extends T> c: classes) {
			if (!Modifier.isAbstract(c.getModifiers())) {
				classStrings.add(new ClassEntry<T>(c));
			}
		}
		Collections.sort(classStrings);
		
		// Narrow down search
		if (!getSearchBar().getText().matches("")) {
			for (int i = 0; i < classStrings.size(); i++) {
				if (!classStrings.get(i).classObj.getSimpleName().toLowerCase()
						.contains(getSearchBar().getText().toLowerCase())) {
					classStrings.remove(i);
					i--;
				}
			}
		}

		classList.setListData(classStrings.toArray(new ClassEntry[0]));
		repaint();
	}
	
	/**
	 * Set the ClassSelectListener to call when a class is selected.
	 * @param classSelectListener New ClassSelectListener.
	 */
	public void setClassSelectListener(ClassSelectListener<T> classSelectListener) {
		this.classListener = classSelectListener;
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	
	protected void triggerClassSelect() {
		if (classListener != null && classList.getSelectedValue() != null) {
			classListener.classSelected(classList.getSelectedValue().classObj);
		}
	}

	protected JList<ClassEntry<T>> getClassList() {
		return classList;
	}
	protected JTextField getSearchBar() {
		return searchBar;
	}
	protected JButton getOkButton() {
		return okButton;
	}
}

class ClassEntry<T> implements Comparable<ClassEntry<?>> {
	public final Class<? extends T> classObj;
	
	public ClassEntry(Class<? extends T> classObj) {
		this.classObj = classObj;
	}
	
	public String toString() {
		return classObj.getSimpleName();
	}

	@Override
	public int compareTo(ClassEntry<?> o) {
		return toString().compareTo(o.toString());
	}
	
}
