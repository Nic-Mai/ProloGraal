package ch.heiafr.prolograal.builtins.predicates;

import ch.heiafr.prolograal.ProloGraalTypes;
import ch.heiafr.prolograal.nodes.ProloGraalBuiltinHeadNode;
import ch.heiafr.prolograal.runtime.*;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.NodeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for built-ins.
 * A built-in must inherit from this class and be added to the built-in list to work.
 * The built-in list is handled in the installBuiltins method from {@link ProloGraalRuntime}.
 * @see ProloGraalRuntime
 * @author Martin Spoto
 */
@TypeSystemReference(ProloGraalTypes.class)
@NodeInfo(shortName = "ProloGraalBuiltinClauseNode")
@GenerateWrapper
@NodeChild(value = "head", type = ProloGraalBuiltinHeadNode.class)
public abstract class ProloGraalBuiltinClause extends ProloGraalClause implements InstrumentableNode {

   public ProloGraalBuiltinClause() {
      super(new HashMap<>());
   }

   public ProloGraalBuiltinClause(Map<ProloGraalVariable, ProloGraalVariable> variables) {
      super(variables);
   }

   /**
    * Overrided method from InstrumentableNode, has to return true to make builtins Truffle executable nodes.
    * More informations: https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/instrumentation/InstrumentableNode.html
    * @return true
    */
   public boolean isInstrumentable() {
      return true;
   }

   /**
    * Overrided method from InstrumentableNode, necessary to handle Truffle node execution
    * @param probe
    * @return new Wrapper for the given ProbeNode
    */
   public InstrumentableNode.WrapperNode createWrapper(ProbeNode probe) {
      // ASTNodeWrapper is generated by @GenerateWrapper
      return new ProloGraalBuiltinClauseWrapper(this, probe);
   }

   /**
    * Overridable execute method for built-ins.
    * May have side effects, like writing or opening a file.
    * @param frame necessary param to make this method specializable in the Truffle way.
    * @return The result of the builtin execution.
    */
   public abstract ProloGraalBoolean executeBuiltin(VirtualFrame frame);

   /**
    * We want the built-ins to override this because otherwise they will lose their "ProloGraalBultinClause" status.
    * @return a copy of the built-in
    */
   @Override
   public abstract ProloGraalClause copy();

   /**
    * Copy the current head present in the builtin head node.
    * @return the head present in the builtin head node child
    */
   protected ProloGraalTerm copyHead(){
      List<Node> childrens = NodeUtil.findNodeChildren(this);
      ProloGraalTerm head = ((ProloGraalBuiltinHeadNode)childrens.get(0)).getValue();
      Map<ProloGraalVariable, ProloGraalVariable> newVars = new HashMap<>();
      return head.copy(newVars);
   }
}