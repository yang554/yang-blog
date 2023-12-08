<template>
    <div class="panel-body" ref="myDiagramDiv"
        style="background-color: rgb(248, 248, 248); border: 1px solid #9b9b9b; -webkit-tap-highlight-color: rgba(255, 255, 255, 0); cursor: auto;height: 100%;">

    </div>
</template>

<script>
// export default {
//     name: 'test',
//     data() {
//         return {
//             myDiagramRefs: null, //这个是为了将初始化的结构保存起来，可以调用go.js的方法
//         };
//     },
//     created() {
//         //初始化画布
//         this.init();
//     },
//     methods: {
//         init() {
//             const gojs = this.go.GraphObject.make; // 为了定义模板的简洁性
//             let myDiagram = gojs(go.Diagram, this.$refs.myDiagramDiv,
//                 {
//                     // initialAutoScale: go.Diagram.Uniform,
//                     "undoManager.isEnabled": false,
//                     // layout: gojs(GenogramLayout, { direction: 90, layerSpacing: 35 }),
//                     // allowZoom: true
//                 });
//         },
//         //字体样式
//         textStyle() {
//             return { font: "9pt  Segoe UI,sans-serif", stroke: "white" };
//         }
//     }
// }

// A custom layout that shows the two families related to a person's parents
class GenogramLayout extends go.LayeredDigraphLayout {
    constructor() {
        super();
        this.alignOption = go.LayeredDigraphLayout.AlignAll;
        this.initializeOption = go.LayeredDigraphLayout.InitDepthFirstIn;
        this.spouseSpacing = 30;  // 配偶之间的最小空间
        this.isRouting = false;
    }

    makeNetwork(coll) {
        // 为每个父子链接生成LayoutEdges
        const net = this.createNetwork();
        if (coll instanceof go.Diagram) {
            this.add(net, coll.nodes, true);
            this.add(net, coll.links, true);
        } else if (coll instanceof go.Group) {
            this.add(net, coll.memberParts, false);
        } else if (coll.iterator) {
            this.add(net, coll.iterator, false);
        }
        return net;
    }

