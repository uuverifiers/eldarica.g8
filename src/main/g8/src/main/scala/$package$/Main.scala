package $package$

import ap.parser._
import ap.SimpleAPI
import lazabs.horn.bottomup.{HornClauses, SimpleWrapper}

object Main extends App {
  SimpleAPI.withProver { p =>
    import p._
    import IExpression._
    import HornClauses._

    val r = createRelation("r", List(Sort.Integer))
    val x = createConstant("x")

    val clauses = List(
      r(0) :- true,
      r(x+1) :- r(x),
      (x > 10) :- r(x)
    )

    println(clauses)

    println(SimpleWrapper.solve(clauses))
  }
}
