package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.gate_declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.mod_item.declaration.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.statement.*;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.*;
import edi.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;
    
public class IndexerVisitor implements AstNodeVisitor<Void, Void, Void>{

    private Environment<String, Position> modEnv;
    private Environment<String, Position> funcEnv;
    private Environment<String, Position> varEnv;
    private ModuleDeclaration mod;

    private Destination dest;
    private InfoLog errorLog;
    
    public IndexerVisitor(ModuleDeclaration mod, Destination dest, InfoLog errorLog){
	this.modEnv = new Environment<>();
	this.funcEnv = new Environment<>();
	this.varEnv = new Environment<>();
	this.dest = dest;
	this.errorLog = errorLog;
	this.mod = mod;
	visit(this.mod);
    }
    /**
     *This is the top level visit statement used to visit a Verilog Module which should allways be the root of the
     *AST
     *@param mod
     *@author Jacob bauer
     */
    private bool param = false;
    public void visit(ModuleDeclaration mod){
	modEnv.addScope();
	funcEnv.addScope();
	Identifier modName = mod.getModuleName();
	if(mod.entryExists(modName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Module Entry " + modName.getLexeme() + " Allready Exists", modName.getPosition())); 
	} else {
	    dest.println("DECL MODULE " + modName.getLexeme() + " at " + modName.getPosition());
	    modEnv.addEntry(modName.getLexeme(), modName.getPosition());
	}
	param = true;
	for(int i = 0; i < mod.numParameters(); i++){
	    mod.getParameter(i).accept(this);
	}
	param = false;
	for(int i = 0; i < mod.numModItems(); i++){
	    mod.getModItem(i).accept(this);
	}
	funcEnv.removeScope();
	modEnv.removeScope();
    }

    /*
     * Below is the code for visiting ModItem constructs
     */

    /**
     * This is the visit statment to visit an Allways Statement.
     * @param stat
     */
    
    public Void visit(AllwaysStatement stat){
	stat.getStatement().accept(this);
    }

    /**
     * This is the code to visit a Continuous Assignment in Verilog.
     * @param assign
     */
    
    public Void visit(ContinuousAssignment assign){
	for(int i = 0; i < assign.numAssignments(); i++){
	    assign.getAssignment().accept(this);
	}
    }

    /**
     * This is the code that is used to visit a function declaration in java
     * @param function
     */
    
    public Void visit(FunctionDeclaration function){
	Identifier funcName = function.getFunctionName();
	if(funcEnv.entryExists(funcName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Function Entry " + funcName.getLexeme() + " Allready Exists", funcName.getPosition())); 
	} else {
	    dest.println("DECL FUNCTION " + funcName.getLexeme() + " at " + funcName.getPosition());
	    funcEnv.addEntry(funcName.getLexeme(), funcName.getPosition());
	}
	varEnv.addScope();
	for(int i = 0; i < function.numDeclarations(); i++){
	    function.getDeclaration(i).accept(this);
	}
	function.getStatement().accept(this);
	varEnv.removeScope();
    }

    /**
     * This is the code to visit a Initial Statement in Verilog
     * @param stat
     */
    
    public Void visit(InitialStatement stat){
	varEnv.addScope();
	stat.getStatement().accept(this);
	varEnv.removeScope();
    }

    /**
     * This is the code to visit a Module call or Instantiation in verilog
     * @param mod
     */
    
    public Void visit(ModInstantiation mod){
	for(int i = 0; i < mod.numModInstances(); i++){
	    mod.getModInstance(i).accept(this);
	}
    }

    /**
     * This is the code to visit a Module instance in Verilog
     * @param mod
     */
    
    public Void visit(ModInstance mod){
	Identifier modName = mod.getIdentifier();
	if(modEnv.entryExists(modName.getLexeme())){
	    dest.println("USE MODULE " + modName.getLexeme() + " DECLARED AT " + modEnv.getEntry(modName.getLexeme()));
	} else {
	    errorLog.addItem(new ErrorItem("Identifier " + modName.getLexeme() + " not found", modName.getPosition()));
	}
	for(int i = 0; i < mod.numExpressions(); i++){
	    mod.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit a task declaration in verilog
     * @param task
     */
    
    public Void visit(TaskDeclaration task){
	Identifier taskName = function.getTaskName();
	if(funcEnv.entryExists(taskName.getLexeme())){
	    errorLog.addItem(new ErrorItem("Task Entry " + funcName.getLexeme() + " Allready Exists", taskName.getPosition())); 
	} else {
	    dest.println("DECL TASK " + taskName.getLexeme() + " AT " + taskName.getPosition());
	    funcEnv.addEntry(taskName.getLexeme(), taskName.getPosition());
	}
	varEnv.addScope();
	for(int i = 0; i < function.numDeclarations(); i++){
	    function.getDeclaration(i).accept(this);
	}
	function.getStatement().accept(this);
	varEnv.removeScope();
    }

    /**
     * This is used to visit any input scalar declaration in verilog.
     * Ex. input a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL INPUT " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any input vector declaration in verilog.
     * Ex. input [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(InputVectorDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL INPUT " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any wire scalar wire declaration in verilog.
     * Ex. wire a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any wire vector declaration in verilog.
     * Ex. wire [31:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(WireVectorDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL WIRE " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegScalarDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    RegValue currrent = decl.getRegValue(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL REG " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any reg scalar declaration in verilog.
     * Ex. reg [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RegVectorDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    RegValue currrent = decl.getRegValue(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL REG " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }


    /**
     * This is used to visit any output scalar declaration in verilog.
     * Ex. output a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputScalarDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL OUTPUT " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any output vector declaration in verilog.
     * Ex. output [2:0] a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OutputVectorDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL OUTPUT " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any integer declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(IntegerDeclaration decl){
	for(int i = 0; i < decl.numRegValues(); i++){
	    RegValue currrent = decl.getRegValue(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL INTEGER " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any real declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(RealDeclaration decl){
	for(int i = 0; i < decl.numIdentifiers(); i++){
	    Identifier currrent = decl.getIdentifier(i);
	    if(varEnv.entryExists(current.getLexeme())){
		errorLog.addItem(new ErrorItem("Variable Entry " + current.getLexeme() + " Allready Exists", current.getPosition())); 
	    } else {
		dest.println("DECL REAL " + current.getLexeme() + " AT " + current.getPosition());
		funcEnv.addEntry(current.getLexeme(), current.getPosition());
	    }
	}
    }

    /**
     * This is used to visit any andgate declaration in verilog.
     * Ex. integer a, b, c ... ;
     * @param decl
     */
    
    public Void visit(AndGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit any orgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(OrGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

     /**
     * This is used to visit any nandgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NandGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit any norgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NorGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit any xorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XorGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit any xnorgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(XnorGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit any notgate declaration in verilog.
     * Ex. real a, b, c ... ;
     * @param decl
     */
    
    public Void visit(NotGateDeclaration decl){
	for(int i = 0; i < decl.numExpressions(); i++){
	    decl.getExpression(i).accept(this);
	}
    }





    
    /*
     * Below is the code for viewing statements in the verilog language
     */

    /**
     * This is used to visit assignments in verilog
     * @param assign
     */
    
    public Void visit(Assignment assign){
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
    }

    /**
     * This is used to visit blocking assignments in verilog
     * @param assign
     */
    
    public Void visit(BlockAssign assign){
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
    }

    /**
     * This is used to visit case statements in verilog
     * @param assign
     */
    
    public Void visit(CaseStatement stat){
	stat.getExpression.accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExpCaseItem){
		for(int i = 0; i < stat.numExpressions(); i++){
		    stat.getExpression(i).accept(this);
		}
	    }
	    item.getStatement().accept(this);
	}
    }

    /**
     * This is used to visit casex statements in verilog
     * @param assign
     */
    
    public Void visit(CaseXStatement stat){
	stat.getExpression.accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExpCaseItem){
		for(int i = 0; i < stat.numExpressions(); i++){
		    stat.getExpression(i).accept(this);
		}
	    }
	    item.getStatement().accept(this);
	}
    }

    /**
     * This is used to visit casez statements in verilog
     * @param assign
     */
    
    public Void visit(CaseZStatement stat){
	stat.getExpression.accept(this);
	for(int i = 0; i < stat.numCaseItems(); i++){
	    CaseItem item = stat.getCaseItem(i);
	    if(item instanceof ExpCaseItem){
		for(int i = 0; i < stat.numExpressions(); i++){
		    stat.getExpression(i).accept(this);
		}
	    }
	    item.getStatement().accept(this);
	}
    }

    /**
     * This is used to visit a for loop in verilog
     * @param forLoop
     */
    
    public Void visit(ForStatement forLoop){
	forLoop.getInit.accept(this);
	forLoop.getExpression.accept(this);
	forLoop.getChange.accept(this);
	forLoop.getStatement.accept(this);
    }

    /**
     * This is used to visit a forever loop in verilog
     * @param foreverLoop
     */
    
    public Void visit(ForeverStatement foreverLoop){
	foreverLoop.getStatement().accept(this);
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfElseStatement ifElseStatement){
	ifElseStatement.getExpression().accept(this);
	ifElseStatement.getIfStatement().accept(this);
	ifElseStatement.getElseStatement().accept(this);
    }

    /**
     * This is used to visit a if else statement in verilog
     * @param ifElseStatement
     */
    
    public Void visit(IfStatement ifStatement){
	ifStatement.getExpression().accept(this);
	ifStatement.getStatement().accept(this);
    }

    /**
     * This is used to visit a non blocking assignment statement in verilog
     * @param assign
     */
    
    public Void visit(NonBlockAssign assign){
	assign.getLValue().accept(this);
	assign.getExpression().accept(this);
    }

    /**
     * This is used to visit a repeat statement in verilog
     * @param stat
     */
    
    public Void visit(RepeatStatement stat){
	stat.getExpression().accept(this);
	stat.getStatement().accept(this);
    }

    /**
     * This is used to visit a seq block in verilog
     * @param stat
     */
     
    public Void visit(SeqBlockStatement stat){
	for(int i = 0; i < stat.numStatements(); i++){
	    stat.getStatement(i).accept(this);
	}
    }

    /**
     * This is used to visit a taskcall in verilog
     * @param stat
     */
     
    public Void visit(TaskStatement task){
	Identifier tname = task.getTaskName();
	
	if(funcEnv.entryExists(tname.getLexeme())){
	    dest.println("USE FUNCTION " + tname.getLexeme() + " AT " + tname.getPosition() + " DEFINED AT " + funcEnv.getEntry(tname.getLexeme()));
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + tname.getLexeme() + " Doesnt Exist", tname.getPosition())); 
	}

	for(int i = 0; i < task.numExpressions(); i++){
	    task.getExpression(i).accept(this);
	}
    }