    // 用于创建LayeredDigraphNetwork的内部方法，其中表示丈夫/妻子对
    // 通过与婚姻链接上的标签“节点”对应的单个LayeredDigraphVertex
    add(net, coll, nonmemberonly) {
        const horiz = this.direction == 0.0 || this.direction == 180.0;
        const multiSpousePeople = new go.Set();
        // consider all Nodes in the given collection
        const it = coll.iterator;
        while (it.next()) {
            const node = it.value;
            if (!(node instanceof go.Node) || !node.data) continue;
            if (!node.isLayoutPositioned || !node.isVisible()) continue;
            if (nonmemberonly && node.containingGroup !== null) continue;
            // if it's an unmarried Node, or if it's a Link Label Node, create a LayoutVertex for it
            if (node.isLinkLabel) {
                // get marriage Link
                const link = node.labeledLink;
                if (link.category === "Marriage") {
                    const spouseA = link.fromNode;
                    const spouseB = link.toNode;
                    // create vertex representing both husband and wife
                    const vertex = net.addNode(node);
                    // now define the vertex size to be big enough to hold both spouses
                    if (horiz) {
                        vertex.height = spouseA.actualBounds.height + this.spouseSpacing + spouseB.actualBounds.height;
                        vertex.width = Math.max(spouseA.actualBounds.width, spouseB.actualBounds.width);
                        vertex.focus = new go.Point(vertex.width / 2, spouseA.actualBounds.height + this.spouseSpacing / 2);
                    } else {
                        vertex.width = spouseA.actualBounds.width + this.spouseSpacing + spouseB.actualBounds.width;
                        vertex.height = Math.max(spouseA.actualBounds.height, spouseB.actualBounds.height);
                        vertex.focus = new go.Point(spouseA.actualBounds.width + this.spouseSpacing / 2, vertex.height / 2);
                    }
                }
            } else {
                // don't add a vertex for any married person!
                // instead, code above adds label node for marriage link
                // assume a marriage Link has a label Node
                let marriages = 0;
                node.linksConnected.each(l => {
                    if (l.category === "Marriage") marriages++;
                });
                if (marriages === 0) {
                    net.addNode(node);
                } else if (marriages > 1) {
                    multiSpousePeople.add(node);
                }
            }
        }
        // now do all Links
        it.reset();
        while (it.next()) {
            const link = it.value;
            if (!(link instanceof go.Link)) continue;
            if (!link.isLayoutPositioned || !link.isVisible()) continue;
            if (nonmemberonly && link.containingGroup !== null) continue;
            // if it's a parent-child link, add a LayoutEdge for it
            if (link.category === "" && link.data) {
                const parent = net.findVertex(link.fromNode);  // should be a label node
                const child = net.findVertex(link.toNode);
                if (child !== null) {  // an unmarried child
                    net.linkVertexes(parent, child, link);
                } else {  // a married child
                    link.toNode.linksConnected.each(l => {
                        if (l.category !== "Marriage" || !l.data) return;  // if it has no label node, it's a parent-child link
                        // found the Marriage Link, now get its label Node
                        const mlab = l.labelNodes.first();
                        // parent-child link should connect with the label node,
                        // so the LayoutEdge should connect with the LayoutVertex representing the label node
                        const mlabvert = net.findVertex(mlab);
                        if (mlabvert !== null) {
                            net.linkVertexes(parent, mlabvert, link);
                        }
                    });
                }
            }
        }

        while (multiSpousePeople.count > 0) {
            // find all collections of people that are indirectly married to each other
            const node = multiSpousePeople.first();
            const cohort = new go.Set();
            this.extendCohort(cohort, node);
            // then encourage them all to be the same generation by connecting them all with a common vertex
            const dummyvert = net.createVertex();
            net.addVertex(dummyvert);
            const marriages = new go.Set();
            cohort.each(n => {
                n.linksConnected.each(l => {
                    marriages.add(l);
                })
            });
            marriages.each(link => {
                // find the vertex for the marriage link (i.e. for the label node)
                const mlab = link.labelNodes.first()
                const v = net.findVertex(mlab);
                if (v !== null) {
                    net.linkVertexes(dummyvert, v, null);
                }
            });
            // done with these people, now see if there are any other multiple-married people
            multiSpousePeople.removeAll(cohort);
        }
    }

    // 收集所有与某人间接结婚的人
    extendCohort(coll, node) {
        if (coll.has(node)) return;
        coll.add(node);
        node.linksConnected.each(l => {
            if (l.category === "Marriage") {  // if it's a marriage link, continue with both spouses
                this.extendCohort(coll, l.fromNode);
                this.extendCohort(coll, l.toNode);
            }
        });
    }

    assignLayers() {
        super.assignLayers();
        const horiz = this.direction == 0.0 || this.direction == 180.0;
        // for every vertex, record the maximum vertex width or height for the vertex's layer
        const maxsizes = [];
        this.network.vertexes.each(v => {
            const lay = v.layer;
            let max = maxsizes[lay];
            if (max === undefined) max = 0;
            const sz = (horiz ? v.width : v.height);
            if (sz > max) maxsizes[lay] = sz;
        });
        // now make sure every vertex has the maximum width or height according to which layer it is in,
        // and aligned on the left (if horizontal) or the top (if vertical)
        this.network.vertexes.each(v => {
            const lay = v.layer;
            const max = maxsizes[lay];
            if (horiz) {
                v.focus = new go.Point(0, v.height / 2);
                v.width = max;
            } else {
                v.focus = new go.Point(v.width / 2, 0);
                v.height = max;
            }
        });
        // from now on, the LayeredDigraphLayout will think that the Node is bigger than it really is
        // (other than the ones that are the widest or tallest in their respective layer).
    }

