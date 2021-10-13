import java.util.*;


/**
 * This is a genetic algorithm simulator. Given an input from the command line, this will simulate genetic algorithm until
 * the maximum generation is met or until a child equals the target. The genetic algorithm class contains helper methods
 * used in the main method to solve the two towers problem. Given a number 
 * @author Tugi Davaadorj
 */

public class GATwoTowers {
    private static double target;
    private Random rand = new Random();
    private int blockNum; 
    private List<Double> blocks;

    /*
     * Initializes the GA and calculates the target and creates a list containing all the blocks for the towers
     * */
    public GATwoTowers(int num) {
    	blocks = new ArrayList<>();
    	blockNum = num;
        for (int i = 1; i <= num; i++) {
        	double sideLen = Math.sqrt(i);
        	blocks.add(i * 1.0);
        	target += sideLen / 2;
        }
    }
    
    
    /*
     * This method will use generate random strings given N 
     * @param num of string to generate
     * @return list of randomly generated strings 
      */
    public List<Set<Double>> genParents(int num, double probToSelectBlock) {
        List<Set<Double>> list = new ArrayList<>(num);
        for (int x = 0; x < num; x ++) {
        	Set<Double> parent = new HashSet<Double>();
        	for (int y = 0; y < blockNum; y++) {
        		if (Math.random() <= probToSelectBlock) {
        			parent.add(blocks.get(y));
        		}
        	}
        	list.add(parent);
        }
        return list;
    }

    /*
     * This method will use Rank Selection from the fitness ArrayList. This will randomly select
     * two elements from the breeding pool where the odd is proportional to the rank (Better score get higher rank)
     * @param the ordered list of fitness of the entire breeding pool
     * @return index of 2 parents 
      */
    public List<Integer> RankSelection(List<Double> fitness) {
        //The index of list is proportional to the rank since our input is already sorted
        int n = fitness.size();

        //getting two random numbers between zero and sum of ranks
        int sumRank = (n * (n + 1)) / 2;
        int randRank1 = rand.nextInt(sumRank);

        //selects first parent
        int seen = 0;
        int parent1 = 0;
        for (int x = 0; x < n; x++) {
            seen += x;
            if (randRank1 < seen) {
                parent1 = x;
                sumRank -= x;
                break;
            }
        }

        //selects 2nd parent without including parent 1 
        seen = 0;
        int parent2 = 0;
        int randRank2 = rand.nextInt(sumRank);
        for (int x = 0; x < n; x++) {
            if (x == parent1) {
                continue;
            }
            seen += x;
            if (randRank2 < seen) {
                parent2 = x;
                break;
            }
        }
        ArrayList<Integer> select = new ArrayList<Integer>();
        select.add(parent1);
        select.add(parent2);
        return select;
    }

    public List<Integer> greedy(List<Integer> fitness) {
        List<Integer> list = new ArrayList<>();
        list.add(fitness.size() - 1);
        list.add(fitness.size() - 2);
        return list;
    }

    /*
     * This method will use Boltzmann Selection from the fitness ArrayList. This will randomly select
     * two elements from the breeding pool where the odd is proportional to the e^(fitness score) 
     * @param the list of fitness of the entire breeding pool
     * @return index of 2 parents 
      */
    public List<Integer> BoltzmannSelection(List<Double> fitness){
        double boltzSum = 0;
        ArrayList<Double> boltz = new ArrayList<Double>();

        //converts fitness values to Boltzmann equivalent and calculates the sum of all Boltzmann values
        for (int x = 0; x < fitness.size(); x++) {
            double curBolt = Math.exp(fitness.get(x));
            boltz.add(curBolt);
            boltzSum += curBolt;
        }
        
        //selects the first parent
        double seen = 0;
        double randBoltz1 = rand.nextDouble() * boltzSum;
        int parent1 = 0;
        for (int x = 0; x < fitness.size(); x++) {
            seen += boltz.get(x);
            if (randBoltz1 < seen) {
                parent1 = x;
                boltzSum -= boltz.get(x);
                boltz.set(x, 0.0);
                break;
            }
        }

        //selects the second parent and ensures it is not the first parent
        seen = 0.0;
        int parent2 = 0;
        double randBoltz2 = rand.nextDouble() * boltzSum;
        for (int x = 0; x < fitness.size(); x++) {
            seen += boltz.get(x);
            if (randBoltz2 < seen) {
                parent2 = x;
                break;
            }
        }

        ArrayList<Integer> select = new ArrayList<Integer>();
        select.add(parent1);
        select.add(parent2);
        return select;
    }
    
