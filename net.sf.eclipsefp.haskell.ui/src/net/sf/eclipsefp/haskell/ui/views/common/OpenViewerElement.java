// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.views.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.*;

import de.leiffrenzel.fp.haskell.core.halamo.*;
import de.leiffrenzel.fp.haskell.core.util.ResourceUtil;
import net.sf.eclipsefp.haskell.ui.HaskellUIPlugin;


/** <p>Action for opening a Haskell compilation unit etc., typically after
  * double click on a viewer item.</p>
  * 
  * @author Leif Frenzel
  */
public class OpenViewerElement extends Action {

  private Object element;

  public OpenViewerElement( final DoubleClickEvent event ) {
    Viewer viewer = event.getViewer();
    ISelection selection = viewer.getSelection();
    element = ( ( IStructuredSelection )selection ).getFirstElement();
  }


  // interface methods of IAction
  ///////////////////////////////

  public void run() {
    if( element instanceof IHaskellLanguageElement ) {  
      HaskellUIPlugin.showInEditor( ( IHaskellLanguageElement )element );
    } else if( element instanceof IFile ) {
      // try to find the corresponding compilation unit
      IFile file = ( IFile )element;
      if( ResourceUtil.hasHaskellExtension( file ) ) {
        ICompilationUnit cu = Halamo.getInstance().getCompilationUnit( file );
        HaskellUIPlugin.showInEditor( cu );
      }
    }
  }
}