    initializeIndices() {
        super.initializeIndices();
        const vertical = this.direction === 90 || this.direction === 270;
        this.network.edges.each(e => {
            if (e.fromVertex.node && e.fromVertex.node.isLinkLabel) {
                e.portFromPos = vertical ? e.fromVertex.focusX : e.fromVertex.focusY;
            }
            if (e.toVertex.node && e.toVertex.node.isLinkLabel) {
                e.portToPos = vertical ? e.toVertex.focusX : e.toVertex.focusY;
            }
        })
    }

    commitNodes() {
        super.commitNodes();
        // position regular nodes
        this.network.vertexes.each(v => {
            if (v.node !== null && !v.node.isLinkLabel) {
                v.node.position = new go.Point(v.x, v.y);
            }
        });

        const horiz = this.direction == 0.0 || this.direction == 180.0;
        // position the spouses of each marriage vertex
        this.network.vertexes.each(v => {
            if (v.node === null) return;
            if (!v.node.isLinkLabel) return;
            const labnode = v.node;
            const lablink = labnode.labeledLink;
            // In case the spouses are not actually moved, we need to have the marriage link
            // position the label node, because LayoutVertex.commit() was called above on these vertexes.
            // Alternatively we could override LayoutVetex.commit to be a no-op for label node vertexes.
            lablink.invalidateRoute();
            let spouseA = lablink.fromNode;
            let spouseB = lablink.toNode;
            if (spouseA.opacity > 0 && spouseB.opacity > 0) {
                // prefer fathers on the left, mothers on the right
                if (spouseA.category === "F") {  // sex is female
                    const temp = spouseA;
                    spouseA = spouseB;
                    spouseB = temp;
                }
                // see if the parents are on the desired sides, to avoid a link crossing
                const aParentsNode = this.findParentsMarriageLabelNode(spouseA);
                const bParentsNode = this.findParentsMarriageLabelNode(spouseB);
                if (aParentsNode !== null && bParentsNode !== null &&
                    (horiz
                        ? aParentsNode.position.x > bParentsNode.position.x
                        : aParentsNode.position.y > bParentsNode.position.y)) {
                    // swap the spouses
                    const temp = spouseA;
                    spouseA = spouseB;
                    spouseB = temp;
                }
                spouseA.moveTo(v.x, v.y);
                if (horiz) {
                    spouseB.moveTo(v.x, v.y + spouseA.actualBounds.height + this.spouseSpacing);
                } else {
                    spouseB.moveTo(v.x + spouseA.actualBounds.width + this.spouseSpacing, v.y);
                }
            } else if (spouseA.opacity === 0) {
                const pos = horiz
                    ? new go.Point(v.x, v.centerY - spouseB.actualBounds.height / 2)
                    : new go.Point(v.centerX - spouseB.actualBounds.width / 2, v.y);
                spouseB.move(pos);
                if (horiz) pos.y++; else pos.x++;
                spouseA.move(pos);
            } else if (spouseB.opacity === 0) {
                const pos = horiz
                    ? new go.Point(v.x, v.centerY - spouseA.actualBounds.height / 2)
                    : new go.Point(v.centerX - spouseA.actualBounds.width / 2, v.y);
                spouseA.move(pos);
                if (horiz) pos.y++; else pos.x++;
                spouseB.move(pos);
            }
            lablink.ensureBounds();
        });
    }

    findParentsMarriageLabelNode(node) {
        const it = node.findNodesInto();
        while (it.next()) {
            const n = it.value;
            if (n.isLinkLabel) return n;
        }
        return null;
    }
} // end GenogramLayout class


