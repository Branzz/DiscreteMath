# DiscreteMath
Discrete Math abstract library

When worked on: September 2020 - May 2021

- - -

The entire class of discrete math I took as a library (and some more)

⭐ - Favorites / most impressive

* Combinatorics
* Expressions
  * Drawer (like desmos.com) ⭐
    * Declare variables and functions
    * Automatically adjusts precision
    * Real-time render scaling
    * Deep exception detection
  * Functions / Operators
  * Simplifier ⭐
    * Factoring
  * Derivatives ⭐
  * Equations/Inequalities
  * Knows Domain (using Sets)
* Graphs
  * "Simple graph"
  * Drawer
* Gödel Numbers
  * From and to numbers
* Matrices
  * Convert to/from a Graph, System of Equations, or Set of vectors
  * +, -, ×, ÷ etc. matrices
* Parser (String input) ⭐
  * Read statements and expressions
  * Syntax error catching
* Proofs
  * Exhaustive (uses combinatorics)
* Random generator
  * Generate randomized expressions or statements
  * Custom seed and size
* Sets
  * Infinite such as all Reals
  * Finite such as { A, 2, C^A }
  * operations like union, if subset, or complement of
* Statements (tree structure)
  * Boolean logic
  * Existential/Universal
  * Laws (to simplify)
  * Logic table display ⭐
  * Simplifier
    * Deep search commutative operations

- - -

## Examples

### Parsing

```java
Statement statement = StatementParser.parseStatement("a   and  ~ b OR  !(c ^  t) implies b");
```
`a && !b || !(c ^ t) implies b // (c is contradiction, t is tautology)`

```java
statement.getTable()
```
![img.png](tableSimplified.png)
- - -

### Simplification

```java
StatementGenerator.generate(seed: 3L, length: 14)
```
`A nand (B implies not A or A xor A xnor A)`

*other display styles:*\
`A ⊼ B ⇒ (¬A ⋁ A ⊻ A ⊙ A)`\
`A nand B implies (~A | A ^ A == A)`

```java
.simplified()
```
`not A`
```java
Variable varA = new Variable("a");
Expression exp = varA.pow(LOG.of(varA, varA.pow(varA))).minus(Constant.ZERO);
```

`a ^ LOG(a, a ^ a) - 0`
```java
exp.simplified()
```

`a ^ a`
```java
exp.derive()
```
`a ^ LOG(a, a ^ a) * (LOG(a, a ^ a) / a * 1 + LN a((a ^ a * (a / a * 1 + LN a * 1) - a ^ a * LOG(a, a ^ a) * 1 / a) / a ^ a * LN a)) - 0`
```java
exp.derive().simplified()
```
`a ^ a * (1 + LN(a))`
```java
exp.derive().getUniversalStatement() // the domain
```
`∀a∈R a > 0 && a ^ a > 0 && a > 0 && a ^ a > 0 && a != 0 && t && a != 0 && a != 0 && a > 0 && a != 0 && t && a != 0 && a != 0 && a > 0 && t && a != 0 && a != 0 && a > 0 && a ^ a > 0 && t && a != 0 && a != 0 && a != 0 && a > 0 && a ^ a * LN a != 0`
```java
exp.derive().getUniversalStatement().simplified()
```
`∀a∈R a > 0 && a ^ a > 0 && a != 0 && a ^ a > 0 && a ^ a * LN a != 0`
- - -

### Proofs

```java
Definition.ODD.test(new Constant(5.0))
```
`for the integer 5, it is odd iff 5 % 2 == 1, which is true, so 5 is odd`
```java
thereExists(new Variable("a"))
    .in(new FiniteNumberSet(0, 1, 2))
    .that(a -> forAll(new Variable("b"))
                    .in(new FiniteNumberSet(9, 5))
                    .that(b -> a.times(b).equates(ZERO)))
```
`{∃a∈{2.0, 1.0, 0.0}|∀b∈{9.0, 5.0}, a * b = 0}`
```java
exhaustiveProofString()
```
```
for a = 2.0, ↴
	for b = 5.0, a * b = 0, which is false. (invalid), which is false... continuing
for a = 1.0, ↴
	for b = 5.0, a * b = 0, which is false. (invalid), which is false... continuing
for a = 0.0, ↴
	for b = 9.0, a * b = 0, which is true... continuing
	for b = 5.0, a * b = 0, which is true... continuing
	for b = 9.0, a * b = 0
	which are all true. (valid)
which is true. (valid)
```
- - -

### Linear Algebra

```java
new SystemOfEquations(
   Constant.of(3).times(a).plus(b).minus(c).equates(Constant.of(-5)),
   b.negate().equates(Constant.of(4)),
   d.equates(Constant.of(1).plus(a))
)
```
`{ 3a + b - c = -5, -b = 4, d = 1 + a }`

`.toMatrix()`

```
| 3 1 -1 0 -5 |
| 0 -1 0 0 4 |
| -1 0 0 1 1 |
```

`.map(Expression::squared).map(Expression::simplified)`

```
| 9 1 1 0 25 |
| 0 1 0 0 16 |
| 1 0 0 1 1 |
```

`.getColumn(1)`

`(1, 1, 0)` (Vector)

- - -

### Gödel Numbers

```java
ZERO.equates(ZERO).godelNumber().getNumber()
```
`243000000`
```java
new GodelNumberFactors(243_000_000L).symbols()
```
`0=0`
```java
forAll(new Variable('x')).in(new SpecialSet(SpecialSetType.Z)).that(x -> x.nand(new VariableStatement('y'))).proven()
        .implies(Constant.of(3).plus(new Variable("abc")).equates(new Variable("var")))
```
```java
.godelNumber()
```
`¬(¬(A)∨¬(B))⊃(sss0+a)=b Gödel Number: 1181176374..........1505759230` ≈ 10^318
- - -

### Desmos-like graphing

![image](https://user-images.githubusercontent.com/12685201/118525688-1762c300-b705-11eb-8feb-26b0a69ebdb2.png)

Very brief mid re-render frame while calculations aren't precise yet.

![image](https://user-images.githubusercontent.com/12685201/118526791-34e45c80-b706-11eb-8bb0-abe9b8026df4.png)

- - -

All of these are featured in MainTest.java
