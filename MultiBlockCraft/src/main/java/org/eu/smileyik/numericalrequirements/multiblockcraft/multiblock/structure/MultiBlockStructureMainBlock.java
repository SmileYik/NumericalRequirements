package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class MultiBlockStructureMainBlock extends MultiBlockStructure implements StructureMainBlock {
    private List<Node> inputPath;
    private List<Node> outputPath;

    @Override
    public void init() {
        inputPath = findSubclassNode(MultiBlockStructureInput.class);
        outputPath = findSubclassNode(MultiBlockStructureOutput.class);
    }

    @Override
    public boolean isMatch(Block block, BlockFace[] ways) {
        if (!this.isSameBlock(block)) return false;

        final LinkedList<Pair<Block, Structure>> queues = new LinkedList<>();
        queues.add(new Pair<>(block, this));
        while (!queues.isEmpty()) {
            Pair<Block, Structure> pair = queues.removeFirst();
            if (!pair.getSecond().isSameBlock(pair.getFirst())) return false;

            for (Pair<StructureFace, Structure> structurePair : pair.getSecond()) {
                Structure near = structurePair.getSecond();
                block = pair.getFirst().getRelative(ways[structurePair.getFirst().getIndex()]);
                if (!near.isSameBlock(block)) return false;
                queues.add(Pair.newPair(block, near));
            }
        }
        return true;
    }

    @Override
    public Block getBlock(Node node, Block block, BlockFace[] ways) {
        while (node != null) {
            block = block.getRelative(ways[node.face.getIndex()]);
            node = node.next;
        }
        return block;
    }

    @Override
    public List<Node> getInputPath() {
        return inputPath;
    }

    @Override
    public List<Node> getOutputPath() {
        return outputPath;
    }

    protected List<Node> findSubclassNode(Class<?> clazz) {
        final LinkedList<Pair<Node, Structure>> queue = new LinkedList<>();
        final StructureFace[] faces = StructureFace.values();
        for (StructureFace face : faces) {
            Structure near = getNear(face);
            if (near != null) {
                Node node = new Node();
                node.face = face;
                queue.add(Pair.newPair(node, near));
            }
        }

        LinkedList<Node> ans = new LinkedList<>();
        while (!queue.isEmpty()) {
            Pair<Node, Structure> pair = queue.removeFirst();
            if (pair.getSecond().getClass() == clazz) {
                // 反转链表
                Node node = pair.getFirst().copy();
                Node head = null;
                while (node != null) {
                    Node next = node.next;
                    node.next = head;
                    head = node;
                    node = next;
                }
                ans.add(head);
            }

            for (StructureFace face : faces) {
                Structure near = pair.getSecond().getNear(face);
                if (near != null) {
                    Node node = new Node();
                    node.face = face;
                    node.next = pair.getFirst();
                    queue.add(Pair.newPair(node, near));
                }
            }
        }
        return ans;
    }

    public static final class Node {
        StructureFace face;
        Node next;

        Node copy() {
            Node copy = new Node();
            copy.face = this.face;
            if (this.next != null) {
                copy.next = this.next.copy();
            }
            return copy;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            Node n = this;
            while (n != null && (first || n != this)) {
                if (!first) {
                    sb.append(" -> ");
                }
                sb.append(n.face.getName());
                first = false;
                n = n.next;
            }
            return String.format("MultiBlockStructureMainBlock.Node{ %s }", sb);
        }
    }
}
