package edu.depauw.emulator_ide.verilog_compiler.circuit_elem.gates;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.CircuitElem;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Register;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.misc_elem.Wire;

import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Primitive;
import edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.Tuple;

import static edu.depauw.emulator_ide.verilog_compiler.circuit_elem.test_utils.TestUtils.*;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FullAdderTest {
    @Test
    public void FullAdderTest(){
    	Register A = new Register(false);
	Register B = new Register(false);
	Register Cin = new Register(false);

	Wire aW = new Wire();
	Wire bW = new Wire();
	Wire CinW = new Wire();

	A.addOutput(aW);
	B.addOutput(bW);
	Cin.addOutput(CinW);

	Wire  iW1 = new Wire();
	Wire  iW2 = new Wire();
	
	Gate Xor1Gate = new XorGate(iW1, aW, bW); //put wires through a basic and gate
	Gate And1Gate = new AndGate(iW2, aW, bW);

	Wire S = new Wire();

	Gate Xor2Gate = new XorGate(S, iW1, CinW);

	Wire  iW3 = new Wire();

	Gate And2Gate = new AndGate(iW3, CinW, iW1);

	Wire Cout = new Wire();

	Gate Or1Gate = new OrGate(Cout, iW3, iW2);
	
	Primitive table = new Primitive(3, 2); //two input one output table
	table.addRow(new Tuple<Boolean>(false, false, false), new Tuple<Boolean>(false, false)); //First Tuple is for inputs and the second is for outputs these represent rows in a boolean logic table
	table.addRow(new Tuple<Boolean>(true, false, false), new Tuple<Boolean>(true, false));
	table.addRow(new Tuple<Boolean>(false, true, false), new Tuple<Boolean>(true, false));
	table.addRow(new Tuple<Boolean>(false, false, true), new Tuple<Boolean>(true, false));
	table.addRow(new Tuple<Boolean>(true, true, false), new Tuple<Boolean>(false, true));
	table.addRow(new Tuple<Boolean>(false, true, true), new Tuple<Boolean>(false, true));
	table.addRow(new Tuple<Boolean>(true, false, true), new Tuple<Boolean>(false, true));
	table.addRow(new Tuple<Boolean>(true, true, true), new Tuple<Boolean>(true, true));

	primitiveVerify(table, new Tuple<Register>(A, B, Cin), new Tuple<CircuitElem>(S, Cout)); //check if the boolean logic table playes out correctly in the circuit created
    }
}
