package bran.tree.compositions.expressions.operators

import bran.tree.compositions.Composition
import bran.tree.compositions.expressions.Expression
import bran.tree.compositions.godel.GodelNumberSymbols
import bran.tree.compositions.godel.GodelBuilder
import bran.tree.compositions.expressions.functions.FunctionExpression
import bran.tree.compositions.expressions.functions.MultiArgFunction.{COS, LN, LOG, SIN}
import bran.tree.compositions.expressions.operators.Operator.*
import bran.tree.compositions.expressions.values.Constant.{E, NEG_ONE, ONE, ZERO}
import bran.tree.compositions.expressions.values.Variable
import bran.tree.structure.Fork

//import importOption.option2Iterable
//import collection.LazyZip2.lazyZip2ToIterable
//import collection.LazyZip3.lazyZip3ToIterable
//import collection.LazyZip4.lazyZip4ToIterable
//import collection.convert.AsScalaConverters
//import scala.collection

//import bran.tree.compositions.expressions.Expression

import java.util.function.Function

object OperatorExpression {
  // @Override
  // public Expression clone() {
  // 	return new OperatorExpression(left.clone(), operator, right.clone());
  // }
  final class FactorParts(val factor: Expression, val leftPart: Expression, val rightPart: Expression) {
  }

  // private Expression commutativeDeepSearchAddSub(Expression expression) { // TODO
  // 	Collection<Expression> terms = new ArrayList<>();
  // 	commutativeDeepSearchAddSub(expression, terms);
  //
  // 	// try to factor with all terms?
  // 	return expression;`
  // private void commutativeDeepSearchAddSub(Expression expression, Collection<Expression> terms) {
  // 	if (expression instanceof OperatorExpression operatorExpression) {
  // 		if ((operatorExpression.getOperator() == ADD || operatorExpression.getOperator() == SUB)) {
  // 			commutativeDeepSearchAddSub(operatorExpression.getLeft(), terms);
  // 			commutativeDeepSearchAddSub(operatorExpression.getRight(), terms);
  // 		}
  // 	} else
  // 		terms.add(expression);
  // TODO translate into object with inverted version holding
  final class Term(val expression: Expression, val inverted: Boolean) {
  }

  def combine(terms: Iterable[Term], operator: Operator): Expression = {
    val iterator = terms.iterator
    if (terms.size == 1) return iterator.next.expression
    var combinedExpressions = new OperatorExpression(iterator.next.expression, operator, iterator.next.expression)
    while ( {
      iterator.hasNext
    }) combinedExpressions = new OperatorExpression(combinedExpressions, operator, iterator.next.expression)
    combinedExpressions
  }

  def commutativeSearch(statement: Expression, terms: Iterable[OperatorExpression.Term], inverted: Boolean, op: Operator, invOp: Operator): Unit = {
    statement match {
      case operatorExpression: OperatorExpression =>
        if ((operatorExpression.getOperator eq op) || (operatorExpression.getOperator eq invOp)) {
          commutativeSearch(operatorExpression.getLeft, terms, inverted, op, invOp)
          commutativeSearch(operatorExpression.getRight, terms, ((operatorExpression.getOperator eq SUB) || (operatorExpression.getOperator eq DIV)) ^ inverted, op, invOp)
          return
        }
      case _ =>
    }
    // if (inverted) {
    // 	if (operator == SUB || operator.inverse() == SUB) {
    // 		terms.add(statement instanceof Constant constant
    // 						  ? Constant.of(-constant.evaluate()) : statement.negate());
    // 		return;
    // 	} else if (operator == DIV || operator.inverse() == DIV) {
    // 					  ? Constant.of(1 / constant.evaluate()) : statement.reciprocal());
    // 	} // else ???
    // terms.add(statement);
    terms.add(new Term(statement, inverted))
  }

  def factor(leftExp: Expression, rightExp: Expression): FactorParts = new FactorSystem(leftExp, rightExp).factor

  private object FactorSystem { // MUTABLE
    class Factor(var power: Expression, var base: Expression, var inverse: Boolean) { // final Expression source;

