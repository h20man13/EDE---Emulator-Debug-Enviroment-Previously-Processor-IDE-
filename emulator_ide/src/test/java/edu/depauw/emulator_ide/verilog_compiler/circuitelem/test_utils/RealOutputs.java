import edu.depauw.emulator_ide.verilog_compiler.circuitelem.CircuitElem;

import java.util.ArrayList;
import java.lang.Boolean;

public class RealOutputs{
    private ArrayList<CircuitElem> outputs = new ArrayList();

    public RealInputs(CircuitElem... outputs){
	for(CircuitElem output : outputs){
	    this.outputs.add(output);
	}
    }

    public ArrayList<CircuitElem> getList(){
	return this.outputs;
    }

    public int size(){
	return outptus.size();
    }
}