    /*
     * This method will use Tournament Selection from the fitness ArrayList. This will randomly select
     * three random elements from the breeding pool and will return the top two fitness score index 
     * @param the list of fitness of the entire breeding pool
     * @return index of 2 parents 
      */
    public List<Integer> TournamentSelection(List<Double> fitness){
        int randParent1 = rand.nextInt(fitness.size());
        int randParent2 = -1;

        //to prevent a self-tournament
        while (randParent2 == -1 || randParent2 == randParent1) {
            randParent2 = rand.nextInt(fitness.size());
        }

        int randParent3 = -1;
        //to prevent a self-tournament
        while (randParent3 == -1 || randParent3 == randParent1 || randParent3 == randParent2) {
            randParent3 = rand.nextInt(fitness.size());
        }

        //To get the top two parent, we get the worst score out of the three
        int worstIndex = randParent1;;
        double minScore = fitness.get(randParent1);
        if (fitness.get(randParent2) < minScore) {
            worstIndex = randParent2;
            minScore = fitness.get(randParent2);
        }if (fitness.get(randParent3) < minScore) {
            worstIndex = randParent3;
            minScore = fitness.get(randParent3);
        }

        //return the top two (the parents that don't have worst score out of the three)
        List<Integer> select = new ArrayList<Integer>();
        if (randParent1 != worstIndex) {
            select.add(randParent1);
        }
        if (randParent2 != worstIndex) {
            select.add(randParent2);
        }
        if (randParent3 != worstIndex) {
            select.add(randParent3);
        }
        return select;
    }

    /*
    * This method counts the score/fitness of a set of blocks
    * @param current string and the target string
    * @return number of characters that match up with the target string.
     */
    public double score(Set<Double> current) {
        double count = 0.0;
        for (double block: current) {
        	count += Math.sqrt(block);
        }
        return Math.abs(target - count)*-1.0;
    }

    /*
     * This method does the crossover by splitting at a random point and crossover
     * @param the selected strings, or parents
     * @return a tuple of children; parents that have been crossover-ed
     */
    public List<Set<Double>> crossover(Set<Double> parent1, Set<Double> parent2) {
        int split = rand.nextInt(Math.min(parent1.size(), parent2.size()));
        Set<Double> child1 = new HashSet<>(blockNum);
        Set<Double> child2 = new HashSet<>(blockNum);
        
        int counter = 0;
        for (Double block: parent1) {
        	if (counter <= split) {
        		child1.add(block);
        		counter++;
        	} else {
        		break;
        	}
        }
        for (Double block: child1) {
        	parent1.remove(block);
        }
        
        counter = 0;
        for (Double block: parent2) {
        	if (counter <= split) {
        		child2.add(block);
        		counter++;
        	} else {
        		break;
        	}
        }
        
        for (Double block: child2) {
        	parent2.remove(block);
        }
        
        child1.addAll(parent2);
        child2.addAll(parent1);
        
        List<Set<Double>> children = new ArrayList<>();
        
        children.add(child1);
        children.add(child2);

        return children;
    }
    
    /*
     * This method does the crossover by looking for a cross-over at every possible block
     * @param the selected strings, or parents
     * @return a tuple of children; parents that have been crossover-ed
     */
    public List<Set<Double>> fixedCrossover(Set<Double> parent1, Set<Double> parent2, double probCrossover) {
        int[] blocks1 = new int[blockNum+1];
        int[] blocks2 = new int[blockNum+1];
        
        for (Double block: parent1) {
        	blocks1[block.intValue()] = 1;
        }
        
        for (Double block: parent2) {
        	blocks2[block.intValue()] = 1;
        }
        
        for (int x = 1; x<= blockNum; x++ ) {
        	//swap
        	if(rand.nextDouble() <= probCrossover) {
        		int temp = blocks1[x];
        		blocks1[x] = blocks2[x];
        		blocks2[x] = temp;
        	}
        }
        
        //convert the array back to set;
        Set<Double> child1 = new HashSet<>(blockNum);
        Set<Double> child2 = new HashSet<>(blockNum);
        
        for (int x = 1; x<= blockNum; x++ ) {
        	//swap
        	if(blocks1[x] == 1) {
        		child1.add(x*1.0);
        	}
        	if(blocks2[x] == 1) {
        		child2.add(x*1.0);
        	}
        }
        
        List<Set<Double>> children = new ArrayList<>();
        
        children.add(child1);
        children.add(child2);

        return children;
    }