      def this(expression: Expression, inverse: Boolean) = {
          // source = expression;
        // noPower = false;
        this()
        if (expression.isInstanceOf[OperatorExpression] && (operatorExp.getOperator eq POW)) {
          base = operatorExp.getLeft
          power = operatorExp.getRight
          if (power.isInstanceOf[Constant]) { // constantPower = true;
            val powerValue = powerConstant.evaluate
            if (powerValue < 0) {
              power = Constant.of(-powerValue)
              this.inverse = !inverse
            }
          }
          // if (power instanceof Constant powerConstant) {
          // 	// constantPower = true;
          // 	powerValue = powerConstant.evaluate();
          // 	if (inverse)
          // 		powerValue = -powerValue;
          // 	// if (powerValue == 1)
          // 	// 	noPower = true;
          // 	this.inverse = false;
          // 	// final double val = powerConstant.evaluate();
          // 	// this.inverse = (powerValue < 0) ^ inverse;
          // 	// powerValue = Math.abs(val);
          // 	// this.inverse = false;
          // } else {
          // 	constantPower = false;
          // 	powerValue = 0.0; // N/A - null
        }
        else {
          base = expression
          power = ONE // for expression power factoring

          // powerValue = 1;
          // noPower = true;
          // this.inverse = false;
        }
      }

//      def this() {
//        base = null
//        power = null
//        inverse = false
//      }
      // final boolean constantPower;
      // 	  double powerValue;
      // boolean noPower;
//      def this(base: Expression, power: Expression, inverse: Boolean) {
//        this()
//        this.base = base
//        this.power = power
//        this.inverse = inverse
//      }



      def reconstruct: Expression = { // if (constantPower) {
        // 	if (powerValue == 1)
        // 		return base;
        // 	else
        // 		return base.pow(Constant.of(powerValue));
        if (power eq ONE) base
        else base.pow(power)
      }
      // private static class Power {
      // 	boolean exists;
      // 	public double powerValue() {
      // 	}
      // 	public Expression power() {
    }
  }

  private class FactorSystem(val leftExp: Expression, val rightExp: Expression) {
    this.factors = new List[FactorSystem.Factor]
    this.leftFactors = new List[FactorSystem.Factor]
    seekFactors(leftExp, false, leftFactors)
    this.rightFactors = new List[FactorSystem.Factor]
    seekFactors(rightExp, false, rightFactors)

    var leftFactor : FactorSystem.Factor
    var rightFactor : FactorSystem.Factor
    var leftIter : Iterator[FactorSystem.Factor]
    var rightIter : Iterator[FactorSystem.Factor]

