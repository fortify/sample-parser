package com.thirdparty;

import com.fortify.plugin.api.BasicVulnerabilityBuilder;
import com.thirdparty.scan.Finding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateraj on 15.9.2017.
 */
class SteadySampleScan {

    static final List<Finding> STEADY_FINDINGS = generateSteadyFindings();

    static final String ENGINE_VERSION = "1.0-SNAPSHOT";
    static final String SCAN_DATE = "2017-04-18T23:31:42.136Z";
    static final String BUILD_SERVER = "server01";
    static final int ELAPSED = 860;

    private static List<Finding> generateSteadyFindings() {
        List<Finding> findingList = new ArrayList<>();

        Finding fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("fda2eaa2-7643-4fc5-809e-3eb6957e1945");
        // Builtin attributes:
        fn.setCategory("Cross-site Scripting");
        fn.setFileName("file-fda2eaa2-7643-4fc5-809e-3eb6957e1945/00000001.bin");
        fn.setVulnerabilityAbstract("Cross-site Scripting found in file-fda2eaa2-7643-4fc5-809e-3eb6957e1945/00000001.bin");
        fn.setLineNumber(103);
        fn.setConfidence(4.968653f);
        fn.setImpact(200.690f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.Critical);
        // Custom attributes:
        fn.setCategoryId("a101");
        fn.setArtifact("artifact-fda2eaa2-7643-4fc5-809e-3eb6957e1945/00000001.jar");
        fn.setDescription("Cross-site scripting (XSS) is a type of computer security vulnerability typically found in web applications. XSS enables attackers to inject client-side scripts into web pages viewed by other users. A cross-site scripting vulnerability may be used by attackers to bypass access controls such as the same-origin policy. Cross-site scripting carried out on websites accounted for roughly 84% of all security vulnerabilities documented by Symantec as of 2007.[1] Their effect may range from a petty nuisance to a significant security risk, depending on the sensitivity of the data handled by the vulnerable site and the nature of any security mitigation implemented by the site's owner.");
        fn.setComment("This should be fixed");
        fn.setBuildNumber("300.3837014436722");
        fn.setCustomStatus(ScanGenerator.CustomStatus.OPEN.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("fda2eaa2-7643-4fc5-809e-3eb6957e1999");
        // Builtin attributes:
        fn.setCategory("Cross-site Scripting");
        fn.setFileName("file-fda2eaa2-7643-4fc5-809e-3eb6957e1999/00000021.bin");
        fn.setVulnerabilityAbstract("Cross-site Scripting found in file-fda2eaa2-7643-4fc5-809e-3eb6957e1999/00000021.bin");
        fn.setLineNumber(146);
        fn.setConfidence(4.968653f);
        fn.setImpact(200.690f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.Critical);
        // Custom attributes:
        fn.setCategoryId("a101");
        fn.setArtifact("artifact-fda2eaa2-7643-4fc5-809e-3eb6957e1999/00000001.jar");
        fn.setDescription("Cross-site scripting (XSS) is a type of computer security vulnerability typically found in web applications. XSS enables attackers to inject client-side scripts into web pages viewed by other users. A cross-site scripting vulnerability may be used by attackers to bypass access controls such as the same-origin policy. Cross-site scripting carried out on websites accounted for roughly 84% of all security vulnerabilities documented by Symantec as of 2007.[1] Their effect may range from a petty nuisance to a significant security risk, depending on the sensitivity of the data handled by the vulnerable site and the nature of any security mitigation implemented by the site's owner.");
        fn.setComment("This should be fixed");
        fn.setBuildNumber("300.3837014436722");
        fn.setCustomStatus(ScanGenerator.CustomStatus.OPEN.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("fda2eaa2-7643-4fc5-809e-3eb6957e1946");
        // Builtin attributes:
        fn.setCategory("Cross-site Scripting");
        fn.setFileName("file-fda2eaa2-7643-4fc5-809e-3eb6957e1946/00000011.bin");
        fn.setVulnerabilityAbstract("Cross-site Scripting found in file-fda2eaa2-7643-4fc5-809e-3eb6957e1946/00000011.bin");
        fn.setLineNumber(489);
        fn.setConfidence(4.968653f);
        fn.setImpact(200.690f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.Critical);
        // Custom attributes:
        fn.setCategoryId("a101");
        fn.setArtifact("artifact-fda2eaa2-7643-4fc5-809e-3eb6957e1946/00000001.jar");
        fn.setDescription("Cross-site scripting (XSS) is a type of computer security vulnerability typically found in web applications. XSS enables attackers to inject client-side scripts into web pages viewed by other users. A cross-site scripting vulnerability may be used by attackers to bypass access controls such as the same-origin policy. Cross-site scripting carried out on websites accounted for roughly 84% of all security vulnerabilities documented by Symantec as of 2007.[1] Their effect may range from a petty nuisance to a significant security risk, depending on the sensitivity of the data handled by the vulnerable site and the nature of any security mitigation implemented by the site's owner.");
        fn.setComment("fixed in build 303.0001");
        fn.setBuildNumber("300.3837014436722");
        fn.setCustomStatus(ScanGenerator.CustomStatus.REMEDIATED.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95fee3");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.bin");
        fn.setLineNumber(8409);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("fixed in build 300.845200451");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.REMEDIATED.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95fe11");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95fe11/00000002.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95fe11/00000002.bin");
        fn.setLineNumber(1001);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("fixed in build 300.845200451");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.REMEDIATED.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95fe12");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95fe12/00000003.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95fe12/00000003.bin");
        fn.setLineNumber(423);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.OPEN.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95ffx5");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95ffx5/00000042.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95ffx5/00000042.bin");
        fn.setLineNumber(8409);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("fixed in build 300.845200451");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.REMEDIATED.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95fe88");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95fe88/00000008.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95fe88/00000008.bin");
        fn.setLineNumber(409);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95feag/00000012.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.NEW.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95f111");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95f111/00000018.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95f111/00000018.bin");
        fn.setLineNumber(22);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fe88/00000008.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.NEW.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        fn = new Finding();
        // Mandatory custom attributes:
        fn.setUniqueId("c834c327-4cee-4420-b1f8-b24bea95fe55");
        // Builtin attributes:
        fn.setCategory("SQL Injection");
        fn.setFileName("file-c834c327-4cee-4420-b1f8-b24bea95fe55/00000007.bin");
        fn.setVulnerabilityAbstract("SQL Injection found in file-c834c327-4cee-4420-b1f8-b24bea95fe55/00000007.bin");
        fn.setLineNumber(112);
        fn.setConfidence(2.941967f);
        fn.setImpact(200.696f);
        fn.setPriority(BasicVulnerabilityBuilder.Priority.High);
        // Custom attributes:
        fn.setCategoryId("c121");
        fn.setArtifact("artifact-c834c327-4cee-4420-b1f8-b24bea95fee3/00000002.jar");
        fn.setDescription("SQL injection is a code injection technique, used to attack data-driven applications, in which nefarious SQL statements are inserted into an entry field for execution (e.g. to dump the database contents to the attacker).[1] SQL injection must exploit a security vulnerability in an application's software, for example, when user input is either incorrectly filtered for string literal escape characters embedded in SQL statements or user input is not strongly typed and unexpectedly executed. SQL injection is mostly known as an attack vector for websites but can be used to attack any type of SQL database.");
        fn.setComment("");
        fn.setBuildNumber("300.314668238163");
        fn.setCustomStatus(ScanGenerator.CustomStatus.OPEN.toString());
        fn.setLastChangeDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-16T21:31:42.092Z"));
        fn.setArtifactBuildDate(ScanGenerator.DATE_DESERIALIZER.convert("2017-04-17T22:31:42.092Z"));
        fn.setText64("Example of a text encoded in the original scan to Base64. \n" + longText);
        findingList.add(fn);

        return findingList;
    }

private static final String longText = "From Wikipedia: \n"+
    "\n"+
    "Computer security, also known as cyber security or IT security, is the protection of computer systems from the theft or damage to their hardware, software or information, as well as from disruption or misdirection of the services they provide. \n"+
    "\n"+
    "Cyber security includes controlling physical access to the hardware, as well as protecting against harm that may come via network access, data and code injection. Also, due to malpractice by operators, whether intentional, accidental, IT security is susceptible to being tricked into deviating from secure procedures through various methods.\n";

}