    /**
     * This is used to visit a wait statement in verilog
     * @param stat
     */
     
    public Void visit(WaitStatement wait){
	wait.getExpression().accept(this);
	wait.getStatement().accept(this);
    }

    /**
     * This is used to visit a while loop in verilog
     * @param whileLoop
     */
     
    public Void visit(WhileStatement whileLoop){
	whileLoop.getExpression.accept(this);
	whileLoop.getStatement.accept(this);
    }


    /**
     * This is the code for visiting empty statements this is here just for completion
     * @param none
     */
    
    public Void visit(EmptyStatement stat){
	//this is empty it is just a placeholder
    }

    
    /*
     * Below is the code that is used for visiting Expressions
     */


    /**
     * This is the code for visiting binary operations
     * @param op
     */
    
    public Void visit(BinaryOperation op){
	op.getLeft().accept(this);
	op.getRight().accept(this);
    }

    /**
     * This is the code for visiting unary operations
     * @param op
     */
    
    public Void visit(UnaryOperation op){
	op.getRight().accept(this);
    }


    /**
     * This is the code for visiting concatenations
     * @param concat
     */
    
    public Void visit(Concatenation concat){
	for(int i = 0; i < concat.numExpressions(); i++){
	    concat.getExpression(i).accept(this);
	}
    }

