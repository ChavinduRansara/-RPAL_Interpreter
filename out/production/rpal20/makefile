# makefile begins

JFLAGS = -g
JC = javac
JVM= java 

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java
		
CLASSES = \
	Interpreter.java \
	LexicalAnalyzer/NameOFToken.java \
	LexicalAnalyzer/Scanner.java \
	LexicalAnalyzer/Token.java \
	LexicalAnalyzer/Regex.java\
        ast/AST.java \
        ast/Node.java \
        ast/NodeType.java \
	ast/ExceptionHandleStandardize.java \
        csemachine/BetaClass.java \
        csemachine/CSEMachine.java \
	csemachine/DeltaClass.java \
	csemachine/Environment.java \
	csemachine/EtaClass.java \
	csemachine/Error.java \
	csemachine/BackupNode.java \
	csemachine/TupleClass.java \
	parser/ExceptionHandleParser.java \
	parser/Parser.java \ 

MAIN = Interpreter

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN)
