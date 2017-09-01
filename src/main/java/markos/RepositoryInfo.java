package markos;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

/* 
 * This class is responsible for getting repo information
*/
public class RepositoryInfo {

	Git git;
	
	public Repository getRepo() throws IOException{
		Scanner ler = new Scanner(System.in);
		 String s;
		 System.out.printf("Informe a URL do repositorio:\n");
		 s = ler.next();
		 ler.close();
		
		 String URI = s + ".git";
		 String[] split = URI.split("/");
		 String dir = split[split.length-1].substring(0, split[split.length-1].length()-4);

		 //Clone the respository in this project's directory
		 String direc = System.getProperty("user.dir");
		 dir = direc + "-Repository-" + dir;

		 git = Git.cloneRepository()
				  .setURI( URI )
				  .setDirectory( new File(dir) )
				  .call();
		 
		 System.out.println("\nRepository cloned to the root directory.\n");
		 File gitDir = new File(dir + "/.git");

		 RepositoryBuilder builder = new RepositoryBuilder();
		 Repository repository;
		 repository = builder.setGitDir(gitDir).readEnvironment()
		         .findGitDir().build();
		 
		 return repository;
	}
	
	public Git getGit(){
		return git;
	}
}
