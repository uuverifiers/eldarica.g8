package $package$

// We import several packages from Princess, as Eldarica mainly uses
// Princess to encode formulas
import ap.Signature
import ap.Signature.PredicateMatchConfig
import ap.parser.IExpression.Predicate
import ap.parser.{CollectingVisitor, IExpression, IFormula, IFunApp, IFunction, ITerm}
import ap.proof.theoryPlugins.Plugin
import ap.terfor.Formula
import ap.theories.Theory
import ap.parser.IExpression._
import ap.terfor.conjunctions.Conjunction
import ap.types.{MonoSortedIFunction, SortedConstantTerm}

/**
 * An example theory that rewrites foo(a,b,c) as (a+b)*c
 * foo : Int x Int x Int -> Int
 */
object FunTheory extends Theory {
  val fun = MonoSortedIFunction(
    name     = "foo",
    argSorts = List(Sort.Integer, Sort.Integer, Sort.Integer),
    resSort  = Sort.Integer,
    partial  = false, relational = false)

  override val functions: Seq[IFunction] = Seq(
    fun  // the sole theory function
  )
  override val predicates: Seq[Predicate] = Seq(
    // this theory has no predicates
  )

  override val dependencies: Iterable[Theory] = List(
    /* any theories that this theory depends on, for instance if one of the
    * argument sorts would be of an array sort, we would need to declare the
    * related theory here */)

  // we leave most overrides empty, as they are used in decision procedures and
  // we will do a simple rewriting
  override val functionPredicateMapping: Seq[(IFunction, Predicate)] = Nil
  override val functionalPredicates: Set[Predicate] = Set()
  override val predicateMatchConfig: PredicateMatchConfig = Map()
  override val triggerRelevantFunctions: Set[IFunction] = Set()
  override val axioms: Formula = Conjunction.TRUE
  override val totalityAxioms: Formula = Conjunction.TRUE
  override def plugin: Option[Plugin] = None

  /**
   * A pre-processor that is applied to formulas over this
   * theory, prior to sending the formula to a prover. This method
   * will be applied very early in the translation process.
   */
  override def iPreprocess(f : IFormula,
                           signature : Signature) : (IFormula, Signature) =
    (Preproc.visit(f, ()).asInstanceOf[IFormula], signature)

  /**
   * The preprocessor that rewrites foo(a,b,c) as (a + b) * c
   */
  private object Preproc extends CollectingVisitor[Unit, IExpression] {
    def postVisit(t : IExpression, arg : Unit,
                  subres : Seq[IExpression]) : IExpression = t match {
      case IFunApp(`fun`, _) =>
        val Seq(a, b, c) = subres.take(3).map(_.asInstanceOf[ITerm])
        (a + b) * c
      case t =>
        t update subres
    }
  }
}