    /*
     * This methods mutate the selected strings by replacing characters with random characters by chance
     * @param the selected strings, and the chance of mutating passed in from main.
     * @return a tuple of children; parents that have been mutated
     */
    public List<Set<Double>> mutate(Set<Double> parent1, Set<Double> parent2, double chanceToMutate) {
    	Set<Double> child1 = new HashSet<>(blockNum);;
    	Set<Double> child2 = new HashSet<>(blockNum);;
    	List<Set<Double>> children = new ArrayList<>();
    	
    	//Loops through parent one with a chance of mutation
    	for (Double block: parent1) {
    		if (Math.random() <= chanceToMutate) {
    			int split = rand.nextInt(blockNum);
    			child1.add(blocks.get(split));
    		} else {
    			child1.add(block);
    		}
		}
          
          
        children.add(child1);

        //Loops through parent two with a chance of mutation
        for (Double block: parent2) {
    		if (Math.random() <= chanceToMutate) {
    			int split = rand.nextInt(blockNum);
    			child2.add(blocks.get(split));
    		} else {
    			child2.add(block);
    		}
		}
        children.add(child2);

        return children;
    }
    
    
    /*
     * This method creates a new breeding pool of best possible solutions until it finds target
     * or maximum generation is reached. It also outputs results every specified interval. This uses
     * the three different types of selections and does crossover and mutation. 
     * @param args from command line
      */
    public static void main(String[] args) {
        GATwoTowers func = new GATwoTowers(15);
        Random rand = new Random();
        
        //Used for testing purposes
        String[] arg = {"100", "ts", "0.4", "0.05", "20000", "100"};
        
        int populationSize = Integer.parseInt(arg[0]);
        String selectionType = arg[1];
        double pC = Double.parseDouble(arg[2]);
        double pM = Double.parseDouble(arg[3]);
        int maxGen = Integer.parseInt(arg[4]);     
        int disInterval = Integer.parseInt(arg[5]);
        
        List<Set<Double>> population = func.genParents(populationSize, .9);
        List<Set<Double>> newPopulation = new ArrayList<>();

        //pre-sort the population pool so selecting methods can function properly
        Collections.sort(population, new Comparator<Set<Double>>() {
            @Override
            public int compare(Set<Double> s1, Set<Double> s2) {
                double score1 = func.score(s1);
                double score2 = func.score(s2);

                return Double.compare(score1, score2);
            }
        });
        
        List<Double> fitness = new ArrayList<>();
        for (Set<Double> s : population) {
            fitness.add(func.score(s));
        }

        int generation = 0;
        
        
        
        while (generation < maxGen) {
            //generate a new population pool from crossing-over and mutating
            while (newPopulation.size() < populationSize) {
                List<Integer> selected = new ArrayList<>();
                if (selectionType.equals("rs")){
                    selected = func.RankSelection(fitness);
                } else if (selectionType.equals("ts")) {
                    selected = func.TournamentSelection(fitness);
                } else {
                    selected = func.BoltzmannSelection(fitness);
                }

                Set<Double> p1 = population.get(selected.get(0));
                Set<Double> p2 = population.get(selected.get(1));
                List<Set<Double>> parents = new ArrayList<>();
                parents.add(p1);
                parents.add(p2);

                parents = func.fixedCrossover(p1, p2, pC);
                //if (rand.nextDouble() <= pC) {
                //    parents = func.crossover(p1, p2);
                //}

                parents = func.mutate(parents.get(0), parents.get(1), pM);

                newPopulation.add(parents.get(0));
                newPopulation.add(parents.get(1));
            }

            Collections.copy(population, newPopulation);
            newPopulation.clear();

            //sort the new population pool so we can find the fittest string (at the last index);
            Collections.sort(population, new Comparator<Set<Double>>() {
                @Override
                public int compare(Set<Double> s1, Set<Double> s2) {
                    double score1 = func.score(s1);
                    double score2 = func.score(s2);

                    return Double.compare(score1, score2);
                }
            });
            fitness.clear();
            for (Set<Double> s : population) {
                fitness.add(func.score(s));
            }

            int bestIndex = populationSize - 1;

            if (generation % disInterval == 0) {
                System.out.println("Generation " + generation + " ( Distance from target: " + fitness.get(bestIndex)+ "): " + population.get(bestIndex));
            }
            generation++;
            if (fitness.get(populationSize-1) == 0) {
                System.out.println("Hit the target!!");
                System.out.println("Target:           " + target);
                System.out.println("Distance From Target:  " +fitness.get(bestIndex));
                System.out.println("Best subset:  " + population.get(bestIndex));
                System.out.println("Generation Found: " + generation);
                break;
            }
            //last gen report
            if (generation == maxGen) {
                System.out.println("Missed the target...");
                System.out.println("Target:           " + target);
                System.out.println("Distance From Target:  " +fitness.get(bestIndex));
                System.out.println("Best subset:  " + population.get(bestIndex));
                System.out.println("Generation Found: " + generation);
            }
        }
    }
}

