# DiscreteMath
Discrete Math abstract library

When worked on: September 2020 - May 2021

- - -

The entire class of discrete math I took as a library (and some more)

⭐ - Favorites / most impressive

* Combinatorics
* Expressions
  * Drawer (like desmos) ⭐
    * Declare variables and functions
    * Automatically adjusts precision
    * Real-time render scaling
    * Deep exception detection
  * Functions / Operators
  * Simplifier ⭐
  * Derivatives ⭐
  * Equations/Inequalities
  * Knows Domain (uses Sets)
* Graphs
  * Simple graph
  * Drawer *(unfinished)*
* Matrices
  * Convert to Graph
  * Utilility
    * +, -, *, / matrices
* Parser (String input) ⭐
  * Read statements
  * Applied to any tree structure *(unfinished)*
  * Syntax error catching
* Proofs
  * Exhaustive (uses combinatorics)
* Random generator
  * Generate randomized expressions or statements with seed
* Sets
  * Infinite
* Statements (tree structure)
  * Boolean logic
  * Existential/Universal
  * Laws
  * Logic table display ⭐

Examples:

```java
Statement statement = StatementParser.parseStatement("a    and  ~ b or  !(c ^   t) implies b");
```
`((a and (not b)) or (not (c xor t))) implies b    // c is contradiction, t is tautology`

*other display style*

`((a ⋀ (¬b)) ⋁ (¬(c ⊻ t))) ⇒ b`
```java
statement.getTable()
```
![image](https://user-images.githubusercontent.com/12685201/118528497-06678100-b708-11eb-81c9-20fc8a530bdd.png)
- - -
```java
Variable varA = new Variable("a");
Expression exp = varA.pow(LOG.of(varA, varA.pow(varA))).minus(Constant.ZERO);
```
```java
exp
```
`(a ^ LOG(a, (a ^ a))) - 0`
```java
exp.simplified()
```

`a ^ a`
```java
exp.derive()
```
`((a ^ LOG(a, (a ^ a))) * (((LOG(a, (a ^ a)) / a) * da) + (LN(a) * ((((a ^ a) * (((a / a) * da) + (LN(a) * da))) - ((((a ^ a) * LOG(a, (a ^ a))) * da) / a)) / ((a ^ a) * LN(a)))))) - 0`
```java
exp.derive().simplified()
```
`(a ^ a) * (((LOG(a, (a ^ a)) / a) * da) + (LN(a) * ((((a ^ a) * (da * (1 + LN(a)))) - ((((a ^ a) * LOG(a, (a ^ a))) * da) / a)) / ((a ^ a) * LN(a)))))`
```java
exp.derive().getUniversalStatement() // the domain, unsimplified
```
`∀a,da∈R((a > 0 ⋀ (a ^ a) > 0) ⋀ (((a > 0 ⋀ (a ^ a) > 0) ⋀ (a ≠ 0)) ⋀ (a > 0 ⋀ (((((a ≠ 0) ⋀ a > 0) ⋀ ((a > 0 ⋀ (a ^ a) > 0) ⋀ (a ≠ 0))) ⋀ a > 0) ⋀ (((a ^ a) * LN(a)) ≠ 0)))))`
- - -
```java
Definition.ODD.test(new Constant(5.0))
```
`for the integer 5, it is odd iff (5 % 2) == 1, which is true, so 5 is odd`
```java
new ExistentialStatement<>(
        a -> new UniversalStatement<>(b -> a[0].times(b[0]).equates(Constant.ZERO),
		  new FiniteSet<>(new NumberLiteral(9), new NumberLiteral(5)), true,
		  new Variable("b")),
        new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), true,
	new Variable("a"))
```
`{∃a∈{2.0, 1.0, 0.0}|∀b∈{9.0, 5.0}, ((a * b) = 0)}`
```java
exhaustiveProofString()
```
```
for a = 2.0, ↴
	for b = 5.0, (a * b) = 0, which is false. (invalid), which is false... continuing
for a = 1.0, ↴
	for b = 5.0, (a * b) = 0, which is false. (invalid), which is false... continuing
for a = 0.0, ↴
	for b = 9.0, (a * b) = 0, which is true... continuing
	for b = 5.0, (a * b) = 0, which is true... continuing
	for b = 9.0, (a * b) = 0
	which are all true. (valid)
which is true. (valid)
```
- - -
![image](https://user-images.githubusercontent.com/12685201/118525688-1762c300-b705-11eb-8feb-26b0a69ebdb2.png)

Very brief mid re-render frame while not very precise yet.

![image](https://user-images.githubusercontent.com/12685201/118526791-34e45c80-b706-11eb-8bb0-abe9b8026df4.png)
