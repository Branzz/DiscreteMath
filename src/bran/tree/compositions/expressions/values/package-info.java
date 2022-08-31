/**
 * <p>
 * Variables can be blurry:<br>
 * "<i>x</i>" in "<i>x</i>^2 + 2" is a <i>Variable in a plain old expression</i><br>
 * "<i>x</i>" in "<i>x</i> = 3;" is a <i>Variable declaration</i><br>
 * (in code) "<i>x</i>" in "<i>x</i> = x + 4;" is a <i>Variable set</i><br>
 * "<i>x</i>" in "<i>x</i> = 5" is a <i>Variable in an equation</i><br>
 * The first "<i>x</i>" in "f(<b>x</b>) = 2x" is a <i>Parameter declaration</i><br>
 * The second "<i>x</i>" in "f(x) = 2<b>x</b>" is a <i>Parameter usage/call</i><br>
 * "<i>x</i>" in "x; f(<i>x</i>)" is just an <i>Argument</i>; <i>x</i> could be any expression:<br>
 * "<i>3</i>" in "f(<i>3</i>)" is an <i>Argument</i><br>
 * <i>Argument</i> depends on its use, and is not necessarily a type.<br>
 * <br>
 * And, x can either be used as an expression in: a variable, a variable (in an equation),
 *     or an argument in a function call.<br>
 *     Or, in 4 of: a variable declaration, variable setting,
 *     parameter declaration in function declaration or parameter use in function declaration.<br>
 *     Which, is really only <b>5</b> uses when grouped with its uPse as just a general expression.<br>
 * <br>
 * This describes these constructions:<br>
 * variable declaration = expression<br>
 * variable setting = variable/expression<br>
 * <br>
 * {@link bran.tree.compositions.expressions.values.Variable} - Variable (for expressions)<br>
 * {@link bran.tree.compositions.expressions.values.Variable#Variable(java.lang.String)} - Variable declaration<br>
 * {@link bran.tree.compositions.expressions.values.Variable#setValue(double)} - Variable set<br>
 * {@link bran.tree.compositions.expressions.functions.FunctionExpression
 * #FunctionExpression(bran.tree.compositions.expressions.functions.ExpFunction,
 * bran.tree.compositions.expressions.Expression...)} - Usage as argument<br>
 * {@link bran.tree.compositions.expressions.functions.MultiArgFunction#functional}} - Usage as parameter<br>
 * Note: {@link bran.application.draw.exprs.Declaration}<br>
 * TODO: Setting, Declaration<br>
 *<br>
 * See {@link bran.tree.compositions.expressions.functions}
 * </p>
 */
package bran.tree.compositions.expressions.values;
