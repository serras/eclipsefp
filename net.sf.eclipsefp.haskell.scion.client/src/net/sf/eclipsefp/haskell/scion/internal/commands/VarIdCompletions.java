package net.sf.eclipsefp.haskell.scion.internal.commands;

import org.eclipse.core.resources.IFile;

public class VarIdCompletions extends CompletionTupleBase {
  public VarIdCompletions(final IFile theFile) {
    super(theFile);
  }

  @Override
  public String getMethod() {
    return "completion-varIds";
  }
}
