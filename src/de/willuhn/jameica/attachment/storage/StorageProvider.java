/**********************************************************************
 *
 * Copyright (c) 2022 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.attachment.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.willuhn.jameica.attachment.Attachment;
import de.willuhn.jameica.attachment.Context;

/**
 * Interface f�r einen Storage-Provider.
 */
public interface StorageProvider
{
  /**
   * Liefert einen Identifier f�r den Storage-Provider.
   * @return ein Identifier.
   */
  public String getId();
  
  /**
   * Liefert einen sprechenden Namen f�r den Storage-Provider.
   * @return sprechender Name f�r den Storage-Provider.
   */
  public String getName();
  
  /**
   * Liefert true, wenn der Storage-Provider verf�gbar ist.
   * @return true, wenn der Storage-Provider verf�gbar ist.
   */
  public boolean isEnabled();

  /**
   * Liefert die Attachments f�r den angegebenen Context.
   * Hierbei werden nur die Informationen zu den Attachments geliefert, nicht der Datei-Inhalt. Der kann per 
   * @param ctx der Context.
   * @return die Attachments.
   */
  public List<Attachment> getAttachments(Context ctx);
  
  /**
   * Kopiert das exsitierende Attachment in den angegebenen Stream.
   * @param a das existierende Attachment.
   * @param os Stream, in den das Attachment geschrieben wird.
   */
  public void copy(Attachment a, OutputStream os);
  
  /**
   * Erstell ein neues Attachment.
   * @param a das zu erstellende Attachment.
   * @param is Stream mit den Daten des neuen Attachments.
   */
  public void create(Attachment a, InputStream is);
  
  /**
   * L�scht das Attachment.
   * @param a das zu l�schende Attachment.
   */
  public void delete(Attachment a);

}
