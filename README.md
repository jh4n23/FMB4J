How to run (requires Java JDK and Maven):
- Navigate to project folder (FMB4J)
- Run `mvn compile`
- On Linx / Mac, run `mvn exec:java -Dexec.mainClass=Main -Dexec.args="path/to/file false"`
- On Windows (CMD or Powershell), run `mvn exec:java "-Dexec.mainClass=Main" "-Dexec.args=path/to/file.txt false"`

The first argument (`path/to/file`) should be the path from the project root to the plaintext file containing the CNF problem that should be run. 
The second argument represents verbose mode, i.e., whether to output the decoded model, function assignments, etc. It's usually useful to set this to true, but not required if all that matters is if a model is found or not.
