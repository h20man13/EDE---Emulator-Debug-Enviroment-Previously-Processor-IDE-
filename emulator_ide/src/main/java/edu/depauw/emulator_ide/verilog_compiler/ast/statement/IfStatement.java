package edu.depauw.emulator_ide.verilog_compiler.ast.statement;

import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_compiler.visitor.StatementVisitor;
import edu.depauw.emulator_ide.common.Position;

public class IfStatement extends Statement{

    
    private Expression exp; //expression 
    private final Statement stat; //statement
    
    public IfStatement(Expression exp, Statement stat){
	super(exp.getPosition());
	this.exp = exp;
	this.stat = stat;
    }

    public Expression getExpression(){
	return exp;
    }

    public void setExpression(Expression exp){
	this.exp = exp;
    }

    public Statement getStatement(){
	return stat;
    }

    /** The accept method makes it possible so that nodes know which visitor object to call the visit method from. This is needed because the Visitor method is an interface not a class. All of the classes implementing ASTnode visitor will not have the required dependencies. 
     * @author Jacob Bauer
     */
    public <StatVisitType> StatVisitType accept(StatementVisitor<StatVisitType> statVisitor, Object... argv){
	return statVisitor.visit(this, argv);
    }
}
