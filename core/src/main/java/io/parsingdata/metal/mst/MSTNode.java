package io.parsingdata.metal.mst;

import java.util.List;

import io.parsingdata.metal.mst.visitor.Visitor;
import io.parsingdata.metal.mst.visitor.VoidVisitor;

public interface MSTNode {

    List<MSTNode> children();

    <T> T accept(Visitor<T> visitor);

    void accept(VoidVisitor visitor);
}
