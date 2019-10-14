package org.metaversemedia.scaffold.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;

import javax.swing.JTree;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NBTBrowser extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DefaultMutableTreeNode top;
	
	private CompoundMap nbt;
	
	/**
	 * Get the CompoundMap this browser is currently displaying.
	 * @return Displayed CompoundMap.
	 */
	public CompoundMap getNBT() {
		return nbt;
	}

	/**
	 * Create the dialog.
	 */
	public NBTBrowser() {
		setBounds(100, 100, 300, 600);
		setTitle("NBT Browser");
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				top = new DefaultMutableTreeNode("nbt");
				tree = new JTree(top);
				panel.add(tree, BorderLayout.CENTER);
			}
		}
	}
	
	/**
	 * Set the nbt to display.
	 * @param nbt CompoundMap to display.
	 */
	public void setNBT(CompoundMap nbt) {
		this.nbt = nbt;
		reloadTree();
	}
	
	/**
	 * Reloads the NBT tree
	 */
	public void reloadTree() {
		top.removeAllChildren();
		loadCompoundMap(nbt, top);
	}
	
	/**
	 * Load a compound map into the passed node.
	 * @param node Node to load into.
	 */
	private static void loadCompoundMap(CompoundMap map, DefaultMutableTreeNode node) {
		for (Tag<?> tag : map) {
			DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode();
			
			if (tag.getType() == TagType.TAG_COMPOUND) {
				tagNode.setUserObject(tag.getName());
				CompoundTag compoundTag = (CompoundTag) tag;
				loadCompoundMap(compoundTag.getValue(), tagNode);
				
			} else if (tag.getType() == TagType.TAG_LIST) {
				System.out.println("list");
				tagNode.setUserObject(tag.getName());
				@SuppressWarnings("unchecked")
				ListTag<Tag<?>> listTag = (ListTag<Tag<?>>) tag;
				loadList(listTag.getValue(), tagNode);
				
			} else {
				tagNode.setUserObject(tag.getName()+":"+tag.getValue());
			}
			
			node.add(tagNode);
		}
	}
	
	/**
	 * Load a list into the passed node.
	 * @param node Node to load into.
	 */
	private static void loadList(List<Tag<?>> list, DefaultMutableTreeNode node) {
		for (Tag<?> tag : list) {
			DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode();
			if (tag.getType() == TagType.TAG_COMPOUND) {
				CompoundTag compoundTag = (CompoundTag) tag;
				loadCompoundMap(compoundTag.getValue(), tagNode);
				
			} else if (tag.getType() == TagType.TAG_LIST) {
				@SuppressWarnings("unchecked")
				ListTag<Tag<?>> listTag = (ListTag<Tag<?>>) tag;
				loadList(listTag.getValue(), tagNode);
				
			} else {
				tagNode.setUserObject(tag.getValue().toString());
			}
			
			node.add(tagNode);
		}
	}

	/**
	 * Get the JTree this browser is using.
	 * @return
	 */
	protected JTree getTree() {
		return tree;
	}
	
}
