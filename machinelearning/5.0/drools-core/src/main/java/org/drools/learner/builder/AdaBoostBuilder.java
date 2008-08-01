package org.drools.learner.builder;

import java.util.ArrayList;

import org.drools.learner.DecisionTree;
import org.drools.learner.InstanceList;
import org.drools.learner.Memory;
import org.drools.learner.Stats;
import org.drools.learner.tools.LoggerFactory;
import org.drools.learner.tools.SimpleLogger;
import org.drools.learner.tools.Util;

/*
 * 
 */
public class AdaBoostBuilder implements DecisionTreeBuilder{

	private static SimpleLogger flog = LoggerFactory.getUniqueFileLogger(AdaBoostBuilder.class, SimpleLogger.DEFAULT_LEVEL);
	private static SimpleLogger slog = LoggerFactory.getSysOutLogger(AdaBoostBuilder.class, SimpleLogger.DEFAULT_LEVEL);
	
	private TreeAlgo algorithm = TreeAlgo.BOOST; // default bagging, TODO boosting
	
	private static int FOREST_SIZE = 50;
	private static final double TREE_SIZE_RATIO = 1.0d;
	private static final boolean WITH_REP = false;
	
	private ArrayList<DecisionTree> forest;
	private ArrayList<Double> classifier_accuracy;
	
	private DecisionTreeMerger merger;
	
	public AdaBoostBuilder() {
		//this.trainer = _trainer;
		merger = new DecisionTreeMerger();
	}
	public void build(Memory mem, Learner _trainer) {
		
		final InstanceList class_instances = mem.getClassInstances();
		_trainer.setInputData(class_instances);
		
		
		if (class_instances.getTargets().size()>1 ) {
			//throw new FeatureNotSupported("There is more than 1 target candidates");
			if (flog.error() !=null)
				flog.error().log("There is more than 1 target candidates\n");
			System.exit(0);
			// TODO put the feature not supported exception || implement it
		} else if (_trainer.getTargetDomain().getCategoryCount() >2) {
			if (flog.error() !=null)
				flog.error().log("The target domain is not binary!!!\n");
			System.exit(0);
		}
	
		int N = class_instances.getSize();
		int NUM_DATA = (int)(TREE_SIZE_RATIO * N);	// TREE_SIZE_RATIO = 1.0, all data is used to train the trees again again
		_trainer.setTrainingDataSizePerTree(NUM_DATA);
		
		/* all data fed to each tree, the same data?? */
		_trainer.setTrainingDataSize(NUM_DATA); // TODO????

		
		forest = new ArrayList<DecisionTree> (FOREST_SIZE);
		classifier_accuracy = new ArrayList<Double>(FOREST_SIZE);
		// weight for each instance - the higher the weight, the more the instance influences the classifier learned.
		double [] weights = new double [NUM_DATA];
		for (int index_i=0; index_i<NUM_DATA; index_i++) {		
			weights[index_i] = 1.0d/(double)NUM_DATA;
			class_instances.getInstance(index_i).setWeight(weights[index_i] * (double)NUM_DATA);
			if (slog.debug() != null)
				slog.debug().log(index_i+" new weight:"+class_instances.getInstance(index_i).getWeight()+ "\n");
		}
		
		int i = 0;
//			int[] bag;		
		while (i++ < FOREST_SIZE ) {
//			if (WITH_REP)
//				bag = Util.bag_w_rep(NUM_DATA, N);
//			else
//				bag = Util.bag_wo_rep(NUM_DATA, N);	
			
			InstanceList working_instances = class_instances; //.getInstances(bag);			
			DecisionTree dt = _trainer.train_tree(working_instances);
			dt.setID(i);
			
			double error = 0.0, sum_weight = 0.0;
			SingleTreeTester t= new SingleTreeTester(dt);
			for (int index_i = 0; index_i < NUM_DATA; index_i++) {
				Integer result = t.test(class_instances.getInstance(index_i));
				sum_weight += weights[index_i];
				if (result == Stats.INCORRECT) {
					error += weights[index_i];
					if (slog.debug() != null)
						slog.debug().log("[e:"+error+" w:"+weights[index_i]+ "] ");
				}
			}
			
			error = error / sum_weight; // forgotton
			if (error > 0.0f) {
				double alpha = Util.ln( (1.0d-error)/error ) / 2.0d;
				
				if (error < 0.5d) {
					// The classification accuracy of the weak classifier
					classifier_accuracy.add(alpha);
					
					double norm_fact= 0.0d;
					// Boosting the missclassified instances
					for (int index_i = 0; index_i < NUM_DATA; index_i++) {
						Integer result = t.test(class_instances.getInstance(index_i));//TODO dont need to test two times
						switch (result) {
						case Stats.INCORRECT:
							weights[index_i] = weights[index_i] * Util.exp(alpha);
							break;
						case Stats.CORRECT:	// if it is correct do not update
							//weights[index_i] = weights[index_i] * Util.exp(-1.0d * alpha);
							break;
						case Stats.UNKNOWN:
							if (slog.error() !=null)
								slog.error().log("Unknown situation bok\n");
							System.exit(0);
							break;
						}
						norm_fact += weights[index_i];
					}
					// Normalization of the weights
					for (int index_i = 0; index_i < NUM_DATA; index_i++) {
						weights[index_i] = weights[index_i] / norm_fact;
						class_instances.getInstance(index_i).setWeight(weights[index_i] * (double)NUM_DATA);
					}
				} else {
					if (slog.debug() != null)
						slog.debug().log("The error="+error+" alpha:"+alpha+ "\n");
					if (slog.error() != null)
						slog.error().log("error:"+error + " alpha will be negative and the weights of the training samples will be updated in the wrong direction"+"\n");
					FOREST_SIZE = i-1;//ignore the current tree
					break;
				}
			}
			
			
			else {
				if (slog.stat() != null) {
					slog.stat().log("\n Boosting ends: ");
					slog.stat().log("All instances classified correctly TERMINATE, forest size:"+i+ "\n");
				}
				// What to do here??
				FOREST_SIZE = i;
				classifier_accuracy.add(10.0); // TODO add a very big number
				
				
			}
			
			
			forest.add(dt);
			// the DecisionTreeMerger will visit the decision tree and add the paths that have not been seen yet to the list
			merger.add(dt);	

			if (slog.stat() !=null)
				slog.stat().stat(".");

		}
		// TODO how to compute a best tree from the forest
		DecisionTree best = merger.getBest();
		if (best == null)
			best = forest.get(0);
		
		_trainer.setBestTree(forest.get(0));
		//this.c45 = dt;
	}

	public ArrayList<DecisionTree> getTrees() {
		return forest;
	}
	public ArrayList<Double> getAccuracies() {
		return classifier_accuracy;
	}
	public TreeAlgo getTreeAlgo() {
		return algorithm; //TreeAlgo.BAG; // default
	}
	public void clearForest(int j) {
		//forest = null;
		for (int i = forest.size()-1; i >j; i--)
			forest.remove(i);
		
	}
}
