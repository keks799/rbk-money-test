Reconciliation application.
Using Java 8, Spring boot, xtext, camel.

To run, first you should point directory with reconciliation file and directory to store report files. You can do it in properties file.

* camel.outer.data.input - reconciliation csv file
* report.dir.filename - report directory and file name format

Than `mvn clean package` and then run jar using `java -jar [jar_name]`
