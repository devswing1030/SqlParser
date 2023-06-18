package com.devswing.sqlparser.comment;// Generated from TableComment.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TableCommentParser}.
 */
public interface TableCommentListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(TableCommentParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(TableCommentParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#localName}.
	 * @param ctx the parse tree
	 */
	void enterLocalName(TableCommentParser.LocalNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#localName}.
	 * @param ctx the parse tree
	 */
	void exitLocalName(TableCommentParser.LocalNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#detail}.
	 * @param ctx the parse tree
	 */
	void enterDetail(TableCommentParser.DetailContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#detail}.
	 * @param ctx the parse tree
	 */
	void exitDetail(TableCommentParser.DetailContext ctx);
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#taglist}.
	 * @param ctx the parse tree
	 */
	void enterTaglist(TableCommentParser.TaglistContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#taglist}.
	 * @param ctx the parse tree
	 */
	void exitTaglist(TableCommentParser.TaglistContext ctx);
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#tag}.
	 * @param ctx the parse tree
	 */
	void enterTag(TableCommentParser.TagContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#tag}.
	 * @param ctx the parse tree
	 */
	void exitTag(TableCommentParser.TagContext ctx);
	/**
	 * Enter a parse tree produced by {@link TableCommentParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(TableCommentParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TableCommentParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(TableCommentParser.DescriptionContext ctx);
}