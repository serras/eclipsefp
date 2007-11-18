// Copyright (c) 2007 by Leif Frenzel <himself@leiffrenzel.de>
// All rights reserved.
package net.sf.eclipsefp.haskell.ui.internal.refactoring;

import org.eclipse.osgi.util.NLS;

/** <p>provides internationalized String messages for the UI.</p> 
  * 
  * @author Leif Frenzel
  */
public class UITexts {

  // message fields
  public static String mkPointFree_refuseDlg_title;
  public static String mkPointFree_refuseDlg_message;
  
  public static String mkPointFreeProcessor_elem;
  public static String mkPointFreeProcessor_name;

  public static String mkPointFreeDelegate_checking;
  public static String mkPointFreeDelegate_collectingChanges;
  public static String mkPointFreeDelegate_noSelection;
  public static String mkPointFreeDelegate_noSourceFile;
  public static String mkPointFreeDelegate_notApplicable;
  public static String mkPointFreeDelegate_roFile;
  
  // init stuff
  /////////////
  
  private static final String NAME =   UITexts.class.getPackage().getName()
                                     + ".uitexts"; //$NON-NLS-1$

  static {
    NLS.initializeMessages( NAME, UITexts.class );
  }
}