function init() {

    // 从2.2开始，您还可以使用方法链接而不是GraphObject.make编写简洁的模板
    // 有关详细信息，请参阅https://gojs.net/latest/intro/buildingobjects.html
    const $ = go.GraphObject.make;

    myDiagram =
        new go.Diagram(this.$refs.myDiagramDiv,//this.$refs.myDiagramDiv
            {
                "animationManager.isEnabled": false,
                initialAutoScale: go.Diagram.Uniform,
                "undoManager.isEnabled": true,
                maxSelectionCount: 1,
                // when a node is selected, draw a big yellow circle behind it
                nodeSelectionAdornmentTemplate:
                    $(go.Adornment, "Auto",
                        { layerName: "Grid" },  // the predefined layer that is behind everything else
                        $(go.Shape, "Circle", { fill: "#c1cee3", stroke: null }),
                        $(go.Placeholder, { margin: 2 })
                    ),
                layout:  // use a custom layout, defined above
                    $(GenogramLayout, { direction: 90, layerSpacing: 30, columnSpacing: 10 })
            });

    // determine the color for each attribute shape
    function attrFill(a) {
        switch (a) {
            case "A": return "#00af54"; // green
            case "B": return "#f27935"; // orange
            case "C": return "#d4071c"; // red
            case "D": return "#70bdc2"; // cyan
            case "E": return "#fcf384"; // gold
            case "F": return "#e69aaf"; // pink
            case "G": return "#08488f"; // blue
            case "H": return "#866310"; // brown
            case "I": return "#9270c2"; // purple
            case "J": return "#a3cf62"; // chartreuse
            case "K": return "#91a4c2"; // lightgray bluish
            case "L": return "#af70c2"; // magenta
            case "S": return "#d4071c"; // red
            default: return "transparent";
        }
    }

    // determine the geometry for each attribute shape in a male;
    // except for the slash these are all squares at each of the four corners of the overall square
    const tlsq = go.Geometry.parse("F M1 1 l19 0 0 19 -19 0z");
    const trsq = go.Geometry.parse("F M20 1 l19 0 0 19 -19 0z");
    const brsq = go.Geometry.parse("F M20 20 l19 0 0 19 -19 0z");
    const blsq = go.Geometry.parse("F M1 20 l19 0 0 19 -19 0z");
    const slash = go.Geometry.parse("F M38 0 L40 0 40 2 2 40 0 40 0 38z");
    function maleGeometry(a) {
        switch (a) {
            case "A": return tlsq;
            case "B": return tlsq;
            case "C": return tlsq;
            case "D": return trsq;
            case "E": return trsq;
            case "F": return trsq;
            case "G": return brsq;
            case "H": return brsq;
            case "I": return brsq;
            case "J": return blsq;
            case "K": return blsq;
            case "L": return blsq;
            case "S": return slash;
            default: return tlsq;
        }
    }

    // determine the geometry for each attribute shape in a female;
    // except for the slash these are all pie shapes at each of the four quadrants of the overall circle
    const tlarc = go.Geometry.parse("F M20 20 B 180 90 20 20 19 19 z");
    const trarc = go.Geometry.parse("F M20 20 B 270 90 20 20 19 19 z");
    const brarc = go.Geometry.parse("F M20 20 B 0 90 20 20 19 19 z");
    const blarc = go.Geometry.parse("F M20 20 B 90 90 20 20 19 19 z");
    function femaleGeometry(a) {
        switch (a) {
            case "A": return tlarc;
            case "B": return tlarc;
            case "C": return tlarc;
            case "D": return trarc;
            case "E": return trarc;
            case "F": return trarc;
            case "G": return brarc;
            case "H": return brarc;
            case "I": return brarc;
            case "J": return blarc;
            case "K": return blarc;
            case "L": return blarc;
            case "S": return slash;
            default: return tlarc;
        }
    }


    // two different node templates, one for each sex,
    // named by the category value in the node data object
    myDiagram.nodeTemplateMap.add("M",  // male
        $(go.Node, "Vertical",
            {
                locationSpot: go.Spot.Center, locationObjectName: "ICON",
                selectionObjectName: "ICON"
            },
            new go.Binding("opacity", "hide", h => h ? 0 : 1),
            new go.Binding("pickable", "hide", h => !h),
            $(go.Panel,
                { name: "ICON" },
                $(go.Shape, "Square",
                    { width: 40, height: 40, strokeWidth: 2, fill: "white", stroke: "#919191", portId: "" }),
                $(go.Panel,
                    { // for each attribute show a Shape at a particular place in the overall square
                        itemTemplate:
                            $(go.Panel,
                                $(go.Shape,
                                    { stroke: null, strokeWidth: 0 },
                                    new go.Binding("fill", "", attrFill),
                                    new go.Binding("geometry", "", maleGeometry))
                            ),
                        margin: 1
                    },
                    new go.Binding("itemArray", "a")
                )
            ),
            $(go.TextBlock,
                { textAlign: "center", maxSize: new go.Size(80, NaN), background: "rgba(255,255,255,0.5)" },
                new go.Binding("text", "n"))
        ));

    myDiagram.nodeTemplateMap.add("F",  // female
        $(go.Node, "Vertical",
            {
                locationSpot: go.Spot.Center, locationObjectName: "ICON",
                selectionObjectName: "ICON"
            },
            new go.Binding("opacity", "hide", h => h ? 0 : 1),
            new go.Binding("pickable", "hide", h => !h),
            $(go.Panel,
                { name: "ICON" },
                $(go.Shape, "Circle",
                    { width: 40, height: 40, strokeWidth: 2, fill: "white", stroke: "#a1a1a1", portId: "" }),
                $(go.Panel,
                    { // for each attribute show a Shape at a particular place in the overall circle
                        itemTemplate:
                            $(go.Panel,
                                $(go.Shape,
                                    { stroke: null, strokeWidth: 0 },
                                    new go.Binding("fill", "", attrFill),
                                    new go.Binding("geometry", "", femaleGeometry))
                            ),
                        margin: 1
                    },
                    new go.Binding("itemArray", "a")
                )
            ),
            $(go.TextBlock,
                { textAlign: "center", maxSize: new go.Size(80, NaN), background: "rgba(255,255,255,0.5)" },
                new go.Binding("text", "n"))
        ));

    // the representation of each label node -- nothing shows on a Marriage Link
    myDiagram.nodeTemplateMap.add("LinkLabel",
        $(go.Node,
            { selectable: false, width: 1, height: 1, fromEndSegmentLength: 20 }));

    myDiagram.linkTemplate =  // for parent-child relationships
        $(go.Link,
            {
                routing: go.Link.Orthogonal, corner: 10, curviness: 15,
                layerName: "Background", selectable: false
            },
            $(go.Shape, { stroke: "gray", strokeWidth: 2 })
        );

    myDiagram.linkTemplateMap.add("Marriage",  // for marriage relationships
        $(go.Link,
            // AvoidsNodes routing might be better when people have multiple marriages
            {
                routing: go.Link.AvoidsNodes, corner: 10,
                fromSpot: go.Spot.LeftRightSides, toSpot: go.Spot.LeftRightSides,
                selectable: false, isTreeLink: false, layerName: "Background"
            },
            $(go.Shape, { strokeWidth: 2.5, stroke: "#5d8cc1" /* blue */ })
        ));

    // n: name, s: sex, m: mother, f: father, ux: wife, vir: husband, a: attributes/markers

    setupDiagram(myDiagram, [
        { key: 0, n: "Aaron", s: "M", m: -10, f: -11, ux: 1, a: ["C", "F", "K"] },
        { key: 1, n: "Alice", s: "F", m: -12, f: -13, a: ["B", "H", "K"] },
        { key: 2, n: "Bob", s: "M", m: 1, f: 0, ux: 3, a: ["C", "H", "L"] },
        { key: 3, n: "Barbara", s: "F", a: ["C"] },
        { key: 4, n: "Bill", s: "M", m: 1, f: 0, ux: 5, a: ["E", "H"] },
        { key: 5, n: "Brooke", s: "F", a: ["B", "H", "L"] },
        { key: 6, n: "Claire", s: "F", m: 1, f: 0, a: ["C"] },
        { key: 7, n: "Carol", s: "F", m: 1, f: 0, a: ["C", "I"] },
        { key: 8, n: "Chloe", s: "F", m: 1, f: 0, vir: 9, a: ["E"] },
        { key: 9, n: "Chris", s: "M", a: ["B", "H"] },
        { key: 10, n: "Ellie", s: "F", m: 3, f: 2, a: ["E", "G"] },
        { key: 11, n: "Dan", s: "M", m: 3, f: 2, a: ["B", "J"] },
        { key: 12, n: "Elizabeth", s: "F", vir: 13, a: ["J"] },
        { key: 13, n: "David", s: "M", m: 5, f: 4, a: ["B", "H"] },
        { key: 14, n: "Emma", s: "F", m: 5, f: 4, a: ["E", "G"] },
        { key: 15, n: "Evan", s: "M", m: 8, f: 9, a: ["F", "H"] },
        { key: 16, n: "Ethan", s: "M", m: 8, f: 9, a: ["D", "K"] },
        { key: 17, n: "Eve", s: "F", vir: 16, a: ["B", "F", "L"] },
        { key: 18, n: "Emily", s: "F", m: 8, f: 9 },
        { key: 19, n: "Fred", s: "M", m: 17, f: 16, a: ["B"] },
        { key: 20, n: "Faith", s: "F", m: 17, f: 16, a: ["L"] },
        { key: 21, n: "Felicia", s: "F", m: 12, f: 13, a: ["H"] },
        { key: 22, n: "Frank", s: "M", m: 12, f: 13, a: ["B", "H"] },

        // "Aaron"'s ancestors
        { key: -10, n: "Paternal Grandfather", s: "M", m: -33, f: -32, ux: -11, a: ["A", "S"] },
        { key: -11, n: "Paternal Grandmother", s: "F", a: ["E", "S"] },
        { key: -32, n: "Paternal Great", s: "M", ux: -33, a: ["F", "H", "S"] },
        { key: -33, n: "Paternal Great", s: "F", a: ["S"] },
        { key: -40, n: "Great Uncle", s: "M", m: -33, f: -32, a: ["F", "H", "S"] },
        { key: -41, n: "Great Aunt", s: "F", m: -33, f: -32, a: ["B", "I", "S"] },
        { key: -20, n: "Uncle", s: "M", m: -11, f: -10, a: ["A", "S"] },

        // "Alice"'s ancestors
        { key: -12, n: "Maternal Grandfather", s: "M", ux: -13, a: ["D", "L", "S"] },
        { key: -13, n: "Maternal Grandmother", s: "F", m: -31, f: -30, a: ["H", "S"] },
        { key: -21, n: "Aunt", s: "F", m: -13, f: -12, a: ["C", "I"] },
        { key: -22, n: "Uncle", s: "M", ux: -21 },
        { key: -23, n: "Cousin", s: "M", m: -21, f: -22 },
        { key: -30, n: "Maternal Great", s: "M", ux: -31, a: ["D", "J", "S"] },
        { key: -31, n: "Maternal Great", s: "F", m: -50, f: -51, a: ["B", "H", "L", "S"] },
        { key: -42, n: "Great Uncle", s: "M", m: -30, f: -31, a: ["C", "J", "S"] },
        { key: -43, n: "Great Aunt", s: "F", m: -30, f: -31, a: ["E", "G", "S"] },
        { key: -50, n: "Maternal Great Great", s: "F", vir: -51, a: ["D", "I", "S"] },
        { key: -51, n: "Maternal Great Great", s: "M", a: ["B", "H", "S"] }
    ],
        4 /* focus on this person */);
}


