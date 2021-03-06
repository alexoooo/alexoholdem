package ao.holdem.abs.bucket.abstraction.bucketize.build;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;

import java.io.File;

/**
 * Date: 21-May-2009
 * Time: 6:08:04 PM
 */
public interface BucketTreeBuilder
{
    //--------------------------------------------------------------------
    public BucketTree bucketize(
            File dir,
            int  numHoleBuckets,
            char numFlopBuckets,
            char numTurnBuckets,
            char numRiverBuckets);


    //--------------------------------------------------------------------
    String id();
}