    /**
     * This is the code for visiting Constant Expressions
     * @param expr
     */
    
    public Void visit(ConstantExpression expr){
	expr.getExpression().accept(this);
    }

    /**
     * This is the code for visiting Empty Expressions
     * @param expr
     */
    
    public Void visit(EmptyExpression  expr){
	//this is just a placeholder we do not need to put anything here
    }

    /**
     * This is the code for visiting Function Calls
     * @param call
     */
    
    public Void visit(FunctionCall call){
	Identifier fname = call.getFunctionName();
	
	if(funcEnv.entryExists(fname.getLexeme())){
	    dest.println("USE FUNCTION " + fname.getLexeme() + " AT " + fname.getPosition() + " DEFINED AT " + funcEnv.getEntry(fname.getLexeme()));
	} else {
	    errorLog.addItem(new ErrorItem("Function Entry " + fname.getLexeme() + " Doesnt Exist", fname.getPosition())); 
	}

	for(int i = 0; i < call.numExpressions(); i++){
	    call.getExpression(i).accept(this);
	}
	
    }

    /**
     * This is the code for visiting an Identifier
     * @param ident
     */
    
    public Void visit(Identifier ident){
	if(param){
	    if(varEnv.entryExists(ident.getLexeme())){
		dest.println("USE PARAM " + ident.getLexeme() + " AT " + ident.getPosition() + " DEFINED AT " + varEnv.getEntry(ident.getLexeme()));
	    } else {
		errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	    }
	} else {
	    if(varEnv.entryExists(ident.getLexeme())){
		dest.println("USE VARIABLE " + ident.getLexeme() + " AT " + ident.getPosition() + " DEFINED AT " + varEnv.getEntry(ident.getLexeme()));
	    } else {
		errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	    }
	}
    }

