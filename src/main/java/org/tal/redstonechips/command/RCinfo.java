
package org.tal.redstonechips.command;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.bitset.BitSetUtils;
import org.tal.redstonechips.util.ChunkLocation;

/**
 *
 * @author Tal Eisenberg
 */
public class RCinfo extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandUtils.checkPermission(rc, sender, command.getName(), false, true)) return true;

        Circuit c;
        if (args.length==0) { // use target circuit
            c = CommandUtils.findTargetCircuit(rc, sender);
            if (c==null) return true;
        } else {
            
            c = rc.getCircuitManager().getCircuitById(args[0]);
            if (c==null) {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "There's no activated chip with id " + args[0]);
                return true;
            }

        }

        printCircuitInfo(sender, c, rc);

        return true;
    }

    public static void printCircuitInfo(CommandSender sender, Circuit c, org.tal.redstonechips.RedstoneChips rc) {
        ChatColor infoColor = rc.getPrefs().getInfoColor();
        ChatColor extraColor = ChatColor.YELLOW;

        String disabled;
        if (c.isDisabled()) disabled = ChatColor.GRAY + " - disabled";
        else disabled = "";

        String loc = c.activationBlock.getBlockX() + ", " + c.activationBlock.getBlockY() + ", " + c.activationBlock.getBlockZ();
        String chunkCoords = "";
        for (ChunkLocation l : c.circuitChunks)
            chunkCoords += (l.isChunkLoaded()?extraColor:ChatColor.WHITE) + "[" + l.getX() + ", " + l.getZ() + " " + (l.isChunkLoaded()?"L":"u") + "]" + infoColor + ", ";

        if (!chunkCoords.isEmpty()) chunkCoords = chunkCoords.substring(0, chunkCoords.length()-2);
        
        String name;
        if (c.name==null) name = "unnamed";
        else name = c.name;
        
        sender.sendMessage("");
        sender.sendMessage(infoColor + c.getChipString() + disabled);
        sender.sendMessage(extraColor + "----------------------------------------");

        sender.sendMessage(infoColor + "" + c.inputs.length + " input(s), " + c.outputs.length + " output(s) and " + c.interfaceBlocks.length + " interface blocks.");
        sender.sendMessage(infoColor +
                "location: " + extraColor + loc + infoColor + " @ " + extraColor + c.world.getName());
        sender.sendMessage(infoColor + " chunks: " + chunkCoords);

        int inputInt = BitSetUtils.bitSetToUnsignedInt(c.getInputBits(), 0, c.inputs.length);
        int outputInt = BitSetUtils.bitSetToUnsignedInt(c.getOutputBits(), 0, c.outputs.length);

        if (c.inputs.length>0)
            sender.sendMessage(infoColor + "input states: " + extraColor + BitSetUtils.bitSetToBinaryString(c.getInputBits(), 0, c.inputs.length)
                    + infoColor + " (0x" + Integer.toHexString(inputInt) + ")");

        if (c.outputs.length>0)
            sender.sendMessage(infoColor + "output states: " + extraColor + BitSetUtils.bitSetToBinaryString(c.getOutputBits(), 0, c.outputs.length)
                    + infoColor + " (0x" + Integer.toHexString(outputInt) + ")");

        String signargs = "";
        for (String arg : c.args)
            signargs += arg + " ";

        sender.sendMessage(infoColor + "sign args: " + extraColor + signargs);

        Map<String,String> internalState = c.getInternalState();
        if (!internalState.isEmpty()) {
            sender.sendMessage(infoColor + "internal state:");
            String internal = "   ";
            for (String key : internalState.keySet())
                internal += infoColor + key + ": " + extraColor + internalState.get(key) + infoColor + ", ";
            
            sender.sendMessage(internal.substring(0, internal.length()-2));
        }
    }
}
