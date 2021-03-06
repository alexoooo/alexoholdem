package ao.holdem.bot.simple;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.Odds;
import ao.holdem.abs.odds.agglom.impl.ApproximateOddFinder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

//import ao.holdem.engine.analysis.Analysis;

/**
 * Designed for heads up play.
 */
public class MathBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private CardSequence prevCards = null;
    private Odds         prevOdds  = null;


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}

    
    //--------------------------------------------------------------------
    public Action act(ActionState state,
                      CardSequence cards)
    {
        Odds odds;
        if (cards.equals( prevCards )) {
            odds = prevOdds;
        } else {
            OddFinder oddFinder = new ApproximateOddFinder();
            odds = oddFinder.compute(
                        cards.hole(),
                        cards.community(),
                        state.numActivePlayers()-1);
        }

//        double toCall  = state.remainingBetsInRound() * state.betsToCall();
//        double potOdds =
//                (toCall) /
//                (toCall + state.pot().smallBets());
//
////        if (odds.nonLossPercent() <= potOdds)
////        {
////            System.out.println(
////                "odds: " + odds +
////                " with " + hole +
////                " on " + community +
////                " vs [" + Math.round(potOdds * 100) + "]");
////        }

        prevOdds  = odds;
        prevCards = cards;

        double expectedHandStrength = odds.strengthVsRandom();

        FallbackAction act =
                expectedHandStrength < 0.175 ? FallbackAction.CHECK_OR_FOLD :
                expectedHandStrength < 0.6 ? FallbackAction.CHECK_OR_CALL :
                FallbackAction.RAISE_OR_CALL;

        return state.reify(act
                /*(odds.strengthVsRandom() > potOdds)
                ? FallbackAction.RAISE_OR_CALL
                : FallbackAction.CHECK_OR_CALL*/);
    }


    public static void main(String args[]) throws IOException
    {
        for (int i = 0; i < 10000; i++)
        {
            System.out.println();
            System.out.println("=========================");
            System.out.println("=========================");

            System.out.println("to call:");
            char toCallBytes[] = new char[20];
            new InputStreamReader(System.in).read(toCallBytes);
            String toCallStr  = new String(toCallBytes).trim();

            System.out.println("pot size:");
            char potSizeBytes[] = new char[20];
            new InputStreamReader(System.in).read(potSizeBytes);
            String potOddsStr = new String(potSizeBytes).trim();

            double toCall  = Double.valueOf(toCallStr);
            double potSize = Double.valueOf(potOddsStr);
            double potOdds = toCall / (toCall + potSize);

            System.out.println("-------------------");
            System.out.println("potOdds: " + potOdds);
        }
    }
}