// create and initialize the Diagram.model given an array of node data representing people
function setupDiagram(diagram, array, focusId) {
    diagram.model =
        new go.GraphLinksModel(
            { // declare support for link label nodes
                linkLabelKeysProperty: "labelKeys",
                // this property determines which template is used
                nodeCategoryProperty: "s",
                // if a node data object is copied, copy its data.a Array
                copiesArrays: true,
                // create all of the nodes for people
                nodeDataArray: array
            });
    setupMarriages(diagram);
    setupParents(diagram);

    const node = diagram.findNodeForKey(focusId);
    if (node !== null) node.isSelected = true;
}

function findMarriage(diagram, a, b) {  // A and B are node keys
    const nodeA = diagram.findNodeForKey(a);
    const nodeB = diagram.findNodeForKey(b);
    if (nodeA !== null && nodeB !== null) {
        const it = nodeA.findLinksBetween(nodeB);  // in either direction
        while (it.next()) {
            const link = it.value;
            // Link.data.category === "Marriage" means it's a marriage relationship
            if (link.data !== null && link.data.category === "Marriage") return link;
        }
    }
    return null;
}

// now process the node data to determine marriages
function setupMarriages(diagram) {
    const model = diagram.model;
    const nodeDataArray = model.nodeDataArray;
    for (let i = 0; i < nodeDataArray.length; i++) {
        const data = nodeDataArray[i];
        const key = data.key;
        let uxs = data.ux;
        if (uxs !== undefined) {
            if (typeof uxs === "number") uxs = [uxs];
            for (let j = 0; j < uxs.length; j++) {
                const wife = uxs[j];
                const wdata = model.findNodeDataForKey(wife);
                if (key === wife || !wdata || wdata.s !== "F") {
                    console.log("cannot create Marriage relationship with self or unknown person " + wife);
                    continue;
                }
                const link = findMarriage(diagram, key, wife);
                if (link === null) {
                    // add a label node for the marriage link
                    const mlab = { s: "LinkLabel" };
                    model.addNodeData(mlab);
                    // add the marriage link itself, also referring to the label node
                    const mdata = { from: key, to: wife, labelKeys: [mlab.key], category: "Marriage" };
                    model.addLinkData(mdata);
                }
            }
        }
        let virs = data.vir;
        if (virs !== undefined) {
            if (typeof virs === "number") virs = [virs];
            for (let j = 0; j < virs.length; j++) {
                const husband = virs[j];
                const hdata = model.findNodeDataForKey(husband);
                if (key === husband || !hdata || hdata.s !== "M") {
                    console.log("cannot create Marriage relationship with self or unknown person " + husband);
                    continue;
                }
                const link = findMarriage(diagram, key, husband);
                if (link === null) {
                    // add a label node for the marriage link
                    const mlab = { s: "LinkLabel" };
                    model.addNodeData(mlab);
                    // add the marriage link itself, also referring to the label node
                    const mdata = { from: key, to: husband, labelKeys: [mlab.key], category: "Marriage" };
                    model.addLinkData(mdata);
                }
            }
        }
    }
}

// process parent-child relationships once all marriages are known
function setupParents(diagram) {
    const model = diagram.model;
    const nodeDataArray = model.nodeDataArray;
    for (let i = 0; i < nodeDataArray.length; i++) {
        const data = nodeDataArray[i];
        const key = data.key;
        const mother = data.m;
        const father = data.f;
        if (mother !== undefined && father !== undefined) {
            const link = findMarriage(diagram, mother, father);
            if (link === null) {
                // or warn no known mother or no known father or no known marriage between them
                console.log("unknown marriage: " + mother + " & " + father);
                continue;
            }
            const mdata = link.data;
            if (mdata.labelKeys === undefined || mdata.labelKeys[0] === undefined) continue;
            const mlabkey = mdata.labelKeys[0];
            const cdata = { from: mlabkey, to: key };
            myDiagram.model.addLinkData(cdata);
        }
    }
}

window.addEventListener('DOMContentLoaded', init);
</script>