    /**
     * This is the code for visiting an Number in verilog
     * @param number
     */
    
    public Void visit(NumValue number){
	// do nothing
    }

    /**
     * This is the code for visiting a port connection in verilog
     * @param connection
     */
    
    public Void visit(PortConnection connection){
	connection.getExpression().accept(this);
    }

    /**
     * This is the code for visiting a string in verilog
     * @param string
     */
    
    public Void visit(StrValue string){
	// do nothing
    }

    /**
     * This is the code for visiting a TernaryOperation in verilog
     * @param expr
     */
    
    public Void visit(TernaryExpression expr){
	expr.getCondition().accept(this);
	expr.getLeft().accept(this);
	expr.getRight().accept(this);
    }

    /**
     * This is the code for visiting a Vector in verilog
     * @param string
     */
    
    public Void visit(VectorCall vector){
	Identifier ident = vector.getIdentifier();
	if(param){
	    if(varEnv.entryExists(ident.getLexeme())){
		dest.println("USE PARAM " + ident.getLexeme() + " AT " + ident.getPosition() + " DEFINED AT " + varEnv.getEntry(ident.getLexeme()));
	    } else {
		errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	    }
	} else {
	    if(varEnv.entryExists(ident.getLexeme())){
		dest.println("USE VECTOR " + ident.getLexeme() + " AT " + ident.getPosition() + " DEFINED AT " + varEnv.getEntry(ident.getLexeme()));
	    } else {
		errorLog.addItem(new ErrorItem("Variable Entry " + ident.getLexeme() + " Doesnt Exist", ident.getPosition())); 
	    }
	}

	vector.getExpression1().accept(this);
	vector.getExpression2().accept(this);
    }
}
