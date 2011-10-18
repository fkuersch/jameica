/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/messaging/ManifestMessageConsumer.java,v $
 * $Revision: 1.6 $
 * $Date: 2011/10/18 09:29:06 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.messaging;

import java.util.LinkedList;
import java.util.List;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.ConsumerDescriptor;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.services.BeanService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.MultipleClassLoader;

/**
 * Uebernimmt das Registrieren der im Plugin-Manifest definierten
 * Message-Consumer.
 */
public class ManifestMessageConsumer implements MessageConsumer
{
  private static boolean done = false;
  
  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{SystemMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  public void handleMessage(Message message) throws Exception
  {
    if (done)
      return;
    
    // Machen wir nur, wenn die "System-gestartet"-Meldung kommt.
    SystemMessage msg = (SystemMessage) message;
    if (msg.getStatusCode() != SystemMessage.SYSTEM_STARTED)
      return;
    
    Logger.debug("searching for message consumers from manifests");
    MessagingFactory factory = Application.getMessagingFactory();
    BeanService beanService = Application.getBootLoader().getBootable(BeanService.class);
    
    List<Manifest> list = new LinkedList<Manifest>();
    list.addAll(Application.getPluginLoader().getInstalledManifests()); // die Plugins
    list.add(Application.getManifest()); // Jameica selbst
    
    int count = 0;
    for (Manifest mf:list)
    {
      ConsumerDescriptor[] consumers = mf.getMessageConsumers();
      if (consumers == null || consumers.length == 0)
        continue;
      for (ConsumerDescriptor d:consumers)
      {
        String classname = d.getClassname();
        String queue     = d.getQueue();
        
        if (classname == null || classname.length() == 0)
        {
          Logger.warn("skipping consumer declaration in manifest from " + mf.getName() + ", contains no class name");
          continue;
        }
        if (queue == null || queue.length() == 0)
        {
          Logger.warn("skipping messageconsumer " + classname + " in manifest from " + mf.getName() + ", contains no queue name");
          continue;
        }
        
        try
        {
          Class c = null;
          
          if (mf.isSystemManifest())
          {
            // ueber den System-Classloader laden
            c = Application.getClassLoader().load(classname);
          }
          else
          {
            // Wir laden die Klasse ueber den Classloader des Plugins
            AbstractPlugin plugin = Application.getPluginLoader().getPlugin(mf.getPluginClass());
            MultipleClassLoader loader = plugin.getResources().getClassLoader();
            c = loader.load(classname);
          }
          MessageConsumer mc = (MessageConsumer) beanService.get(c);
          
          // Wir registrieren hier nur Consumer, die NICHT das autoRegister-Flag gesetzt
          // haben. Denn die werden ja bereits vom AutoRegisterMessageConsumer erfasst.
          if (mc.autoRegister())
            continue;
          
          Logger.debug("  " + queue + ": " + classname);
          factory.getMessagingQueue(queue).registerMessageConsumer(mc);
          count++;
        }
        catch (Throwable t)
        {
          Logger.error("unable to register message consumer " + classname,t);
        }
        
      }
    }
    Logger.info("message consumers from manifests: " + count);
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  public boolean autoRegister()
  {
    return false;
  }

}



/**********************************************************************
 * $Log: ManifestMessageConsumer.java,v $
 * Revision 1.6  2011/10/18 09:29:06  willuhn
 * @N Reminder in eigenes Package verschoben
 * @N ReminderStorageProvider, damit der ReminderService auch Reminder aus anderen Datenquellen verwenden kann
 *
 * Revision 1.5  2011-10-05 16:51:16  willuhn
 * @N Auch MessageConsumer beruecksichtigen, die im System-Manifest von Jameica stehen
 *
 * Revision 1.4  2011-08-31 07:46:41  willuhn
 * @B Compile-Fixes
 *
 * Revision 1.3  2011-08-30 15:51:11  willuhn
 * @N Message-Consumer via Bean-Service instanziieren, damit dort jetzt auch Dependency-Injection moeglich ist
 *
 * Revision 1.2  2011-06-17 16:06:17  willuhn
 * @C Logging
 *
 * Revision 1.1  2011-06-17 15:55:18  willuhn
 * @N Registrieren von Message-Consumern im Manifest
 *
 * Revision 1.1  2011-06-07 11:08:55  willuhn
 * @C Nach automatisch zu registrierenden Message-Consumern erst suchen, nachdem die SystemMessage.SYSTEM_STARTED geschickt wurde. Vorher geschah das bereits beim Senden der ersten Nachricht - was u.U. viel zu frueh ist (z.Bsp. im DeployService)
 *
 **********************************************************************/