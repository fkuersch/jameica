/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.gui.internal.action;

/**
 * Action zum Aktivieren eines Repository.
 */
public class RepositoryEnable extends AbstractRepositoryChangeState
{
  /**
   * @see de.willuhn.jameica.gui.internal.action.AbstractRepositoryChangeState#getEnabled()
   */
  @Override
  boolean getEnabled()
  {
    return true;
  }

}


