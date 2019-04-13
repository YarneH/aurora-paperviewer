package com.aurora.paperviewerprocessor.paper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.aurora.auroralib.PluginObject;
import com.aurora.paperviewerprocessor.App;
import com.aurora.paperviewerprocessor.R;

import java.util.ArrayList;
import java.util.List;

public class Paper extends PluginObject {

    /**
     * Dummy constant for dummy data
     */
    private static final String BR_BR_CONSTANT = "<br><br>";
    /**
     * Dummy constant for dummy data
     */
    private static final String BR_BR_BR_CONSTANT = "<br><br><br>";
    // TODO: remove the dummy data
    private static final String AUTHOR = "Stijn";
    private static final String TITLE = "What is all the fuzz about?";
    private static final String ABSTRACT_CONTENT =
            "Fuzzing is an automated software testing technique that has proven " +
            "to be remarkably effective in finding bugs and vulnerabilities in various " +
            "programs. Most existing surveys on the topic discuss the conceptual principles of fuzzing. " +
            "The present survey paper attempts to illustrate these concepts with practical examples " +
            "from industry and academia. This survey is directed at people who are new to the field of fuzzing " +
            "as well as people with more experience, who want to refresh their general knowledge " +
            "on the topic.";
    private static final Section DUMMY_SECTION1 = new Section("Introduction",
            "Software security is becoming " +
            "increasingly more important. With the Internet of Things, more and more devices are " +
                    "connected " +
            "to the internet. Usually, programmers use low-level languages like C or C++ to program " +
                    "these devices." +
            " However, these languages do not provide many  security  guarantees,  which  makes  " +
                    "many  of" +
            " the devices prone to bugs and vulnerabilities." +
            BR_BR_CONSTANT +
            "Software testing and finding bugs has been a manual task for a long time, " +
            "but in the last few years, both academia and industry have made some efforts to automate " +
                    "this process." +
            " Some automated techniques and tools for bug finding exist that operate on source code " +
                    "or binary" +
            " code with debugging information, e.g. STACK[1] and SCAN-BUILD[2]. Nevertheless, the focus of " +
            "this paper will be on techniques that operate on the binary file only, stripped of any " +
                    "debugging information." +
            BR_BR_CONSTANT +
            "Techniques that operate on program binaries without debugging information have some " +
            "advantages compared to techniques that need source code or debugging information. " +
            "First, people often do not have access to the sourcecode of the programs they use. " +
                    "Using tools that " +
            "only need binary files, they can still check for vulnerabilities in these programs. " +
                    "When they " +
            "detect vulnerabilities, they can notify the developers and ask for a patch. " +
            "Second,techniques that operate on source code cannot detect all vulnerabilities," +
            " since some vulnerabilities are introduced during compilation. That can happen because " +
                    "there is not " +
            "necessarily a one-to-one mapping between the binary code generated by the compiler or " +
                    "tool chain," +
            " and the source code. This discrepancy between source and binary code can occur because " +
                    "different " +
            "compilers might handle undefined behaviour in different ways, such as in the caseof signed " +
                    "integer " +
            "overflow in C and C++. The compiler does not have to take signed integer overflow into " +
            "account when optimising, and because of this, it might evaluate the conditional " +
            "statement if (x + 1x) as always true, and omit the conditional statement altogether, " +
            "potentially introducing a vulnerability in the code. " +
            "As these kinds of “bugs” are not immediately obvious from the source code, tools that operate on " +
            "binary files are more widely applicable and can discover more bugs than other techniques." +
            BR_BR_CONSTANT +
            "A series of different techniques have emerged to automatically find vulnerabilities in binaries, " +
            "and even automatically craft exploits, to show that the found vulnerabilities are indeed " +
            "exploitable and that they should be fixed assoon as possible. Two large categories exist in " +
            "automatic testing methods" +
            BR_BR_BR_CONSTANT +
            "1) Static testing techniques reason about the program without executing it. " +
            "Almost all static techniques need a control flow graph to operate. " +
            "Some tools like IDA PRO[3],ANGR[4] and JAKSTAB[5]provide this functionality already, " +
            "so they can further use static analysis methods on the resulting control flow graph. " +
            "Static testing techniques include flow modelling, which analyses control flow graphs to " +
            "find certain program properties such as the absence of bugs, and data modelling," +
            " which reasons about the data used by a program to find vulnerabilities. " +
            "Many tools exist to perform these tasks automatically,for example the BINCAT plugin " +
            "for IDA PRO[6], SCANBUILD[2] and JAKSTAB[5]." +
            BR_BR_CONSTANT +
            "These static techniques have some main drawbacks.When one of these techniques " +
            "finds a vulnerability, it does not provide information on how to trigger the bug. " +
            "The vulnerability then has to be verified manually. Also, those techniques typically suffer from" +
            " a high rate of false positives. " +
            "A false positive occurs when a tool finds a vulnerability, but the found " +
            "vulnerability is actually harmless." +
            " This is because the techniques try to reason about the program in a highly abstract domain, " +
            "reducing the semantic insight of the techniques. That is, they do not know why a certain execution " +
            "path is taken." +
            BR_BR_CONSTANT +
            "2) Dynamic techniques examine a program during its execution. " +
            "Two main approaches exist in dynamic testing methods. The first one is symbolic execution which " +
            "was first proposed in 1976 by King [7].A program is executed with a symbolic input and then " +
            "a constraint solver is used to find a satisfying condition for the inputs on the program. " +
            "The second approach is fuzzing. Fuzzing is a simple, yet effect-ive technique for finding bugs " +
            "in code that was first proposed by Milleret al.in 1988 [8]. The main idea is the following the " +
            "fuzzer executes a program with random test inputs and hopes that these inputs will " +
            "trigger software errors." +
            BR_BR_BR_CONSTANT +
            "While both static and dynamic techniques are useful, " +
            "in practice, the industry tends to prefer the dynamic techniques. Most notably, " +
            "fuzzing has become an important software testing technique in industry, with big companies like " +
            "Microsoft and Google using white box fuzzers that use extra information about the " +
            "program to find bugs more effectively. " +
            "One example is SAGE [9]. A notable result is that SAGE found roughly one third of all " +
            "the discovered bugs in Windows 7. Even more remarkable, SAGE typically runs last after all other " +
            "automatic testing tools. " +
            "This means that SAGE found bugs that other techniques,such as static techniques " +
            "and blackbox fuzzing techniques (which do not use any information about the program behaviour), " +
            "were unable to find. Furthermore, Microsoft and Google recently provided ‘Fuzzing as a Service’ " +
            "to application developers with tools such as MSRD [10] and OSS-FUZZ[11]." +
            BR_BR_CONSTANT +
            "In addition, academia seem to " +
            "take a steadily increasing interest in fuzzing as well. According to a survey by Lianget al., " +
            "the number of papers about fuzzing has increased quadratically over the years since 1990 [12]." +
            BR_BR_CONSTANT +
            "Clearly, " +
            "fuzzing has become an indispensable technique to automate software testing. " +
            "In the following sections, " +
            "this paper aims to give a general overview about what fuzzing is, what challenges it " +
            "faces and how these challenges can be mitigated." +
            BR_BR_CONSTANT +
            "In Section II, we explain the basic concept of fuzzing,and how it works in a" +
            " bit more detail. " +
            "Second, Section III discusses some key problems with fuzzing along with how they might " +
            "be solved. One specific way to solve these problems makes use of symbolic execution. " +
            "Section IV explains how symbolic execution can be used in combination with fuzzing, " +
            "and discusses some implementations of this technique. " +
            "Lastly, other approaches that improve fuzzing are shortly reviewed in Section V. This is not" +
            " the main focus, but they are worth mentioning, since they might as well be " +
            "equal counterparts to symbolic execution (or even work in combination with it).\n");
    private static final Section DUMMY_SECTION2 = new Section("Fuzzing", "Fuzzing " +
            "(also called fuzz testing) can be defined as: " +
            "“a highly automated testing technique that covers numerous boundary cases using invalid data " +
            "(from files, network protocols, API calls, and other targets) as application input to better ensure" +
            " the absence of exploitable vulnerabilities” [13]." +
            BR_BR_CONSTANT +
            "Fuzzing on its own is simple: supply some random data at the input, " +
            "and wait until the software crashes. " +
            "The hard (and creative) part is to generate inputs that trigger errors in the program. " +
            BR_BR_CONSTANT +
            "The high-level overview of a fuzzer is displayed in " +
            "Figure 1. " +
            "The dotted box " +
            "encapsulates what is contained within a fuzzer. A fuzzer works as follows [12]: first, " +
            "the user specifies a target program (the program that needs testing) and optionally submits" +
            " a seed file or specification." +
            BR_BR_CONSTANT +
            "Second, " +
            "one of two things happens:" +
            BR_BR_BR_CONSTANT +
            "1) The code passes through the monitor which analyses the input and tries to " +
            "discover information about the structure of the code. " +
            "The monitor might find ways to increase code coverage, for example, by reading trace information. " +
            "Tracing is a form of logging information, which can be" +
            " tracked using code. The information from the monitor is then supplied to the test case generator." +
            BR_BR_CONSTANT +
            "2) The code is passed directly to the test case generator. " +
            "In this case, the monitor might not be present, which may happen if the fuzzer is not intended to ut" +
            "ilise code information. When code information is omitted or not available, " +
            "the fuzzer becomes a blackbox fuzzer. " +
            "The power of blackbox fuzzing lies in the fact that it works fast and is simple to use. " +
            "But there is no such thing as a free meal. " +
            "Blackbox fuzzing has many drawbacks, which will be explained in Section III." +
            BR_BR_BR_CONSTANT +
            "Next, the test case generator generates test cases. " +
            "A test case should be tailored to be valid enough so that it can penetrate into the " +
            "interesting parts of the code,since invalid inputs will be rejected almost immediately by " +
            "the program. " +
            "Still, it must be ‘invalid’ enough to findbugs, since completely valid inputs follow program " +
            "paths that are already tested quite extensively by the application developer or users. " +
            "This can be achieved either using mutations in the input file, or using a grammar:" +
            BR_BR_BR_CONSTANT +
            "1)Mutations of the input file: The t" +
            "est case generator creates test cases by mutating the inputs specified in the seed files. " +
            "If no seed file is specified, the input is chosen at random. In case of blackbox fuzzing, " +
            "these mutations are random changes, such as flipping or reordering bits and bytes in the input. " +
            "In the other case, when a monitor is available, the discovered runtime information can be used" +
            " to determine more relevant mutations, which are able to penetrate deeper into the program’s code." +
            BR_BR_CONSTANT +
            "2) A grammar: This is less obvious: inputs are gener-ated based on a specific set of rules " +
            "or a grammar. One example is the grammar for the C programming language, which describes the" +
            " set of rules everyC programmer has to adhere to when writing a program. " +
            "Other examples include PDF documents,PNG images, MPEG videos, and TCP packets. " +
            "To make sure the input generated by the test casegenerator is able to pass through " +
            "the early parsing stages, it should, for the most part, obey the rules described" +
            " in the relevant grammar." +
            BR_BR_BR_CONSTANT +
            "Next to last is the bug detector. In summary, this element analyses" +
            " every error the program raises or what caused the program to crash. " +
            "Potential exploits are found by using this information." +
            BR_BR_CONSTANT +
            "Lastly, the bug filter filters out actual exploits. " +
            "Automated ways exist to filter the bugs, but currently, there are no state-of-the-art methods. " +
            "That is why this is often done by hand and is in that case not part of the fuzzer.");
    private String mAuthor;
    private String mTitle;
    private String mAbstract;
    private List<Section> mSections;
    private List<Bitmap> mImages;

    public Paper(String author, String title, String paperAbstract, List<Section> sections) {
        this.mAuthor = author;
        this.mTitle = title;
        this.mAbstract = paperAbstract;
        this.mSections = sections;
    }

    public Paper() {
        this.mAuthor = AUTHOR;
        this.mTitle = TITLE;
        this.mAbstract = ABSTRACT_CONTENT;
        List<Section> sections = new ArrayList<>();
        Section sect1 = DUMMY_SECTION1;
        Section sect2 = DUMMY_SECTION2;
        sections.add(sect1);
        sections.add(sect2);
        this.mSections = sections;
        List<Bitmap> images = new ArrayList<>();

        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.fuzzing_scheme));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.hard_to_trigger_code));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.symbolic_execution_code));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.problem_symbolic_execution_code));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.before_symbol_execution_code));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.interaction_scheme));
        images.add(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.under_tainting_code));

        this.mImages = images;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getAbstract() {
        return this.mAbstract;
    }

    public List<Section> getSections() {
        return this.mSections;
    }

    public List<Bitmap> getImages() {
        return this.mImages;
    }

}
