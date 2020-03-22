package org.scaffoldeditor.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.tag.NBTTag;
import com.github.mryurihi.tbnbt.tag.NBTTagCompound;
import com.github.mryurihi.tbnbt.tag.NBTTagList;

import javax.swing.JTree;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NBTBrowser extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DefaultMutableTreeNode top;
	
	private NBTTagCompound nbt;
	
	/**
	 * Get the CompoundMap this browser is currently displaying.
	 * @return Displayed CompoundMap.
	 */
	public NBTTagCompound getNBT() {
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
	public void setNBT(NBTTagCompound nbt) {
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
	private static void loadCompoundMap(NBTTagCompound map, DefaultMutableTreeNode node) {
		
		for (String name : map.getValue().keySet()) {
			DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode();
			NBTTag tag = map.get(name);
			
			if (tag.getTagType() == TagType.COMPOUND) {
				tagNode.setUserObject(name);
				NBTTagCompound compoundTag = tag.getAsTagCompound();
				loadCompoundMap(compoundTag, tagNode);
				
			} else if (tag.getTagType() == TagType.LIST) {
				System.out.println("list");
				tagNode.setUserObject(name);
				NBTTagList listTag = tag.getAsTagList();
				loadList(listTag, tagNode);
				
			} else {
				tagNode.setUserObject(name+":"+tag.toString());
			}
			
			node.add(tagNode);
		}
	}
	
	/**
	 * Load a list into the passed node.
	 * @param node Node to load into.
	 */
	private static void loadList(NBTTagList list, DefaultMutableTreeNode node) {
		for (NBTTag tag : list.getValue()) {
			DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode();
			if (tag.getTagType() == TagType.COMPOUND) {
				NBTTagCompound compoundTag = tag.getAsTagCompound();
				loadCompoundMap(compoundTag, tagNode);
				
			} else if (tag.getTagType() == TagType.LIST) {
				NBTTagList listTag = tag.getAsTagList();
				loadList(listTag, tagNode);
				
			} else {
				tagNode.setUserObject(tag.toString());
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
