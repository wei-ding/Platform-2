// Generated from dpc3po.g4 by ANTLR 4.2.2
package dpc3po;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link dpc3poParser}.
 */
public interface dpc3poListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link dpc3poParser#Prescription}.
	 * @param ctx the parse tree
	 */
	void enterPrescription(@NotNull dpc3poParser.PrescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dpc3poParser#Prescription}.
	 * @param ctx the parse tree
	 */
	void exitPrescription(@NotNull dpc3poParser.PrescriptionContext ctx);

	/**
	 * Enter a parse tree produced by {@link dpc3poParser#verse}.
	 * @param ctx the parse tree
	 */
	void enterVerse(@NotNull dpc3poParser.VerseContext ctx);
	/**
	 * Exit a parse tree produced by {@link dpc3poParser#verse}.
	 * @param ctx the parse tree
	 */
	void exitVerse(@NotNull dpc3poParser.VerseContext ctx);

	/**
	 * Enter a parse tree produced by {@link dpc3poParser#sentence}.
	 * @param ctx the parse tree
	 */
	void enterSentence(@NotNull dpc3poParser.SentenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link dpc3poParser#sentence}.
	 * @param ctx the parse tree
	 */
	void exitSentence(@NotNull dpc3poParser.SentenceContext ctx);

	/**
	 * Enter a parse tree produced by {@link dpc3poParser#stanza}.
	 * @param ctx the parse tree
	 */
	void enterStanza(@NotNull dpc3poParser.StanzaContext ctx);
	/**
	 * Exit a parse tree produced by {@link dpc3poParser#stanza}.
	 * @param ctx the parse tree
	 */
	void exitStanza(@NotNull dpc3poParser.StanzaContext ctx);
}
