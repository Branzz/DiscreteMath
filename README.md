# DiscreteMath
Discrete Math abstract library

When worked on: September 2020 - May 2021

- - -

The entire class of discrete math I took as a library.

⭐ - Favorites / most impressive

* Combinatorics
* Expressions
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
* Parser (String input)
  * Read statements ⭐
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
`(((a n (~b)) v (~(c xor t))) -> b) // c is contradiction, t is tautology`
```java
TruthTable.getTable(statement)
```
```
┌───┬───┬───┬───┬──────┬───────────┬────────────┬──────────────┬─────────────────────────────┬────────────────────────────────────┐
│ a │ b │ c │ t │ (~b) │ (c xor t) │ (a n (~b)) │ (~(c xor t)) │ ((a n (~b)) v (~(c xor t))) │ (((a n (~b)) v (~(c xor t))) -> b) │
├───┼───┼───┼───┼──────┼───────────┼────────────┼──────────────┼─────────────────────────────┼────────────────────────────────────┤
│ 0 │ 0 │ 0 │ 1 │  1   │     1     │     0      │      0       │              0              │                 1                  │
│ 0 │ 1 │ 0 │ 1 │  0   │     1     │     0      │      0       │              0              │                 1                  │
│ 1 │ 0 │ 0 │ 1 │  1   │     1     │     1      │      0       │              1              │                 0                  │
│ 1 │ 1 │ 0 │ 1 │  0   │     1     │     0      │      0       │              0              │                 1                  │
└───┴───┴───┴───┴──────┴───────────┴────────────┴──────────────┴─────────────────────────────┴────────────────────────────────────┘
```
- - -
```java
Variable varA = new Variable("a");
Expression expression = varA.pow(LOG.of(varA, varA.pow(varA))).minus(Constant.ZERO);
```
```java
expression
```
`((a ^ (LOG(a, (a ^ a))) - 0)`
```java
.simplified()
```

`(a ^ a)`
```java
.derive()
```
`(((a ^ (LOG(a, (a ^ a))) * ((((LOG(a, (a ^ a)) / a) * da) + ((LN(a) * ((((a ^ a) * (((a / a) * da) + ((LN(a) * da))) - ((((a ^ a) * (LOG(a, (a ^ a))) * da) / a)) / ((a ^ a) * (LN(a)))))) - 0)`
```java
.derive().simplified()
```
`((a ^ a) * ((((LOG(a, (a ^ a)) / a) * da) + ((LN(a) * ((((a ^ a) * (da * (1 + (LN(a)))) - ((((a ^ a) * (LOG(a, (a ^ a))) * da) / a)) / ((a ^ a) * (LN(a))))))`
- - -
```java
Definition.ODD.test(new Constant(5.0))
```
`for the integer 5, it is odd iff ((5 % 2) = 1), which is true`
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
	for b = 5.0, ((a * b) = 0), which is false. (invalid), which is false... continuing
for a = 1.0, ↴
	for b = 5.0, ((a * b) = 0), which is false. (invalid), which is false... continuing
for a = 0.0, ↴
	for b = 9.0, ((a * b) = 0), which is true... continuing
	for b = 5.0, ((a * b) = 0), which is true... continuing
	for b = 9.0, ((a * b) = 0)
	which are all true. (valid)
which is true. (valid)
```
