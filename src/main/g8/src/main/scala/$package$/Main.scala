package $package$

import ap.parser._
import ap.SimpleAPI
import ap.terfor.preds.Predicate
import ap.theories.TheoryRegistry
import lazabs.horn.abstractions.EmptyVerificationHints
import lazabs.horn.bottomup.Util.Dag
import lazabs.horn.bottomup.{HornClauses, SimpleWrapper, Util}
import lazabs.horn.preprocessor.DefaultPreprocessor

object Settings {
  object ClauseOutputSyntax extends Enumeration {
    type ClauseOutputSyntax = Value
    val Prolog, SMT = Value
  }
  val clauseOutputSyntax = ClauseOutputSyntax.Prolog
}

object Main extends App {
  import Settings._

  /**
   * A simlpe example that encodes a simple set of clauses and directly attempts
   * to solve them. */
  SimpleAPI.withProver { p =>
    import p._
    import IExpression._
    import HornClauses._

    {
      val r = createRelation("r", List(Sort.Integer))
      val x = createConstant("x")

      val unsatClauses = List(
        r(0) :- true,
        r(x + 1) :- r(x),
        (x > 10) :- r(x)
      )

      println("A set of unsat clauses")
      printClauses(unsatClauses)

      // tSimpleWrapper.solve returns either an assignment of
      // uninterpreted predicates to relations or
      // a counterexample trace in the form of a DAG
      printResult(SimpleWrapper.solve(unsatClauses))
      println("-" * 80)


      // Another set of clauses that is satisfiable
      val satClauses = List(
        r(0) :- true,
        r(x + 1) :- r(x),
        (x >= 0) :- r(x)
      )

      println("A set of sat clauses")
      printClauses(satClauses)

      printResult(SimpleWrapper.solve(satClauses))
      println("-" * 80)
    }


    // A set of clauses using the theory of arrays
    {
      import ap.theories.ExtArray
      val arrayTheory = ExtArray(Seq(Sort.Integer), Sort.Integer)
      import arrayTheory._
      import IExpression._

      val r = createRelation("r", List(arrayTheory.sort, Sort.Integer))
      val a = createConstant("a", arrayTheory.sort)
      val i = createConstant("i", Sort.Integer)

      val arrayClauses = List(
        r(a, i) :- (i === 0 & a === const(0)), // arrayTheory.const
        r(store(a, i, select(a, i) + 1), i + 1) :- (r(a, i) & i < 10),
        (select(a, 0) === 1) :- (r(a, i) & i >= 10)
      )

      println
      println("A set of array clauses")
      printClauses(arrayClauses)

      printResult(SimpleWrapper.solve(arrayClauses, showDot = true))
      println("-" * 80)
    }

    // A set of clauses using FunTheory
    {
      import FunTheory.fun
      import IExpression._

      // register the theory first
      TheoryRegistry.register(FunTheory)

      val r = createRelation("r", 1)
      val x = createConstant

      val funClauses = List(
        r(x) :- (x === fun(20, 1, 2)),
        false :- (r(x), x =/= 42)
      )

      println
      println("A set of fun clauses")
      printClauses(funClauses)

      // preprocess the clauses before sending to the solver
      val preprocessor = new DefaultPreprocessor
      val (simpClauses, _, backTranslator) =
        Console.withErr(Console.out) {
          preprocessor.process(funClauses, EmptyVerificationHints)
        }

      printResult(SimpleWrapper.solve(simpClauses, showDot = true))
      println("-" * 80)
    }

  }

  def printResult(res : Either[Map[Predicate, IFormula],
    Dag[(IAtom, HornClauses.Clause)]]) : Unit = {
    println
    res match {
      case Left(solution) =>
        println("sat\n")
        println("Solution (_n refers to the nth argument of a predicate):")
        for ((relation, formula) <- solution) {
          println(relation + ": " + formula)
        }
      case Right(cex) =>
        println("unsat\n")
        println("Counterexample:")
        cex.prettyPrint
        Util.show(cex map (_._1), "cex")
    }
  }

  def printClauses (clauses : Seq[HornClauses.Clause]) : Unit = {
    clauses.foreach(clause =>
      println (clauseOutputSyntax match {
        case ClauseOutputSyntax.Prolog => clause.toPrologString
        case ClauseOutputSyntax.SMT => clause.toSMTString
      }))
  }

}
