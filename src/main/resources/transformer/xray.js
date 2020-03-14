var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var TableSwitchInsnNode = Java
        .type('org.objectweb.asm.tree.TableSwitchInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var SHOULD_SIDE_BE_RENDERED = ASM.mapMethod("func_176225_a"); // shouldSideBeRendered(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z

function log(msg) {
	print("[Xray/Transformer] " + msg);
}

log(SHOULD_SIDE_BE_RENDERED);

function getMethod(classNode, name) {
	for ( var i in classNode.methods) {
		var method = classNode.methods[i];
		if (method.name === name)
			return method;
	}
	throw name + " can't be find";
}

function initializeCoreMod() {
	log("Init Xray coremod");
	return {
		'xray' : {
		    'target' : {
		        'type' : 'CLASS',
		        'name' : 'net.minecraft.block.Block'
		    },
		    'transformer' : function(classNode) {
			    log("Building Xray redirects...");

			    var method = getMethod(classNode, SHOULD_SIDE_BE_RENDERED);

			    var inst = ASM.getMethodNode().instructions;

			    // 0: aload_0 // Lnet/minecraft/block/BlockState
			    // 1: aload_1 // Lnet/minecraft/world/IBlockReader
			    // 2: aload_2 // Lnet/minecraft/util/math/BlockPos
			    // 3: aload_3 // Lnet/minecraft/util/Direction
			    // 4: invokestatic #16 // Method
			    // fr/atesab/xray/XrayMain.shouldSideBeRendered:(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)I
			    // 7: tableswitch { // 0 to 1 // switch (returnvalue) {
			    //
			    // 0: 28 // 0 -> 28
			    //
			    // 1: 30 // 1 -> 30
			    // default: 32 // 2 -> 32
			    // }
			    // 28: iconst_1 // case 0
			    // 29: ireturn // return true
			    // 30: iconst_0 // case 1
			    // 31: ireturn // return false;
			    // 32: iconst_0 // default
			    // 33: ireturn // return false

			    inst.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Lnet/minecraft/block/BlockState
			    inst.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Lnet/minecraft/world/IBlockReader
			    inst.add(new VarInsnNode(Opcodes.ALOAD, 2)); // Lnet/minecraft/util/math/BlockPos
			    inst.add(new VarInsnNode(Opcodes.ALOAD, 3)); // Lnet/minecraft/util/Direction
			    inst.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
			            "fr/atesab/xray/XrayMain", "shouldSideBeRendered", "("
			                    + "Lnet/minecraft/block/BlockState;"
			                    + "Lnet/minecraft/world/IBlockReader;"
			                    + "Lnet/minecraft/util/math/BlockPos;"
			                    + "Lnet/minecraft/util/Direction;" + ")I",
			            false));

			    var case0 = new LabelNode();
			    var case1 = new LabelNode();
			    var casedft = new LabelNode();

			    inst.add(new TableSwitchInsnNode(0, 2, casedft, case0, case1, casedft)); // switch
			    // (returnvalue)
			    inst.add(case0); // case 0
			    inst.add(new InsnNode(Opcodes.ICONST_1));
			    inst.add(new InsnNode(Opcodes.IRETURN)); // return true

			    inst.add(case1); // case 1
			    inst.add(new InsnNode(Opcodes.ICONST_0));
			    inst.add(new InsnNode(Opcodes.IRETURN)); // return false

			    inst.add(casedft); // default

			    log("Adding Xray redirects...");
			    
			    method.instructions.insert(inst);

			    log("Xray redirects added.");

			    return classNode;
		    }
		}
	}
}
