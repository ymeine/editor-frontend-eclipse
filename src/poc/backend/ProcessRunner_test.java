package poc.backend;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.ArrayList;



public class ProcessRunner_test {
	public static void main(String[] args) {
		System.out.println("Hello Node!");
		System.out.println(System.getProperty("user.dir"));
		
		ProcessRunner runner = new ProcessRunner();
		
		runner.setProcessPath("runtime\\node.exe");
		
		List<String> arguments = new ArrayList<String>();
		//arguments.add("-e \"console.log('Hello Java!')\"");
		arguments.add("runtime/test.js");
		runner.setArguments(arguments);
		
		//runner.setStdout(Redirect.INHERIT);
		runner.setStdout(Redirect.PIPE);
		
		try {
			Process process = runner.start();
			InputStream processStdout = process.getInputStream();
			System.out.println(processStdout);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
