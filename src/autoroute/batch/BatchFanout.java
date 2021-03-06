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
 */
package autoroute.batch;

import freert.varie.TimeLimitStoppable;
import interactive.BrdActionThread;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import autoroute.varie.ArtComponent;
import autoroute.varie.ArtPin;
import autoroute.varie.ArtResult;
import board.RoutingBoard;
import board.infos.BrdComponent;
import board.items.BrdAbitPin;

/**
 * Handles the sequencing of the fanout inside the batch autorouter.
 * 
 * @author Alfons Wirtz
 */
public final class BatchFanout
   {
   private static final String classname="BatchFanout.";
   private static final int MAX_PASS_COUNT = 20;
   
   private final BrdActionThread thread;
   private final RoutingBoard routing_board;

   private final SortedSet<ArtComponent> sorted_components = new TreeSet<ArtComponent>();

   public BatchFanout(BrdActionThread p_thread)
      {
      thread = p_thread;
      routing_board = p_thread.hdlg.get_routing_board();
      Collection<BrdAbitPin> board_smd_pin_list = routing_board.get_smd_pins();

      for (int index = 1; index <= routing_board.brd_components.count(); ++index)
         {
         BrdComponent curr_board_component = routing_board.brd_components.get(index);
         
         ArtComponent curr_component = new ArtComponent(curr_board_component, board_smd_pin_list);
         
         if (curr_component.smd_pin_count > 0)
            {
            sorted_components.add(curr_component);
            }
         }
      }

   
   public void fanout_board()
      {
      for (int i = 0; i < MAX_PASS_COUNT; ++i)
         {
         int routed_count = fanout_pass(i);
         if (routed_count == 0)  break;
         }
      }


   /**
    * Routes a fanout pass and returns the number of new fanouted SMD-pins in this pass.
    */
   private int fanout_pass(int p_pass_no)
      {
      int components_to_go = sorted_components.size();
      int routed_count = 0;
      int not_routed_count = 0;
      int insert_error_count = 0;
      int ripup_costs = thread.hdlg.itera_settings.autoroute_settings.get_start_ripup_costs() * (p_pass_no + 1);

      for (ArtComponent curr_component : sorted_components)
         {
         thread.hdlg.screen_messages.set_batch_fanout_info(p_pass_no + 1, components_to_go);
         
         for (ArtPin curr_pin : curr_component.smd_pins)
            {
            routing_board.changed_area_clear();
            
            TimeLimitStoppable time_limit = new TimeLimitStoppable( 10 + p_pass_no * 10, thread);

            ArtResult art_result;
            try
               {
               art_result = routing_board.fanout(curr_pin.board_pin, thread.hdlg.itera_settings, ripup_costs, time_limit);
               }
            catch ( Exception exc )
               {
               thread.hdlg.userPrintln(classname+"fanout_pass", exc);
               art_result = ArtResult.EXCEPTION;
               }

            if (art_result == ArtResult.ROUTED)
               {
               ++routed_count;
               }
            else if (art_result == ArtResult.NOT_ROUTED)
               {
               ++not_routed_count;
               }
            else if (art_result == ArtResult.INSERT_ERROR)
               {
               ++insert_error_count;
               }
            if (art_result != ArtResult.NOT_ROUTED)
               {
               thread.hdlg.repaint();
               }
            if (thread.is_stop_requested())
               {
               return routed_count;
               }
            }
         --components_to_go;
         }
      
      
      thread.hdlg.userPrintln("fanout pass: " + (p_pass_no + 1) + ", routed: " + routed_count + ", not routed: " + not_routed_count + ", errors: " + insert_error_count);

      return routed_count;
      }
   }
