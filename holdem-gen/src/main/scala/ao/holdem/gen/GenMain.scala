package ao.holdem.gen

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.holdem.gen.abs.{GenStateAbstraction, BucketAbstraction, SingleInfoAbstraction, SklanskyInfoAbstraction}
import ao.holdem.bot.simple.starting_hands.Sklansky
import ao.learn.mst.lib.{DisplayUtils, NumUtils}
import ao.holdem.bot.regret.HoldemAbstraction
import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer
import com.google.common.collect.ImmutableMultimap
import scala.collection.JavaConversions._
import ao.learn.mst.gen5.solve2.{RegretMinimizer, RegretSampler}
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler
import ao.learn.mst.gen5.state.{OptimizationState, MixedStrategy}
import ao.learn.mst.gen5.state.impl.ArrayOptimizationState
import com.google.common.base.Stopwatch
import scala.util.Random
import org.apache.commons.math3.random.{Well512a, MersenneTwister, RandomAdaptor}
import ao.holdem.model.card.canon.hole.CanonHole
import ao.holdem.ai.abs.{CompoundStateAbstraction, StateAbstraction}
import ao.holdem.abs.bucket.v2.PercentileImperfectAbstractionBuilder
import ao.holdem.ai.abs.act.{SmallActionView, BasicActionView, ActionAbstraction}
import ao.holdem.model.card.sequence.CardSequence
import java.util.{Date, Comparator}
import ao.holdem.ai.odds.OddsBy5
import ao.holdem.ai.abs.card.CardAbstraction
import ao.holdem.abs.ViewActionAbstraction
import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.card.Community
import ao.holdem.abs.odds.agglom.impl.OddsFinderEvaluator
import java.io.File

/**
 *
 */
object GenMain extends App
{
  val game: ExtensiveGame[HoldemState, HoldemInfo, HoldemAction] =
    HoldemGame

  val rand =
    new Random(new RandomAdaptor(new Well512a()))

  val sampler: RegretSampler[HoldemState, HoldemInfo, HoldemAction] =
    new OutcomeRegretSampler[HoldemState, HoldemInfo, HoldemAction](
      randomness = rand,
      payoffFactor = 1.05)

//  val holdemAbstraction: HoldemAbstraction =
//    new HoldemAbstraction(
//      new FastBucketTreeBuilder(new KMeansBucketizer),
////      5, 25.toChar, 125.toChar, 625.toChar)
//      8, 64.toChar, 512.toChar, 4096.toChar)

//  val cardAbstraction: CardAbstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
//    20, 30, 30, 50)
  val smallCardAbstraction: CardAbstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
    5, 20, 20, 20)

  val actionViewName: String =
    //"BasicActionView.bin"
    "SmallActionView.bin"


  val stateAbstraction: StateAbstraction = {
    val actionAbstraction: ActionAbstraction = ViewActionAbstraction.loadOrBuildAndSave(
      new File(s"lookup/bucket/$actionViewName"),
//      BasicActionView.VIEW)
      SmallActionView.VIEW)

    new CompoundStateAbstraction(
//      cardAbstraction,
      smallCardAbstraction,
      actionAbstraction,
      //OddsBy5.INSTANCE)
      OddsFinderEvaluator.INSTANCE)
  }

//  val bucketTree = holdemAbstraction.tree(false)

  val holeCards: ImmutableMultimap[Int, CanonHole] = {
    val buff: ImmutableMultimap.Builder[Int, CanonHole] = ImmutableMultimap.builder()
      .orderKeysBy(new Comparator[Int]() {
        def compare(a: Int, b: Int): Int =
          a.compareTo(b)
      })

    for (h <- 0 until CanonHole.CANONS) {
      val hole = CanonHole.create(h)

//      val bucket: Int = bucketTree.getHole(h)
      val bucket: Int =
        //cardAbstraction.indexInRound(
        smallCardAbstraction.indexInRound(
          new CardSequence(hole.reify()),
          OddsBy5.INSTANCE)

      buff.put(bucket, hole)
    }

    buff.build()
  }

  //val statePath = "work/opt/" + holdemAbstraction.id()
  //val statePath = "work/opt/b-20-30-30-50"
//  val statePath = "work/opt/b_20_30_30_50_b"
//  val statePath = "work/opt/b_20_30_30_50_c"
  //val statePath = "work/opt/b_20_30_30_50_agro"
  val statePath = "work/opt/s_50_20_20_20_agro"

  val state: ArrayOptimizationState =
    ArrayOptimizationState.readOrEmpty(statePath)

  val abstraction: ExtensiveAbstraction[HoldemInfo, HoldemAction] =
//    SingleInfoAbstraction
//    HoleInfoAbstraction
//    SklanskyInfoAbstraction
//      BucketAbstraction(holdemAbstraction)
    new GenStateAbstraction(stateAbstraction)

  val solver = new RegretMinimizer(
    game, abstraction, state, true)

  (0 to 2 * 1000 * 1000 * 1000).foreach(i => {

    val strategy: MixedStrategy =
      solver.strategyView

    solver.iterate(sampler)

    if (i % 1000000 == 0)
//    if (i % 25000 == 0)
//    if (i % 10000 == 0)
//    if (i % 1000 == 0)
    {
      println(s"\n\n$i")
      println("-" * 79)

      for (bucket <- holeCards.keySet()) {
        val infoSet: Long =
          abstraction.informationSetIndex(
            HoldemInfo(
              StateTree.headsUpRoot(),
              holeCards.get(bucket).iterator().next().reify(),
              Community.PREFLOP
            ))

        val probs: Seq[Double] =
          strategy.probabilities(infoSet, 3)

        println(s"${DisplayUtils.displayProbabilities(probs)}\t${holeCards.get(bucket)}")
      }

//      val rootActions: Seq[Double] =
//        strategy.actionProbabilityMass(0)
//
//      println(s"$i\t$rootActions")

//      for (g <- 0 to Sklansky.HIGH) {
//        val probs: Seq[Double] =
//          strategy.actionProbabilityMass(g)
//
//        val name: String =
//          if (g == 0) {
//            "PF"
//          } else {
//            s"S$g"
//          }
//
//        println(s"$i\t$name\t${DisplayUtils.displayProbabilities(probs)}")
//      }

//      for (h <- 0 until CanonHole.CANONS) {
//        val hole: CanonHole =
//          HoleLookup.lookup(h)
//
//        val probs: Seq[Double] =
//          strategy.actionProbabilityMass(h)
//
//        println(s"$hole\t${DisplayUtils.displayProbabilities(probs)}")
//      }

      val timer = Stopwatch.createStarted()
      state.write(statePath)
      println(s"${new Date()}\t- Done writing, took $timer")
    }
  })


//  val players: Seq[ExtensivePlayer[HoldemInfo, HoldemAction]] =
//    Seq.fill(2)(new RandomPlayer[HoldemInfo, HoldemAction](new Random()))
//
//  val outcome: Seq[Double] =
//    SimpleGameDemo.playout[HoldemState, HoldemInfo, HoldemAction](
//      game, players)
//
//  println(outcome)
}
