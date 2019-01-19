package ao.holdem.ai.odds;

import ao.holdem.model.card.Hole;
import ao.holdem.model.card.canon.hole.CanonHole;

/**
 *
 */
enum HeadsUpPreflopOdds
{;
    // calculated by HoleOddsTest
    private static final double[] HAND_STRENGTHS = {
            0.503340191,
            0.536930764,
            0.570228212,
            0.603249205,
            0.632847482,
            0.662360228,
            0.691630355,
            0.720572519,
            0.750117800,
            0.774694729,
            0.799251641,
            0.823956798,
            0.852037133,
            0.359844306,
            0.368290147,
            0.386419484,
            0.378493280,
            0.396929624,
            0.414534204,
            0.376689641,
            0.395335599,
            0.413333190,
            0.431333862,
            0.381558935,
            0.400359359,
            0.418493119,
            0.436755366,
            0.453717666,
            0.402716344,
            0.408735041,
            0.427016273,
            0.445449927,
            0.462432693,
            0.479363402,
            0.424151719,
            0.432642578,
            0.438619708,
            0.457218746,
            0.474282908,
            0.491177311,
            0.508007554,
            0.448394827,
            0.456925120,
            0.465304936,
            0.472162590,
            0.489406757,
            0.506390375,
            0.523343707,
            0.540275287,
            0.473781524,
            0.482316240,
            0.490704534,
            0.499868495,
            0.506059071,
            0.523247812,
            0.540156442,
            0.556624706,
            0.575278571,
            0.501690353,
            0.510192465,
            0.518553020,
            0.527694109,
            0.536125664,
            0.543022632,
            0.560177330,
            0.576643217,
            0.594675593,
            0.602592051,
            0.532117283,
            0.540549765,
            0.548846366,
            0.557929176,
            0.566407355,
            0.575377375,
            0.583123499,
            0.599884755,
            0.617885582,
            0.625673401,
            0.634004033,
            0.573788979,
            0.582203206,
            0.590336360,
            0.599229256,
            0.599058280,
            0.609839586,
            0.619438106,
            0.627812139,
            0.646023868,
            0.653926790,
            0.662088624,
            0.670446323,
            0.323032281,
            0.331997501,
            0.351458934,
            0.342846455,
            0.362647711,
            0.381552871,
            0.340751383,
            0.360776310,
            0.380104882,
            0.399443023,
            0.345836473,
            0.366022570,
            0.385498278,
            0.405119689,
            0.423227469,
            0.368276741,
            0.374838126,
            0.394467915,
            0.414275260,
            0.432409019,
            0.450508123,
            0.390979363,
            0.400195143,
            0.406710535,
            0.426691428,
            0.444913453,
            0.462978064,
            0.480970328,
            0.416683506,
            0.425945508,
            0.435041080,
            0.442509496,
            0.460920033,
            0.479081355,
            0.497212737,
            0.515316724,
            0.443484676,
            0.452755449,
            0.461863845,
            0.471808882,
            0.478442731,
            0.496819336,
            0.514901630,
            0.532511969,
            0.552477031,
            0.472954369,
            0.482194360,
            0.491276841,
            0.501200828,
            0.510240523,
            0.517656659,
            0.535997921,
            0.553604349,
            0.572907826,
            0.581346897,
            0.505087238,
            0.514256896,
            0.523274721,
            0.533139728,
            0.542232789,
            0.551873502,
            0.560201726,
            0.578119247,
            0.597389151,
            0.605686852,
            0.614558000,
            0.549285587,
            0.558446023,
            0.567296776,
            0.576965344,
            0.576824523,
            0.588411954,
            0.598726051,
            0.607728064,
            0.627216546,
            0.635632579,
            0.644318394,
            0.653200718
    };


    public static double handStrength(Hole hole) {
        CanonHole canonHole = CanonHole.create(hole);
        int index = canonHole.canonIndex();
        return HAND_STRENGTHS[index];
    }
}
