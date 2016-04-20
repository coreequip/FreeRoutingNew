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
 * DynamicRouteState.java
 *
 * Created on 8. Dezember 2003, 10:00
 */

package interactive.state;

import interactive.Actlog;
import interactive.IteraBoard;
import interactive.LogfileScope;
import javax.swing.JPopupMenu;
import planar.PlaPointFloat;

/**
 * State for dynamic interactive routing, which is routing while moving the mouse pointer.
 *
 * @author Alfons Wirtz
 */
public final class StateRouteDynamic extends StateRoute
   {
   protected StateRouteDynamic(StateInteractive p_parent_state, IteraBoard p_board_handling, Actlog p_logfile)
      {
      super(p_parent_state, p_board_handling, p_logfile);
      }

   @Override
   public StateInteractive mouse_moved()
      {
      super.mouse_moved();
      return add_corner(i_brd.get_current_mouse_position());
      }

   /**
    * ends routing
    */
   @Override
   public StateInteractive left_button_clicked(PlaPointFloat p_location)
      {
      if (observers_activated)
         {
         i_brd.get_routing_board().end_notify_observers();
         observers_activated = false;
         }
      
      if (actlog != null)
         {
         actlog.start_scope(LogfileScope.COMPLETE_SCOPE);
         }
      
      for (int curr_net_no : route.net_no_arr)
         {
         i_brd.update_ratsnest(curr_net_no);
         }
      
      return this.return_state;
      }

   /**
    * Action to be taken when a key is pressed (Shortcut).
    */
   public StateInteractive key_typed(char p_key_char)
      {
      StateInteractive curr_return_state = this;
      if (p_key_char == 's')
         {
         i_brd.generate_snapshot();
         }
      else
         {
         curr_return_state = super.key_typed(p_key_char);
         }
      return curr_return_state;
      }

   public JPopupMenu get_popup_menu()
      {
      return i_brd.get_panel().popup_menu_dynamic_route;
      }

   public String get_help_id()
      {
      return "RouteState_DynamicRouteState";
      }
   }
