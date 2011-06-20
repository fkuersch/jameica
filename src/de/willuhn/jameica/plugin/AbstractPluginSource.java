/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/plugin/AbstractPluginSource.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/06/01 12:35:57 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.plugin;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.MultipleClassLoader;


/**
 * Abstrakte Basis-Implementierung der Plugin-Quellen.
 */
public abstract class AbstractPluginSource implements PluginSource
{
  private static List<PluginSource> sources = null;
  
  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   * Wir sortieren anhand der Ordinal-Zahl des Type-Enums.
   */
  public int compareTo(Object o)
  {
    if (!(o instanceof PluginSource))
      return -1;
    
    Type myType = this.getType();
    if (myType == null)
      return 1; // Wenn wir keinen Typ haben - dann der zuerst
    
    Type otherType = ((PluginSource)o).getType();
    if (otherType == null)
      return -1; // Wenn der keinen Typ hat - dann wir zuerst

    // Sortierung basierend auf der Ordinal-Zahl des Enums
    return myType.compareTo(otherType);
  }
  
  /**
   * Liefert die Liste der gefundenen Plugin-Quellen.
   * @return die Liste der gefundenen Plugin-Quellen.
   */
  public static synchronized List<PluginSource> getSources()
  {
    if (sources == null)
    {
      sources = new ArrayList<PluginSource>();
      
      try
      {
        MultipleClassLoader loader = Application.getClassLoader();
        Class<PluginSource>[] classes = loader.getClassFinder().findImplementors(PluginSource.class);
        for (Class<PluginSource> c:classes)
        {
          try
          {
            sources.add(c.newInstance());
          }
          catch (Exception e)
          {
            Logger.error("unable to load plugin source " + c + " - skipping",e);
          }
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to load plugin sources",e);
      }
    }
    
    return sources;
  }
}



/**********************************************************************
 * $Log: AbstractPluginSource.java,v $
 * Revision 1.1  2011/06/01 12:35:57  willuhn
 * @N Die Verzeichnisse, in denen sich Plugins befinden koennen, sind jetzt separate Klassen vom Typ PluginSource. Damit kann das kuenftig um weitere Plugin-Quellen erweitert werden und man muss nicht mehr die Pfade vergleichen, um herauszufinden, in welcher Art von Plugin-Quelle ein Plugin installiert ist
 *
 **********************************************************************/