/**
 * <p>
 * What exactly is a function?<br>
 * How is f(x) different from SIN(x) different from f(3)?<br>
 * "<i>f(x)</i>" in "<i>f(x)</i> = x^2 - 3" is a <i>Function declaration</i><br>
 * "<i>f(x)</i>" in "x = 2; <i>f(x)</i>" is a <i>Function call</i><br>
 * "<i>LN</i>" in "<i>LN</i>(x) = LOG_e(x)" and "x; <i>LN</i>(x);" is also a <i>Function declaration</i> and <i>Function call</i>,
 *     but it is a <i>Predefined function declaration/call</i> by a given library depending different conventions/contexts
 *     (e.g. LOG base 2 in computer science, LOG base e in math, LOG base 10 in some contexts).
 *     This is to be used in user function declaration/calls (such as <i>f(x)</i>) in this project's context<br>
 * "f(x)" in "<i>f(x)</i> = x^2 - 3" is a <i>Function declaration</i><br>
 * <br>
 * So, f(x) can be 2 of: a function call or function declaration.<br>
 * <br>
 * This describes these constructions:<br>
 * function declaration(parameter declaration) = parameter use<br>
 * function call(argument/variable)<br>
 * <br>
 * A declaration can be programmatically defined as its declaration and a list of its arguments.<br>
 * {@link bran.tree.compositions.expressions.functions}'s structure for functions:<br>
 * {@link bran.tree.compositions.expressions.functions.AbstractFunction} - Function declarations<br>
 * {@link bran.tree.compositions.expressions.functions.ExpFunction} - Predefined declared function<br>
 * {@link bran.tree.compositions.expressions.functions.MultiArgFunction} - Predefined Library declared function<br>
 * {@link bran.tree.compositions.expressions.functions.CustomFunction} - Predefined User declared function<br>
 * {@link bran.tree.compositions.expressions.functions.rec.RecFunction} - Function declaration with function type parameters<br>
 * {@link bran.tree.compositions.expressions.functions.FunctionExpression} - ExpFunction with Expression arguments (Typically, it's Double)<br>
 * {@link bran.tree.compositions.expressions.functions.rec.RecFunctionExpression} - Function with function type arguments<br>
 * <br>
 * See {@link bran.tree.compositions.expressions.values}
 * </p>
 */
package bran.tree.compositions.expressions.functions;
