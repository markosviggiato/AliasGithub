package markos;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

public class Alias{

	static List<String> nameEmail = new ArrayList<String>();
	static List<String> alias = new ArrayList<String>();
	static Map<String, List<String[]>> aliasMap = new HashMap<String, List<String[]>>();
	static NormalizedLevenshtein l = new NormalizedLevenshtein();
	static String url = null;
	
	public static void main(String[] args) throws NoHeadException, JGitInternalException, IOException {
			 try {	
				 
				 //Read input URL
				 int count=0;	
				 RepositoryInfo repo = new RepositoryInfo();
				 Repository repository = repo.getRepo();
				 Git git = repo.getGit();
				 
				 RevWalk walk = new RevWalk(repository);
				 RevCommit commit = null;
				 
				 Iterable<RevCommit> logs = git.log().call();
				 Iterator<RevCommit> i = logs.iterator();
	
				 while (i.hasNext()) {
				     commit = walk.parseCommit( i.next() );
				     String name = commit.getAuthorIdent().getName();
				     String email = commit.getAuthorIdent().getEmailAddress();
				     if( !email.contains("noreply"))
				    	 alias(name, email);
				     count++;
	
				 }
	
				 System.out.println("---------------------- ALIAS ----------------------\n");
				 
				 reorganizeAliasMap(aliasMap); //to identify chains of alias
				 
				 for (String key : aliasMap.keySet()) {
	                 
	                 List<String[]> value = aliasMap.get(key);
	                 System.out.println("User: " + key);
	                 for(int j=0; j<value.size(); j++){
	                	 String[] values = value.get(j);
	                	 System.out.println("Alias:" + values[0] + " - " + values[1] + "\n");
	                 }
	          }
				 System.out.println("Number of alias: " + aliasMap.size());
				 
	        }catch(Exception e){
	         }
		}
	
	//Add alias to the alias HashMap
	public static void alias(String name, String email){
	try{
		if(nameEmail.size() == 0){
			if( !name.contains("?")){
				nameEmail.add(name);
				nameEmail.add(email);
			}
			return;
		}
		
		boolean alreadyExists = false;
		for(int j=0; j<nameEmail.size(); j=j+2){
			if(nameEmail.get(j).equals(name) && nameEmail.get(j+1).equals(email)){
				alreadyExists = true;
				break;
			}
		}
		
		if( !alreadyExists ){
			if( !name.contains("?")){
				nameEmail.add(name);
				nameEmail.add(email);
			}
		}
		
		for(int i=0; i<nameEmail.size(); i=i+2){
			
			if( heuristic(nameEmail, name, email, i) ){
				String user = nameEmail.get(i) + " - " + nameEmail.get(i+1);
				
				//Verify if this user already has alias
				boolean exists=false;
				if(aliasMap.containsKey(user)){
					if(aliasMap.get(user).size() != 0){
						List<String[]> aliasListExists = aliasMap.get(user);
						for(String[] s:aliasListExists){
							if( s[0].equals(name) && s[1].equals(email)){
								exists=true;break;
							}
						}
						if(!exists){
							if( !name.contains("?")){
								String[] aliasToPut = {name, email};
								aliasListExists.add(aliasToPut);
								aliasMap.replace(user, aliasListExists);
							}
						}
						
					}
				
				}else{
					if( !aliasMap.containsKey(name + " - " + email)){
						if( !name.contains("?")){
							List<String[]> aliasList = new ArrayList<String[]>();
							String[] alias = {name, email};
							aliasList.add(alias);
							aliasMap.put(user, aliasList);
						}
					}
				}
			}
		}
		
		return;
	}catch(Exception e){
		System.out.println(e);
	}
	}
	
	//HEURISTIC
	public static boolean heuristic(List<String> nameEmail, String name, String email, int i){
		if( (1-l.distance(name, nameEmail.get(i+1).split("@")[0]))>=0.93 && !nameEmail.get(i+1).equals(email)  ||
				
			(1-l.distance(nameEmail.get(i), email.split("@")[0]))>=0.93 && !nameEmail.get(i+1).equals(email)  ||
				
			!nameEmail.get(i).equals(name) && nameEmail.get(i+1).equals(email) )

			return true;
		else
			return false;
	}
	
	//Only for data structure organization purpose
	public static void reorganizeAliasMap(Map<String, List<String[]>> aliasMap){
	try{	
		String keyToExclude = null;
		String keyToExpand = null;
		
		for (String key : aliasMap.keySet()) {
			
			for (String key2 : aliasMap.keySet()) {
				List<String[]> values = aliasMap.get(key2);
				for(String[] s:values){
					if( key.equals(s[0] + " - " + s[1])){
						keyToExclude = key;
						keyToExpand = key2;
						break;
					}
				}
			}
		}
		
		if(!keyToExclude.equals(null) && !keyToExpand.equals(null)){
			List<String[]> toAdd = aliasMap.get(keyToExclude);
			List<String[]> toReceive = aliasMap.get(keyToExpand);
			for( String[] value:toAdd){
				toReceive.add(value);
			}
			aliasMap.remove(keyToExclude);
		}
		
		
		
		if(keyToExclude.equals(null) && keyToExpand.equals(null)){
			for (String key : aliasMap.keySet()){
				List<String[]> list = aliasMap.get(key);
				for(int i=0; i<list.size(); i++){
					for(int j=i+1; j<list.size();j++){
						if( list.get(i)[0].equals(list.get(j)[0]) && list.get(i)[1].equals(list.get(j)[1]) )
							aliasMap.remove(key, list.get(j));
					}
				}
			}
			return;
		}
		else
			reorganizeAliasMap(aliasMap);
	}catch(Exception e){

	}
	}
	
}
	


