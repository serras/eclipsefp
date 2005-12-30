package net.sf.eclipsefp.haskell.core.jparser;

import java.util.LinkedList;
import java.util.Queue;

import antlr.CommonToken;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;

public class PreprocessedTokenStream implements TokenStream {

	private Queue<Token> fInsertedTokens = new LinkedList<Token>();
	private LookaheadTokenStream fStream;
	private boolean fIsFirstCall = true;
	
	public PreprocessedTokenStream(TokenStream stream) {
		fStream = new LookaheadTokenStream(stream);
	}
	
	public Token nextToken() throws TokenStreamException {
		insertNeededTokens();
		
		if (fInsertedTokens.isEmpty()) {
			return fStream.nextToken();
		} else {
			return fInsertedTokens.poll();
		}
	}

	private void insertNeededTokens() throws TokenStreamException {
		if (!fInsertedTokens.isEmpty())
			return;
		
		if (fIsFirstCall) {
			Token firstToken = consumeLinebreaks();
			
			if (!isModule(firstToken) && !isLeftCurly(firstToken)) {
				insertOpenBlock(firstToken.getColumn());
			}
		} else if (isLineBreak(fStream.peekToken())) {
			//consume the line break char
			fStream.nextToken();
			Token referenceToken = consumeLinebreaks();
			
			if (!isEOF(referenceToken)) {
				insertLineBreak(referenceToken.getColumn());
			}
		} else if (isBlockOpener(fStream.peekToken(1)) && !isLeftCurly(fStream.peekToken(2))) {
			fInsertedTokens.offer(fStream.nextToken());
			Token referenceToken = consumeLinebreaks();
			
			int openBlockColumn;
			
			if (isEOF(referenceToken)) {
				openBlockColumn = -1;
			} else {
				openBlockColumn = referenceToken.getColumn();
			}
			
			insertOpenBlock(openBlockColumn);
		}
		
		fIsFirstCall = false;
	}

	private void insertOpenBlock(int column) throws TokenStreamException {
		insertControlToken(HaskellLexerExtendedTokenTypes.OPENBLOCK, column);
	}

	private void insertLineBreak(int column) throws TokenStreamException {
		insertControlToken(HaskellLexerExtendedTokenTypes.LINEBREAK, column);
    }

	private void insertControlToken(int tokenType, int column) {
		Token controlToken = new CommonToken(tokenType, "<<special token>>");
		controlToken.setColumn(column);
		insertToken(controlToken);
	}

	private void insertToken(Token token) {
		fInsertedTokens.offer(token);
	}

	private boolean isEOF(Token token) {
		return token.getType() == HaskellLexerTokenTypes.EOF;
	}

	/**
	 * Consumes all line breaks on the underlying stream and returns
	 * the next non-linebreak token.
	 */
	private Token consumeLinebreaks() throws TokenStreamException {
		while(isLineBreak(fStream.peekToken())) {
			fStream.nextToken();
		}
		return fStream.peekToken();
	}

	private boolean isLineBreak(Token token) throws TokenStreamException {
		return token.getType() == HaskellLexerExtendedTokenTypes.NEWLINE;
	}
	
	private boolean isBlockOpener(Token token) {
		return isWhere(token) || isLet(token) || isDo(token) || isOf(token);
	}

	private boolean isLet(Token token) {
		return token.getType() == HaskellLexerTokenTypes.LET;
	}

	private boolean isDo(Token token) {
		return token.getType() == HaskellLexerTokenTypes.DO;
	}

	private boolean isOf(Token token) {
		return token.getType() == HaskellLexerTokenTypes.OF;
	}

	private boolean isWhere(Token token) {
		return token.getType() == HaskellLexerTokenTypes.WHERE;
	}

	private boolean isLeftCurly(Token theToken) {
		return theToken.getType() == HaskellLexerTokenTypes.LEFT_CURLY;
	}
	
	private boolean isModule(Token token) {
		return token.getType() == HaskellLexerTokenTypes.MODULE;
	}
	
}