    def factor: FactorParts = {
      var factorable = false
      if (leftFactors.size + rightFactors.size < 2) return null
      leftIter = leftFactors.iterator
      while ( {
        leftIter.hasNext
      }) {
        this.leftFactor = leftIter.next
        rightIter = rightFactors.iterator
        while ( {
          rightIter.hasNext
        }) {
          this.rightFactor = rightIter.next
          if (leftFactor.base == rightFactor.base) {
            var foundFactor = false
            // if (leftFactor.constantPower && rightFactor.constantPower)
            // 	if (leftFactor.powerValue < rightFactor.powerValue) {
            // 		foundFactor = factorOutLeft(rightFactor.powerValue - leftFactor.powerValue);
            // 	} else {
            // 		foundFactor = factorOutRight(leftFactor.powerValue - rightFactor.powerValue);
            // if (leftFactor.inverse) {
            // 		if (rightFactor.inverse)
            // 			if (leftFactor.powerValue > rightFactor.powerValue) {
            // 				rightFactor.inverse = false;
            // 				factorOutLeft(leftFactor.powerValue - rightFactor.powerValue);
            // 			} else {
            // 				leftFactor.inverse = false;
            // 				factorOutRight(rightFactor.powerValue - leftFactor.powerValue);
            // 			}
            // 		else {
            // 			factorOutRight(rightFactor.powerValue + leftFactor.powerValue);
            // 		if (rightFactor.inverse) {
            // 			factorOutLeft(leftFactor.powerValue + rightFactor.powerValue);
            // 		} else {
            // 				factorOutRight(leftFactor.powerValue - rightFactor.powerValue);
            // 				factorOutLeft(rightFactor.powerValue - leftFactor.powerValue);
            // else { // simplification check
            if ((leftFactor.inverse == rightFactor.inverse) && leftFactor.power == rightFactor.power) {
              factors.add(leftFactor)
              leftIter.remove()
              rightIter.remove()
              foundFactor = true
            }
            else if (leftFactor.inverse) { // l, l + r
              if (rightFactor.inverse) { // CROSS MULTIPLICATION //
                // Expression power = leftFactor.power.plus(rightFactor.power);
                // Expression simplifiedPower = power.simplified();
                // if (!power.equals(simplifiedPower)) {
                // 	final Expression rightPower = rightFactor.power;
                // 	rightFactor.power = leftFactor.power;
                // 	leftFactor.power = rightPower;
                // 	rightFactor.inverse = false;
                // 	leftFactor.inverse = false;
                // 	factors.add(new Factor(leftFactor.base, simplifiedPower, true));
                // 	foundFactor = true;
                if (leftFactor.power.isInstanceOf[Constant] && rightFactor.power.isInstanceOf[Constant])if (leftConstantPower.compareTo(rightConstantPower) < 0) foundFactor = factorOutLeftSimplified(rightConstantPower.minus(leftConstantPower))
                else foundFactor = factorOutRightSimplified(leftConstantPower.minus(rightConstantPower))
                else { // if (leftFactor.constantPower) { // r, r - l
                  // 	foundFactor = factorOutRightSimplified(rightFactor.power.minus(leftFactor.power));
                  // } else { // l, l - r
                  foundFactor = factorOutLeftSimplified(rightFactor.power.minus(leftFactor.power))
                }
              }
              else foundFactor = factorOutLeftSimplified(leftFactor.power.plus(rightFactor.power))
            }
            else { // r, l + r
              if (rightFactor.inverse) foundFactor = factorOutRightSimplified(leftFactor.power.plus(rightFactor.power))
              else { // l + r, l ; r OR r, r - l OR l, l - r
                if (leftFactor.power.isInstanceOf[Constant] && rightFactor.power.isInstanceOf[Constant]) if (leftConstantPower.compareTo(rightConstantPower) < 0) foundFactor = factorOutLeftSimplified(rightConstantPower.minus(leftConstantPower))
                else foundFactor = factorOutRightSimplified(leftConstantPower.minus(rightConstantPower))
                else if (leftFactor.power.isInstanceOf[Constant]) foundFactor = factorOutLeftSimplified(rightFactor.power.minus(leftFactor.power))
                else foundFactor = factorOutRightSimplified(leftFactor.power.minus(rightFactor.power))
                // if (leftFactor.constantPower)
                // 	; // l, r - l
                // else
                // 	; // r, l - r
              }
            }
            if (foundFactor) factorable = true
          }
        }
      }
      if (factorable) new FactorParts(combineInvertibleFactors(factors), combineInvertibleFactors(leftFactors), combineInvertibleFactors(rightFactors))
      else null
    }

    /**
     * @param power - new power to check if simplified is better
     */
    private def factorOutLeftSimplified(power: Expression): Boolean = {
      val simplifiedPower = power.simplified
      if (!(power == simplifiedPower)) {
        rightFactor.power = simplifiedPower
        return factorOutLeft
      }
      false
    }

    private def factorOutRightSimplified(power: Expression): Boolean = {
      val simplifiedPower = power.simplified
      if (!(power == simplifiedPower)) {
        leftFactor.power = simplifiedPower
        return factorOutRight
      }
      false
    }

    /**
     * // * @param powerValue - new power for remaining factor
     */
    private def factorOutLeft = { // rightFactor.powerValue = powerValue;
      factors.add(leftFactor)
      leftIter.remove()
      true
    }

    // final double powerValue
    private def factorOutRight = { // leftFactor.powerValue = powerValue;
      factors.add(rightFactor)
      rightIter.remove()
      true
    }

