package edu.sjsu.cs255.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;
import edu.sjsu.cs255.exceptions.EmptyItemSetException;

public class AssociationRules {

	//Get the frequent item sets
	//Generate non-empty item sets from frequent item sets (say I)
	//If 's' is there, customer should be shown (recommends) (I-s)
	//If (support count of I)/(support count of S) >= min_confidence (input)
	private List<Itemset> freqItems;
	private Map<Set<Integer>,Set<Integer>> rules;
	
	public AssociationRules(List<Itemset> items) {
		rules = new HashMap<Set<Integer>, Set<Integer>>();
		if(items.isEmpty())
			throw new EmptyItemSetException("Frequent Itemset is empty!");
		freqItems = items;
	}
	public void generate(int confidence){
		rules.clear(); //Clear any existing rules
//		System.out.println("Confidence: " + confidence);
		Itemset itemset = freqItems.get(freqItems.size()-1);
		List<Set<Integer>> sets = itemset.getSets();
		Iterator<Set<Integer>> it = sets.iterator();
		while(it.hasNext()) { // All the final frequent itemsets
			Set<Integer> I = it.next();
			int sc_i = itemset.getFrequency(I);
			//generate all subsets
			Set<Set<Integer>> powerSet = Sets.powerSet(I);
			List<Set<Integer>> setList = new ArrayList<Set<Integer>>(powerSet);
			setList.remove(I);                        // remove self
			setList.remove(new HashSet<Integer>());   // remove null
			Iterator<Set<Integer>> S = setList.iterator();
			
			while(S.hasNext()) {
				Set<Integer> s = S.next();
				int sc_s = freqItems.get(s.size() - 1).getFrequency(s);
				if((float)sc_i / sc_s * 100 >= confidence) {
					HashSet<Integer> iDiffS = new HashSet<Integer>(I);
					iDiffS.removeAll(s);
					rules.put(s, iDiffS); // {S} -> {I - s}
				}
			}
		}
	}
	
	public void printRules(DataCatalog dataCatalog) {
		StringBuilder stringBuilderRules = new StringBuilder();
		Iterator<Set<Integer>> iterator = rules.keySet().iterator();
		
		if (dataCatalog == null)
			this.toString();
		else{	
			while(iterator.hasNext()) {
				Set<Integer> itemids = iterator.next();
				stringBuilderRules.append(this.getMapping(itemids, dataCatalog));
				stringBuilderRules.append(" recommends -> ");
				stringBuilderRules.append(this.getMapping(rules.get(itemids), dataCatalog));
				stringBuilderRules.append("\n");
			}
			System.out.println(stringBuilderRules.toString());
		}	
	}
	
	private List<String> getMapping(Set<Integer> itemids, DataCatalog dataCatalog) {
		List<String> itemNames = new ArrayList<String>();
		Iterator<Integer> iterator = itemids.iterator();
		while(iterator.hasNext()) {
			String itemName = dataCatalog.getName(iterator.next());
			if(itemName!=null)
				itemNames.add(itemName);
			else
				itemNames.add("Item name not found");
		}
		return itemNames;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		Iterator<Set<Integer>> iteratorRules = rules.keySet().iterator();
		while(iteratorRules.hasNext()) {
			Set<Integer> set = iteratorRules.next();
			s.append(set);
			s.append(" recommends -> ");
			s.append(rules.get(set));
			s.append("\n");
		}
		
		return s.toString();
	}
	
	public Set<Set<Integer>> keySet() {
		return rules.keySet();
	}
	
	public Set<Integer> getImplications(Set<Integer> key) {
		return rules.get(key);
	}
	
}
