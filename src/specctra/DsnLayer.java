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
 * Layer.java
 *
 * Created on 15. Mai 2004, 08:29
 */

package specctra;

import java.util.Collection;
import java.util.LinkedList;
import board.BrdLayer;

/**
 * Describes a layer in a Specctra dsn file.
 *
 * @author alfons
 */
public final class DsnLayer
   {
   // all layers of the board
   public static final DsnLayer PCB = new DsnLayer("pcb", -1, false);
   // the signal layers
   public static final DsnLayer SIGNAL = new DsnLayer("signal", -1, true);

   public final String name;
   public final int layer_no;
   public final boolean is_signal;
   public final Collection<String> net_names;
   

   /**
    * Creates a new instance of Layer. p_no is the physical layer number starting with 0 at the component side and ending at the
    * solder side. If p_is_signal, the layer is a signal layer, otherwise it is a powerground layer. For Layer objects describing
    * more than 1 layer the number is -1. p_net_names is a list of nets for this layer, if the layer is a power plane.
    */
   public DsnLayer(String p_name, int p_no, boolean p_is_signal, Collection<String> p_net_names)
      {
      name = p_name;
      layer_no = p_no;
      is_signal = p_is_signal;
      net_names = p_net_names;
      }

   /**
    * Creates a new instance of Layer. p_no is the physical layer number starting with 0 at the component side and ending at the
    * solder side. If p_is_signal, the layer is a signal layer, otherwise it is a powerground layer. For Layer objects describing
    * more than 1 layer the number is -1.
    */
   public DsnLayer(String p_name, int p_no, boolean p_is_signal)
      {
      name = p_name;
      layer_no = p_no;
      is_signal = p_is_signal;
      net_names = new LinkedList<String>();
      }

   /**
    * Writes a layer scope in the stucture scope.
    */
   public static void write_scope(DsnWriteScopeParameter p_par, int p_layer_no, boolean p_write_rule) throws java.io.IOException
      {
      p_par.file.start_scope();
      p_par.file.write("layer ");
      BrdLayer board_layer = p_par.board.layer_structure.get(p_layer_no);
      p_par.identifier_type.write(board_layer.name, p_par.file);
      p_par.file.new_line();
      p_par.file.write("(type ");
      
      if (board_layer.is_signal)
         p_par.file.write("signal)");
      else
         p_par.file.write("power)");
      
      if (p_write_rule)
         {
         DsnRule.write_default_rule(p_par, p_layer_no);
         }
      p_par.file.end_scope();
      }
   }
