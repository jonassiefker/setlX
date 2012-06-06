package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.EndOfFileException;
import org.randoom.setlx.exceptions.FileNotReadableException;
import org.randoom.setlx.exceptions.ParserException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.grammar.*;
import org.randoom.setlx.statements.Block;

import org.antlr.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class ParseSetlX {

    private final   static  int EXPR    =  1337;
    private final   static  int BLOCK   = 31337;
    private         static  int errors  =     0; // our own error accounting, which survives nested parsing

    public static Block parseFile(String fileName) throws ParserException {
        try {
            // allow modification of fileName/path by environment provider
            fileName = Environment.filterFileName(fileName);
            if (new File(fileName).isFile()) {
                // parse the file contents (Antlr will print its parser errors into stderr ...)
                return parseBlock(new ANTLRFileStream(fileName));
            } else {
                throw new FileNotReadableException("File '" + fileName + "' could not be read.");
            }
        } catch (IOException ioe) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.");
        }
    }

    public static Block parseInteractive() throws ParserException {
        try {
            InputStream         stream = InputReader.getStream();

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseExpr(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static void resetErrorCount() {
        errors = 0;
    }

    public static void addReportedError() {
        errors++;
    }

    /* private methods */

    private static Block parseBlock(ANTLRStringStream input) throws SyntaxErrorException {
        return (Block) handleFragmentParsing(input, BLOCK);
    }

    private static Expr parseExpr(ANTLRStringStream input) throws SyntaxErrorException {
        return (Expr) handleFragmentParsing(input, EXPR);
    }

    private static CodeFragment parseFragment(SetlXgrammarParser parser, int type) throws RecognitionException {
        switch (type) {
            case EXPR:
                return parser.initAnyExpr();
            case BLOCK:
                return parser.initBlock();
            default:
                /* this should never be reached if surrounding code is correct */
                return null;
        }
    }

    private static CodeFragment handleFragmentParsing(ANTLRStringStream input, int type) throws SyntaxErrorException {
        SetlXgrammarLexer   lexer   = null;
        SetlXgrammarParser  parser  = null;
        try {
                                lexer  = new SetlXgrammarLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
                                parser = new SetlXgrammarParser(ts);

            // parse the input
            CodeFragment        frag   = parseFragment(parser, type);

            // now Antlr will print its parser errors into stderr ...

            /* check for unparsed syntax errors at the end of the input stream */

            // fill token stream until EOF is reached (parser fills only as far as its lookahead needs)
            ts.fill();
            // current index in stream of tokens
            int index = ts.index();

            /*
             *  If the index into the tokenStream (which was set by the parser)
             *  is not equal to the number of tokens in the complete input
             *  (minus EOF), some tokens where ignored by the parser.
             *
             *  Best guess: The parser encountered a syntax error after a valid rule.
             */
            if (index < (ts.size() - 1)) {
                // parse again to force displaying syntax error in remaining tokenStream
                parseFragment(parser, type);

                // now Antlr will (again) print its parser errors into stderr ...

                // check if parser moved the index
                if (index == ts.index()) {
                    /*  Index was not moved. Probably epsilon can be and was
                     *  derived from the start-rule.
                     *  However there are still unparsed tokens left...
                     *
                     *  Note: In SetlX this can NEVER be the case, because
                     *        epsilon can NOT be derived from any start-rule!
                     */
                    Token   t       = ts.get(index);
                    String  error   = "line " + t.getLine() + ":" + t.getCharPositionInLine();
                            error  += " input '" + ts.toString(index, ts.size()) + "' includes unidentified errors";
                    // fake Antlr like error message
                    Environment.errWriteLn(error);
                    // and stop parsing
                    throw new SyntaxErrorException(error);
                }
            }

            errors += parser.getNumberOfSyntaxErrors() + lexer.getNumberOfSyntaxErrors();
            if (errors > 0) {
                throw new SyntaxErrorException("" + errors + " syntax error(s) encountered.");
            }

            return frag;
        } catch (RecognitionException re) {
            throw new SyntaxErrorException(re.getMessage());
        } catch (NullPointerException npe) {
            if (lexer != null) {
                errors += lexer.getNumberOfSyntaxErrors();
            }
            if (parser != null) {
                errors += parser.getNumberOfSyntaxErrors();
            }
            if (errors > 0) {
                // NullPointerException caused by syntax error(s)
                throw new SyntaxErrorException("" + errors + " syntax error(s) encountered.");
            } else { // NullPointer in parse tree itself
                throw new SyntaxErrorException("Parsed tree contains nullpointer.");
            }
        }
    }
}