    private def seekFactors(exp: Expression, inverse: Boolean, pool: util.Collection[FactorSystem.Factor]): Unit = {
      if (exp.isInstanceOf[OperatorExpression] && ((operatorExp.getOperator eq MUL) || (operatorExp.getOperator eq DIV))) {
        seekFactors(operatorExp.getLeft, inverse, pool)
        seekFactors(operatorExp.getRight, inverse ^ (operatorExp.getOperator eq DIV), pool)
      }
      else pool.add(new FactorSystem.Factor(exp, inverse))
    }

    private def combineInvertibleFactors(factors: util.Collection[FactorSystem.Factor]): Expression = {
      val numeratorFactors = new util.ArrayList[Expression]
      val denominatorFactors = new util.ArrayList[Expression]
      for (f <- factors) {
        if (f.inverse) denominatorFactors.add(f.reconstruct)
        else numeratorFactors.add(f.reconstruct)
      }
      val numerator = combineFactors(numeratorFactors)
      if (denominatorFactors.size == 0) return numerator
      val denominator = combineFactors(denominatorFactors)
      numerator.div(denominator)
    }

    private def combineFactors(factors: util.Collection[Expression]): Expression = {
      if (factors.size == 0) return Constant.ONE
      val iterator = factors.iterator
      var acc = iterator.next
      while ( {
        iterator.hasNext
      }) acc = acc.times(iterator.next)
      acc
    }
  }
}

