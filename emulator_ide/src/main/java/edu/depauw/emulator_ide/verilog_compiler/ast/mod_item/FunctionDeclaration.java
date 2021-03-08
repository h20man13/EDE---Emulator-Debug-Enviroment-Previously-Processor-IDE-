package edu.depauw.emulator_ide.verilog_compiler.ast.mod_item;

import edu.depauw.emulator_ide.verilog_compiler.ast.statement.Statement;
import edu.depauw.emulator_ide.verilog_compiler.ast.expression.Identifier;
import edu.depauw.emulator_ide.verilog_compiler.ast.general.list.DeclarationList;
import edu.depauw.emulator_ide.verilog_compiler.visitor.AstNodeVisitor;

public class FunctionDeclaration extends ModItem{

    private final Identifier ident;
    private final DeclarationList declList;
    private final Statement stat;
    
    public FunctionDeclaration(Identifier ident, DeclarationList declList, Statement stat){
	super(ident.getPosition());
	this.ident = ident;
	this.declList = declList;
	this.stat = stat;
	
    }

    public Identifier getFunctionName(){
	return ident;
    }

    public int numDeclarations(){
	return declList.size();
    }

    public Declaration getDeclaration(int index){
	return declList.getDeclaration(index);
    }
    
    public DeclarationList getDeclList(){
	return declList;
    }
    
    public Statement getStatement(){
	return stat;
    }

    public <ModVisitType, StatVisitType, ExprVisitType> ModVisitType accept(AstNodeVisitor<ModVisitType, StatVisitType, ExprVisitType> astNodeVisitor){
	return astNodeVisitor.visit(this);
    }
    
}
