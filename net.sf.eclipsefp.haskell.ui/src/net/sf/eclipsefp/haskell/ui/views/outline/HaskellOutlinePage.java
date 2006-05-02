// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.views.outline;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.leiffrenzel.fp.haskell.core.halamo.IHaskellLanguageElement;
import de.leiffrenzel.fp.haskell.core.halamo.ISourceLocation;
import net.sf.eclipsefp.haskell.ui.views.common.HaskellLabelProvider;


/** <p>The outline page for the Haskell editor.</p>
  * 
  * @author Leif Frenzel
  */
public class HaskellOutlinePage extends ContentOutlinePage {
  
  private Object input;
  private ITextEditor textEditor;

  public HaskellOutlinePage( final ITextEditor textEditor ) {
    this.textEditor = textEditor;
  }
  
  public void createControl( final Composite parent ) {
    super.createControl( parent );

    TreeViewer viewer = getTreeViewer();
    ExperimentalCP provider = new ExperimentalCP(); 
    viewer.setContentProvider( provider );
    viewer.setLabelProvider( new HaskellLabelProvider() );
    viewer.addSelectionChangedListener( this );

    if( input != null ) {
      viewer.setInput( input );
    }
  }
  
  public void selectionChanged( final SelectionChangedEvent event ) {
    super.selectionChanged( event );

    ISelection selection= event.getSelection();
    if( selection.isEmpty() ) {
      textEditor.resetHighlightRange();
    } else {
      IStructuredSelection sel = ( IStructuredSelection )selection;
      Object firstElement = sel.getFirstElement();
      if( firstElement instanceof IHaskellLanguageElement ) {
        IHaskellLanguageElement elem = ( IHaskellLanguageElement )firstElement;
        IEditorInput fei = textEditor.getEditorInput();
        IDocument doc = textEditor.getDocumentProvider().getDocument( fei );
        ISourceLocation srcLoc = elem.getSourceLocation();
        if( srcLoc != null ) {
          int offset = -1;
          try {
            offset = doc.getLineOffset( srcLoc.getLine() ) + srcLoc.getColumn();
          } catch( final BadLocationException badlox ) {
            // ignore
          }
          int length = elem.getName().length();
          try {
            textEditor.setHighlightRange( offset, length, true );
          } catch( IllegalArgumentException iaex ) {
            textEditor.resetHighlightRange();
          }
        }
      }
    }
  }
  
  /** <p>sets the input of the outline page.</p> */
  public void setInput( final Object input ) {
    this.input = input;
    update();
  }
  
  /** <p>updates the outline page.</p> */
  public void update() {
    TreeViewer viewer = getTreeViewer();
    if( viewer != null ) {
      Control control= viewer.getControl();
      if( control != null && !control.isDisposed() ) {
        control.setRedraw( false );
        viewer.setInput( input );
        viewer.expandToLevel( 2 );
        control.setRedraw( true );
      }
    }
  }
}