class OperatorExpression(var left: Expression, val operator: Operator, var right: Expression)
  extends Expression(left.getDomainConditions.and(right.getDomainConditions).and(operator.domain(left, right)))
    with Fork[Expression, Operator, Expression] {
  override def getLeft: Expression = left

  override def getOperator: Operator = operator

  override def getRight: Expression = right

  override def evaluate: Double = operator.operate(left.evaluate, right.evaluate)

  override def derive: Expression = operator.derive(left, right)

  override def traverse[R, T](t: T, function: Function[T, R]): R = {
    left.traverse(t, function)
    right.traverse(t, function)
    function.apply(t)
  }

  override def respect(respectsTo: util.Collection[Variable]): Boolean = left.respect(respectsTo) || right.respect(respectsTo)

  override def replaceAll(original: Composition, replacement: Composition): Unit = {
    if (original == left) left = replacement.asInstanceOf[Expression]
    else left.replaceAll(original, replacement)
    if (original == right) right = replacement.asInstanceOf[Expression]
    else right.replaceAll(original, replacement)
  }

  override def appendGodelNumbers(godelBuilder: GodelBuilder): Unit = {
    val leftParens = left.isInstanceOf[OperatorExpression] && leftOperator.getOperator.godelOrder < operator.godelOrder
    val rightParens = right.isInstanceOf[OperatorExpression] && (rightOperator.getOperator.godelOrder <= operator.godelOrder && (operator.godelOperator ne GodelNumberSymbols.SYNTAX_ERROR))
    if (leftParens) godelBuilder.push(GodelNumberSymbols.LEFT)
    left.appendGodelNumbers(godelBuilder)
    if (leftParens) godelBuilder.push(GodelNumberSymbols.RIGHT)
    godelBuilder.push(operator.godelOperator)
    if (rightParens) godelBuilder.push(GodelNumberSymbols.LEFT)
    right.appendGodelNumbers(godelBuilder)
    if (rightParens) godelBuilder.push(GodelNumberSymbols.RIGHT)
  }

  override def getVariables: util.Set[Variable] = {
    val variables = new util.HashSet[Variable]
    variables.addAll(left.getVariables)
    variables.addAll(right.getVariables)
    variables
  }

  override def equals(o: Any): Boolean = (this eq o) || (o.isInstanceOf[OperatorExpression] && (operator eq opExp.getOperator) && left == opExp.getLeft && right == opExp.getRight)

  override def reciprocal: OperatorExpression = if (operator eq DIV) right.div(left)
  else Constant.ONE.div(this)

  override def toFullString: String = "(" + left.toFullString + ' ' + operator + ' ' + right.toFullString + ')'

  /**
   * @return full string, but with multiplication operator reduction
   */
  def toSemiFullString: String = {
    if ((left == ZERO && (operator eq SUB)) || (left == NEG_ONE && (operator eq MUL))) return "-" + right.toFullString // negative is a visual illusion
    else if (operator eq MUL) if (right.isInstanceOf[Constant] && !left.isInstanceOf[Constant]) return "(" + right.toFullString + left.toFullString + ')'
    else if (!right.isInstanceOf[Constant]) { // left is a constant
      return "(" + left.toFullString + right.toFullString + ')' // "2 * 3" != "23"
    }
    "(" + left.toFullString + ' ' + operator + ' ' + right.toFullString + ')'
  }

  override def toString: String = {
    var leftString = left.toString
    var rightString = right.toString
    if ((left == ZERO && (operator eq SUB)) || (left == NEG_ONE && (operator eq MUL))) {
      if (right.isInstanceOf[OperatorExpression] && ExpressionOperatorType.AS.precedence <= operator.getOrder) rightString = parens(rightString)
      return '-' + rightString
    }
    var leftGiven = true
    var rightGiven = true
    if (left.isInstanceOf[OperatorExpression]) if (leftOperator.getOperator.getOrder < operator.getOrder) leftString = parens(leftString)
    else leftGiven = false
    if (right.isInstanceOf[OperatorExpression]) if ((rightOperator.getOperator.getOrder < operator.getOrder && !rightOperator.hideMultiply)
      || (rightOperator.getOperator.getOrder == operator.getOrder && !rightOperator.getOperator.isCommutative)) rightString = parens(rightString)
    else rightGiven = false
    if (leftGiven && rightGiven && (operator eq MUL)
      && !(right.isInstanceOf[Constant] && (left.isInstanceOf[FunctionExpression] || left.isInstanceOf[Constant] || rightConstant.evaluate < 0))
      && !(left.isInstanceOf[Variable] && right.isInstanceOf[Variable])) {
      // && !(left instanceof Variable leftVariable && right instanceof Variable rightVariable && leftVariable.equals(rightVariable)))
      return leftString + rightString
    }
    leftString + " " + operator + " " + rightString
  }

  private[operators] def hideMultiply = !(left.isInstanceOf[OperatorExpression]
    && (leftOperator.getOperator.getOrder >= operator.getOrder))
    && !(right.isInstanceOf[OperatorExpression]
      && (rightOperator.getOperator.getOrder >= operator.getOrder || !(rightOperator.hideMultiply)))
    && (operator.getOrder == MUL.getOrder
      && !(right.isInstanceOf[Constant] && (left.isInstanceOf[Constant] || rightConstant.evaluate < 0)))

  /*
     * notes:
     *
     * no super calls; this is handled recursively
     * two powers multiplied/divided by each other -- common bases
     *
     * detect a multiplication between two rations and a division between two functions of multiplication and also adding
     */ override def simplified: Expression = simplifiedNoDomain.limitDomain(domainConditions) // TODO Set new domain???

  private def simplifiedNoDomain: Expression = {
    val leftSimplified = left.simplified
    val rightSimplified = right.simplified
    nullToNew(simplifiedNoDomain(leftSimplified, rightSimplified), leftSimplified, rightSimplified) // TODO maybe simplifiedNoDomain()'s (does simplification limit domain?)

  }

  def simplified(leftSimplified: Expression, rightSimplified: Expression): Expression = nullToNew(simplifiedNoDomain(leftSimplified, rightSimplified), leftSimplified, rightSimplified).limitDomain(domainConditions)

  //@Nullable
  private def simplifiedNoDomain(leftSimplified: Expression, rightSimplified: Expression): Expression = simplifiedNoDomain(leftSimplified, operator, rightSimplified, true)

  private def nullToNew(simplified: Expression, leftSimplified: Expression, rightSimplified: Expression): Expression = if (simplified == null) new OperatorExpression(leftSimplified, operator, rightSimplified)
  else simplified

  /**
   * to pass in the parameters manually if they were guaranteed to already have been simplified
   */
  private def simplifiedNoDomain(leftSimplified: Expression, operator: Operator, rightSimplified: Expression, commutativeSearch: Boolean): Expression = {
    return (leftSimplified, operator, rightSimplified) match {
      case (l : Constant, o, r : Constant) => if !(((o eq DIV) || (o eq MUL)) && r == ZERO) then
        return Constant.of(o.operate(l.evaluate, r.evaluate))

      case (_, POW, ZERO) => ONE
      case (l, POW, ONE) => return l
      case (ZERO, POW, _) => ZERO
      case (ONE, POW, r) => return r
      case (base, POW, FunctionExpression(LOG, base, pow)) => return pow
      case (pow, POW, FunctionExpression(LN, E, pow)) => return pow

      // a^b * a = a^(b + 1)
      case (ZERO, MUL, _) => ZERO
      case (_, MUL, ZERO) => ZERO
      case (ONE, MUL, r) => return r
      case (l, MUL, ONE) => return l
      case (NEG_ONE, MUL, r) => return r.negate
      case (l, MUL, NEG_ONE) => return l.negate
      case (l : Constant, MUL, OperatorExpression(rr : Constant, MUL, rl)) => Constant.of(l.evaluate() * rr.evaluate()).times(rl)
      case (l : Constant, MUL, OperatorExpression(rr : Constant, DIV, rl)) => Constant.of(l.evaluate() * rr.evaluate()).div(rl)
      case (l : Constant, MUL, OperatorExpression(rr, MUL, rl : Constant)) => rr.times(Constant.of(l.evaluate() * rl.evaluate()))
      case (l : Constant, MUL, OperatorExpression(rr, DIV, rl : Constant)) => rr.times(Constant.of(l.evaluate() / rl.evaluate()))
      case (OperatorExpression(ll : Constant, MUL, lr), MUL, r : Constant) => Constant.of(ll.evaluate() * r.evaluate()).times(lr)
      case (OperatorExpression(ll : Constant, DIV, lr), MUL, r : Constant) => Constant.of(ll.evaluate() / r.evaluate()).times(lr)
      case (OperatorExpression(ll, MUL, lr : Constant), MUL, r : Constant) => ll.times(Constant.of(lr.evaluate() * r.evaluate()))
      case (OperatorExpression(ll, DIV, lr : Constant), MUL, r : Constant) => ll.times(Constant.of(r.evaluate() / lr.evaluate()))
      case (OperatorExpression(b, POW, el), MUL, OperatorExpression(b, POW, er)) => b.pow(el.plus(er).simplified)
      case (OperatorExpression(b, POW, e), MUL, b) => b.pow(e.plus(ONE))
      case (OperatorExpression(n, DIV, d), MUL, r) => n.times(r).simplified(n, r).div(d)
      case (l, MUL, OperatorExpression(n, DIV, d)) => l.times(n).simplified(l, n).div(d)
      case (l, MUL, l) => l.squared
      case (l, MUL, r : Constant) => r.times(l) // visual; not actual simplifying under some definitions

      case (l, DIV, l) => ONE
      case (ZERO, DIV, _) => ZERO
      case (_, DIV, _) =>
        val divFactorParts = factor(leftSimplified, rightSimplified)
        if (divFactorParts != null) return divFactorParts.leftPart.div(divFactorParts.rightPart).simplified
      case (l : Constant, DIV, OperatorExpression(rl : Constant, MUL, rr)) => Constant.of(l.evaluate / rl.evaluate).div(rr)
      case (l : Constant, DIV, OperatorExpression(rl, MUL, rr : Constant)) => Constant.of(l.evaluate * rr.evaluate).div(rr)
      case (l : Constant, DIV, OperatorExpression(rl : Constant, DIV, rr)) => rr.div(Constant.of(l.evaluate / rl.evaluate))
      case (l : Constant, DIV, OperatorExpression(rl, DIV, rr : Constant)) => Constant.of(l.evaluate / rr.evaluate).div(rr)
      case (l, DIV, ONE) => return l
      case (OperatorExpression(ll : Constant, MUL, lr), DIV, r : Constant) => Constant.of(ll.evaluate / r.evaluate).times(rr)
      case (OperatorExpression(ll, MUL, lr : Constant), DIV, r : Constant) => ll.times(Constant.of(lr.evaluate * r.evaluate))
      case (OperatorExpression(ll : Constant, DIV, lr), DIV, r : Constant) => Constant.of(ll.evaluate / r.evaluate).div(lr)
      case (OperatorExpression(ll, DIV, lr : Constant), DIV, r : Constant) => ll.div(Constant.of(lr.evaluate * r.evaluate))
      case (OperatorExpression(bl, POW, lr), DIV, OperatorExpression(bl, POW, rr)) => bl.pow(lr.minus(rr).simplified)
      case (OperatorExpression(nl, DIV, dl), DIV, OperatorExpression(nr, DIV, dr)) => nl.times(dr).simplified.div(dl.times(nr).simplified)
      case (OperatorExpression(nl, DIV, dl), DIV, r) => nl.div(dl.times(r).simplified)
      case (OperatorExpression(ll, POW, lr), DIV, ll) => ll.pow(lr.minus(ONE).simplified)
      case (l, DIV, OperatorExpression(l, POW, rr)) => l.pow(rr.minus(ONE).simplified)
      case (l, DIV, OperatorExpression(rl, DIV, rr)) => l.times(rr).simplified.div(rl)

      case (ZERO, MOD, _) => ZERO
      case (x, MOD, x) => ZERO

      case (FunctionExpression(LN, l) | FunctionExpression(LOG, E, l), ADD,
        FunctionExpression(LN, r) | FunctionExpression(LOG, E, r)) => return LN.ofS(l.times(r).simplified)
      case (FunctionExpression(LOG, b, lr), ADD, FunctionExpression(LOG, b, rr)) => return LOG.ofS(b, lr.times(rr).simplified)
      case (OperatorExpression(ln, DIV, d), ADD, OperatorExpression(rn, DIV, d)) => ln.plus(rn).div(ld)
      case (OperatorExpression(FunctionExpression(SIN, x), POW, TWO), ADD,
        OperatorExpression(FunctionExpression(COS, x), POW, TWO)) => ONE
      case (OperatorExpression(FunctionExpression(COS, x), POW, TWO), ADD,
        OperatorExpression(FunctionExpression(SIN, x), POW, TWO)) => ONE
      case (ZERO, ADD, r) => return l
      case (l, ADD, ZERO) => return l
      case (l, ADD, r : Constant) => if (r.evaluate < 0) return l.minus(r)
      case (l, ADD, l) => TWO.times(l)
      case (_, ADD, _) => {
        val addFactorParts = factor(leftSimplified, rightSimplified)
        if (addFactorParts != null) return addFactorParts.factor.times(addFactorParts.leftPart.plus(addFactorParts.rightPart)).simplified
      }
      case (l, SUB, ZERO) => return l
      case (l, SUB, r : Constant) => if (r.evaluate < 0) return l.plus(Constant.of(-rightConstant.evaluate))
      case (l, SUB, l) => ZERO
      case (ZERO, SUB, r) => NEG_ONE.times(r)
      case (FunctionExpression(LOG, E, x) | FunctionExpression(LN, x), SUB,
        FunctionExpression(LOG, E, y) | FunctionExpression(LN, y)) => LN.ofS(x.div(y).simplified)
      case (FunctionExpression(LOG, E, x) | FunctionExpression(LN, x), ADD,
        FunctionExpression(LOG, E, y) | FunctionExpression(LN, y)) => LN.ofS(x.times(y).simplified)
      case (_, SUB, _) =>
       val subFactorParts = factor(leftSimplified, rightSimplified)
       if (subFactorParts != null) return subFactorParts.factor.times(subFactorParts.leftPart.minus(subFactorParts.rightPart)).simplified
      case (_, _, _) =>
        if (commutativeSearch) {
          val commutativeOp = operator.isCommutative
          if (commutativeOp || (operator.inverse != null && operator.inverse.isCommutative)) {
            val simplified = commutativeCrossSearch(left, right, !commutativeOp,
              if (commutativeOp) operator else operator.inverse,
              if (commutativeOp) operator.inverse else operator)
            if (simplified != null) return simplified
          }
        }
        else null
    }
  }

  private def factor0(leftSimplified: Expression, rightSimplified: Expression): OperatorExpression.FactorParts = {
    if (leftSimplified.isInstanceOf[OperatorExpression]) { // tree search for all mults
      if (leftOperator.getOperator eq MUL) if (rightSimplified.isInstanceOf[OperatorExpression] && (rightOperator.getOperator eq MUL)) if (leftOperator.getLeft == rightOperator.getLeft) return new OperatorExpression.FactorParts(leftOperator.getLeft, leftOperator.getRight, rightOperator.getRight)
      else if (leftOperator.getLeft == rightOperator.getRight) return new FactorParts(leftOperator.getLeft, leftOperator.getRight, rightOperator.getLeft)
      else if (leftOperator.getRight == rightOperator.getLeft) return new FactorParts(leftOperator.getRight, leftOperator.getLeft, rightOperator.getRight)
      else if (leftOperator.getRight == rightOperator.getRight) return new FactorParts(leftOperator.getRight, leftOperator.getLeft, rightOperator.getLeft)
      else if (leftOperator.getLeft == rightSimplified) return new FactorParts(leftOperator.getLeft, leftOperator.getRight, Constant.ONE)
      else if (leftOperator.getRight == rightSimplified) return new FactorParts(leftOperator.getRight, Constant.ONE, leftOperator.getLeft)
    }
    else if (rightSimplified.isInstanceOf[OperatorExpression] && (rightOperator.getOperator eq MUL)) if (rightOperator.getLeft == leftSimplified) return new OperatorExpression.FactorParts(rightOperator.getLeft, rightOperator.getRight, Constant.ONE)
    else if (rightOperator.getRight == leftSimplified) return new FactorParts(rightOperator.getRight, Constant.ONE, rightOperator.getLeft)
    null
  }

  private def commutativeCrossSearch(leftStatement: Expression, rightStatement: Expression, inverted: Boolean, op: Operator, invOp: Operator): Expression = {
    val leftTerms = commutativeSearch(leftStatement, false, op, invOp)
    val rightTerms = commutativeSearch(rightStatement, inverted, op, invOp)
    val newTerms = new util.ArrayList[Term]
    if (leftTerms.size + rightTerms.size < 3) return null
    for (i <- 0 until leftTerms.size) {
      var j = 0
      while ( {
        j < rightTerms.size && i < leftTerms.size
      }) {
        var newTerm = simplifiedNoDomain(leftTerms.get(i).expression, if (rightTerms.get(j).inverted) invOp
        else op, rightTerms.get(j).expression, false)
        if (newTerm != null) { // check to see if any of the current terms can combine and accumulate further:
          leftTerms.remove(i)
          rightTerms.remove({
            j -= 1; j + 1
          })
          newTerm = accumulateTerms(newTerms, newTerm, op, invOp)
          newTerm = accumulateTerms(leftTerms, newTerm, op, invOp)
          newTerm = accumulateTerms(rightTerms, newTerm, op, invOp)
          newTerms.add(new Term(newTerm, false))
        }

        j += 1
      }
    }
    if (newTerms.size == 0) null
    else {
      newTerms.addAll(leftTerms)
      newTerms.addAll(rightTerms)
      combine(newTerms, operator)
    }
  }

  private def accumulateTerms(terms: util.List[Term], initialTerm: Expression, op: Operator, invOp: Operator) : Expression = {
    var newTerm = initialTerm
    var i = 0
    while ( {
      i < terms.size
    }) {
      val accNewTerm = simplifiedNoDomain(newTerm, if (terms.get(i).inverted) invOp
      else op, terms.get(i).expression, false)
      if (accNewTerm != null) {
        terms.remove({
          i -= 1; i + 1
        })
        newTerm = accNewTerm
      }

      i += 1
    }
    newTerm
  }

  private def commutativeSearch(statement: Expression, inverted: Boolean, op: Operator, invOp: Operator) : List[Term] = {
    val terms = new List[Term]
    commutativeSearch(statement, terms, inverted, op, invOp)
    terms
  }

}
