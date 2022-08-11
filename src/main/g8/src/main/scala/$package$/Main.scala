package $package$

import ap.parser._
import ap.SimpleAPI
import ap.terfor.preds.Predicate
import lazabs.horn.bottomup.Util.Dag
import lazabs.horn.bottomup.{HornClauses, SimpleWrapper, Util}

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

    val r = createRelation("r", List(Sort.Integer))
    val x = createConstant("x")

    val unsatClauses = List(
      r(0) :- true,
      r(x+1) :- r(x),
      (x > 10) :- r(x)
    )

    println("A set of unsat clauses")
    printClauses(unsatClauses)

    // tSimpleWrapper.solve returns either an assignment of
    // uninterpreted predicates to relations or
    // a counterexample trace in the form of a DAG
    printResult(SimpleWrapper.solve(unsatClauses))
    println("-"*80)


    // Another set of clauses that is satisfiable
    val satClauses = List(
      r(0) :- true,
      r(x+1) :- r(x),
      (x >= 0) :- r(x)
    )

    println("A set of sat clauses")
    printClauses(satClauses)

    printResult(SimpleWrapper.solve(satClauses))
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
        println(cex)
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
