/*
 *  Copyright (C) 2014  Alfons Wirtz  
 *   website www.freerouting.net
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License at <http://www.gnu.org/licenses/> 
 *   for more details.
 *
 * ViaRuleWindow.java
 *
 * Created on 5. April 2005, 06:29
 */

package gui.win;

import freert.rules.RuleViaInfoList;
import gui.BoardFrame;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import board.infos.BrdViaInfo;
import board.infos.BrdViaInfoList;

/**
 * Window for editing a single via rule.
 *
 * @author Alfons Wirtz
 */
public class WindowViaRule extends javax.swing.JFrame
   {
   private static final long serialVersionUID = 1L;

   private final RuleViaInfoList via_rule;

   // the list of possible vias in a rule
   private final BrdViaInfoList via_list;

   private final javax.swing.JPanel main_panel;

   private final JList<BrdViaInfo> gui_list;
   private final DefaultListModel<BrdViaInfo> list_model;

   private final java.util.ResourceBundle resources;
   
   public WindowViaRule(RuleViaInfoList p_via_rule, BrdViaInfoList p_via_list, BoardFrame p_board_frame)
      {
      this.via_rule = p_via_rule;
      this.via_list = p_via_list;

      this.resources = java.util.ResourceBundle.getBundle("gui.resources.WindowViaRule", p_board_frame.get_locale());
      this.setTitle(resources.getString("title") + " " + p_via_rule.rule_name);

      this.main_panel = new javax.swing.JPanel();
      main_panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
      main_panel.setLayout(new java.awt.BorderLayout());

      this.list_model = new javax.swing.DefaultListModel<BrdViaInfo>();
      this.gui_list = new JList<BrdViaInfo>(this.list_model);

      this.gui_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
      this.gui_list.setVisibleRowCount(10);
      javax.swing.JScrollPane list_scroll_pane = new javax.swing.JScrollPane(this.gui_list);
      list_scroll_pane.setPreferredSize(new java.awt.Dimension(200, 100));
      this.main_panel.add(list_scroll_pane, java.awt.BorderLayout.CENTER);

      // fill the list
      for (int i = 0; i < p_via_rule.via_count(); ++i)
         {
         this.list_model.addElement(p_via_rule.get_via(i));
         }

      // Add a panel with buttons for editing the via list.

      javax.swing.JPanel button_panel = new javax.swing.JPanel();
      this.main_panel.add(button_panel, java.awt.BorderLayout.SOUTH);
      java.awt.GridBagLayout gridbag = new java.awt.GridBagLayout();
      button_panel.setLayout(gridbag);
      java.awt.GridBagConstraints gridbag_constraints = new java.awt.GridBagConstraints();

      final javax.swing.JButton add_button = new javax.swing.JButton(resources.getString("append"));
      gridbag_constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
      gridbag.setConstraints(add_button, gridbag_constraints);
      add_button.setToolTipText(resources.getString("append_tooltip"));
      add_button.addActionListener(new AppendListener());
      button_panel.add(add_button);

      final javax.swing.JButton delete_button = new javax.swing.JButton(resources.getString("remove"));
      gridbag_constraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridbag.setConstraints(delete_button, gridbag_constraints);
      delete_button.setToolTipText(resources.getString("remove_tooltip"));
      delete_button.addActionListener(new DeleteListener());
      button_panel.add(delete_button);

      final javax.swing.JButton move_up_button = new javax.swing.JButton(resources.getString("move_up"));
      gridbag_constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
      gridbag.setConstraints(move_up_button, gridbag_constraints);
      move_up_button.setToolTipText(resources.getString("move_up_tooltip"));
      move_up_button.addActionListener(new MoveUpListener());
      button_panel.add(move_up_button);

      final javax.swing.JButton move_down_button = new javax.swing.JButton(resources.getString("move_down"));
      gridbag_constraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridbag.setConstraints(move_down_button, gridbag_constraints);
      move_down_button.setToolTipText(resources.getString("move_down_tooltip"));
      move_down_button.addActionListener(new MoveDownListener());
      button_panel.add(move_down_button);

      p_board_frame.set_context_sensitive_help(this, "WindowVia_EditViaRule");

      this.add(main_panel);
      this.pack();
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.setVisible(true);
      }

   /**
    * Swaps the position of the vias with index p_1 and p_2.
    */
   private void swap_position(int p_1, int p_2)
      {
      BrdViaInfo via_1 = (BrdViaInfo) this.list_model.get(p_1);
      BrdViaInfo via_2 = (BrdViaInfo) this.list_model.get(p_2);
      if (via_1 == null || via_2 == null)
         {
         return;
         }
      this.list_model.set(p_1, via_2);
      this.list_model.set(p_2, via_1);
      this.via_rule.swap(via_1, via_2);
      }

   private class AppendListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         Object[] possible_values = new Object[via_list.count() - via_rule.via_count()];
         if (possible_values.length <= 0)
            {
            return;
            }
         int curr_index = 0;
         for (int i = 0; i < via_list.count(); ++i)
            {
            BrdViaInfo curr_via = via_list.get(i);
            if (!via_rule.contains(curr_via))
               {
               if (curr_index >= possible_values.length)
                  {
                  System.out.println("ViaRuleWindow.AppendListener.actionPerformed: index inconsistent");
                  break;
                  }
               possible_values[curr_index] = curr_via;
               ++curr_index;
               }
            }
         assert (curr_index == possible_values.length);
         Object selected_value = javax.swing.JOptionPane.showInputDialog(null, resources.getString("choose_via_to_append"), resources.getString("append_via_to_rule"),
               javax.swing.JOptionPane.INFORMATION_MESSAGE, null, possible_values, possible_values[0]);
         if (selected_value != null)
            {
            BrdViaInfo selected_via = (BrdViaInfo) selected_value;
            via_rule.append_via(selected_via);
            list_model.addElement(selected_via);
            }
         }
      }

   private class DeleteListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         BrdViaInfo selected_via = (BrdViaInfo) gui_list.getSelectedValue();
         if (selected_via != null)
            {
            String message = resources.getString("remove_2") + " " + selected_via.get_name() + " " + resources.getString("from_the_rule") + " " + via_rule.rule_name + "?";
            if (WindowMessage.confirm(message))
               {
               list_model.removeElement(selected_via);
               via_rule.remove_via(selected_via);
               }
            }
         }
      }

   private class MoveUpListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         int selected_index = gui_list.getSelectedIndex();
         if (selected_index <= 0)
            {
            return;
            }
         swap_position(selected_index - 1, selected_index);
         gui_list.setSelectedIndex(selected_index - 1);
         }
      }

   private class MoveDownListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         int selected_index = gui_list.getSelectedIndex();
         if (selected_index < 0 || selected_index >= list_model.getSize() - 1)
            {
            return;
            }
         swap_position(selected_index, selected_index + 1);
         gui_list.setSelectedIndex(selected_index + 1);
         }
      }
   }
