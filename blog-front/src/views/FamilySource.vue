<template>
  <div style="height: 100%;">
    <div style="height: 7%;">
      <el-row style="margin-top: 10px;margin-bottom: 10px;">
        <el-col :span="8">
          <el-input placeholder="按用户名搜索" v-model="serachName" size="small"></el-input>
        </el-col>
        <el-col :span="1">
          <el-button type="success" size="small" @click="serachByNameFun">
            <i class="el-icon-search"></i>
            搜索
          </el-button>
        </el-col>
        <el-col :span="1" :offset="1">
          <el-button type="warning" size="small" @click="zoomToFitFun">总览</el-button>
        </el-col>
      </el-row>
    </div>
    <!--族谱面板-->
    <div ref="myDiagramDiv" style="background-color: rgb(248, 248, 248); border: 0px solid #9b9b9b; -webkit-tap-highlight-color: rgba(255, 255, 255, 0); cursor: auto;height: 90%">fdsfa</div>
  </div>
</template>
<script setup lang="ts">
class GenogramLayout extends go.LayeredDigraphLayout {
  constructor() {
    super();
    this.alignOption = go.LayeredDigraphLayout.AlignAll;
    this.initializeOption = go.LayeredDigraphLayout.InitDepthFirstIn;
    this.spouseSpacing = 30; // 配偶之间的最小空间
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
    var multiSpousePeople = new go.Set();
    // 考虑给定集合中的所有节点
    var it = coll.iterator;
    while (it.next()) {
      var node = it.value;
      if (!(node instanceof go.Node)) continue;
      if (!node.isLayoutPositioned || !node.isVisible()) continue;
      if (nonmemberonly && node.containingGroup !== null) continue;
      // 如果是未婚节点，或者是链接标签节点，请为其创建LayoutVertex
      if (node.isLinkLabel) {
        // 获得婚姻链接
        var link = node.labeledLink;
        var spouseA = link.fromNode;
        var spouseB = link.toNode;
        // 创建表示丈夫和妻子的顶点
        var vertex = net.addNode(node);
        // 现在定义顶点大小，使其足够大以容纳两个配偶
        vertex.width =
          spouseA.actualBounds.width +
          this.spouseSpacing +
          spouseB.actualBounds.width;
        vertex.height = Math.max(
          spouseA.actualBounds.height,
          spouseB.actualBounds.height
        );
        vertex.focus = new go.Point(
          spouseA.actualBounds.width + this.spouseSpacing / 2,
          vertex.height / 2
        );
      } else {
        var marriages = 0;
        node.linksConnected.each(function(l) {
          if (l.isLabeledLink) marriages++;
        });
        if (marriages === 0) {
          var vertex = net.addNode(node);
        } else if (marriages > 1) {
          multiSpousePeople.add(node);
        }
      }
    }
    // 现在做所有链接
    it.reset();
    while (it.next()) {
      var link = it.value;
      if (!(link instanceof go.Link)) continue;
      if (!link.isLayoutPositioned || !link.isVisible()) continue;
      if (nonmemberonly && link.containingGroup !== null) continue;
      // 现在执行所有链接如果是父子链接，请为其添加布局
      if (!link.isLabeledLink) {
        var parent = net.findVertex(link.fromNode); // 应该是标签节点
        var child = net.findVertex(link.toNode);
        if (child !== null) {
          // 未婚的孩子
          net.linkVertexes(parent, child, link);
        } else {
          // 已婚的孩子
          link.toNode.linksConnected.each(function(l) {
            if (!l.isLabeledLink) return;
            // 找到婚姻链接，现在获取其标签节点
            var mlab = l.labelNodes.first();
            var mlabvert = net.findVertex(mlab);
            if (mlabvert !== null) {
              net.linkVertexes(parent, mlabvert, link);
            }
          });
        }
      }
    }
    while (multiSpousePeople.count > 0) {
      // 查找所有间接结婚的人的集合
      var node = multiSpousePeople.first();
      var cohort = new go.Set();
      this.extendCohort(cohort, node);
      // 然后，通过使用一个公共顶点将它们连接起来
      var dummyvert = net.createVertex();
      net.addVertex(dummyvert);
      var marriages = new go.Set();
      cohort.each(function(n) {
        n.linksConnected.each(function(l) {
          marriages.add(l);
        });
      });
      marriages.each(function(link) {
        // 查找婚姻链接的顶点（即标签节点的顶点）
        var mlab = link.labelNodes.first();
        var v = net.findVertex(mlab);
        if (v !== null) {
          net.linkVertexes(dummyvert, v, null);
        }
      });
      // 完成这些人，现在看看是否还有其他多人结婚
      multiSpousePeople.removeAll(cohort);
    }
  }

  // 收集所有与某人间接结婚的人
  extendCohort(coll, node) {
    if (coll.has(node)) return;
    coll.add(node);
    var lay = this;
    node.linksConnected.each(function(l) {
      if (l.isLabeledLink) {
        // 如果是婚姻关系，继续与配偶双方保持联系
        lay.extendCohort(coll, l.fromNode);
        lay.extendCohort(coll, l.toNode);
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
      const sz = horiz ? v.width : v.height;
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
    });
  }

  commitNodes() {
    super.commitNodes();
    go.LayeredDigraphLayout.prototype.commitNodes.call(this);
    //定位规则节点
    this.network.vertexes.each(function(v) {
      if (v.node !== null && !v.node.isLinkLabel) {
        v.node.position = new go.Point(v.x, v.y);
      }
    });
    // 定位每个婚姻顶点的配偶
    var layout = this;
    this.network.vertexes.each(function(v) {
      if (v.node === null) return;
      if (!v.node.isLinkLabel) return;
      var labnode = v.node;
      var lablink = labnode.labeledLink;
      lablink.invalidateRoute();
      var spouseA = lablink.fromNode;
      var spouseB = lablink.toNode;
      // 父亲在左边，母亲在右边
      if (spouseA.data.sex === "女") {
        var temp = spouseA;
        spouseA = spouseB;
        spouseB = temp;
      }
      // 查看父对象是否位于所需的一侧，以避免链接交叉
      var aParentsNode = layout.findParentsMarriageLabelNode(spouseA);
      var bParentsNode = layout.findParentsMarriageLabelNode(spouseB);
      if (
        aParentsNode !== null &&
        bParentsNode !== null &&
        aParentsNode.position.x > bParentsNode.position.x
      ) {
        // 交换配偶
        var temp = spouseA;
        spouseA = spouseB;
        spouseB = temp;
      }
      spouseA.position = new go.Point(v.x, v.y);
      spouseB.position = new go.Point(
        v.x + spouseA.actualBounds.width + layout.spouseSpacing,
        v.y
      );
      if (spouseA.opacity === 0) {
        var pos = new go.Point(v.centerX - spouseA.actualBounds.width / 2, v.y);
        spouseA.position = pos;
        spouseB.position = pos;
      } else if (spouseB.opacity === 0) {
        var pos = new go.Point(v.centerX - spouseB.actualBounds.width / 2, v.y);
        spouseA.position = pos;
        spouseB.position = pos;
      }
    });
    // 仅将子节点放置在婚姻标签节点下
    this.network.vertexes.each(function(v) {
      if (v.node === null || v.node.linksConnected.count > 1) return;
      var mnode = layout.findParentsMarriageLabelNode(v.node);
      if (mnode !== null && mnode.linksConnected.count === 1) {
        // 如果只有一个孩子
        var mvert = layout.network.findVertex(mnode);
        var newbnds = v.node.actualBounds.copy();
        newbnds.x = mvert.centerX - v.node.actualBounds.width / 2;
        // 查看该层的水平中点是否有任何空白
        var overlaps = layout.diagram.findObjectsIn(
          newbnds,
          function(x) {
            return x.part;
          },
          function(p) {
            return p !== v.node;
          },
          true
        );
        if (overlaps.count === 0) {
          v.node.move(newbnds.position);
        }
      }
    });
  }
  //迭代下一代
  findParentsMarriageLabelNode(node) {
    const it = node.findNodesInto();
    while (it.next()) {
      const n = it.value;
      if (n.isLinkLabel) return n;
    }
    return null;
  }
} // end GenogramLayout class
import {
  ref,
  onMounted,
  onUnmounted,
  defineComponent,
  reactive,
  nextTick,
  watch,
  computed
} from "vue";
import { _getSourceAll } from "@/api/api";
import go from "gojs"; //在export default前
// import { go } from "@/utils/go.js";

function textStyle() {
  return { font: "9pt  Segoe UI,sans-serif", stroke: "white" };
}
const jsonarray = []; //数据
const ManImg = "/static/defaultImg.png";
const WmanImg = "/static/defaultW.png";
let myDiagram = null;
let serachName = "";
// const go = gojs;
// const $ = go.GraphObject.make;
const myDiagramDiv = ref(null);
function init() {
  const gojs = go.GraphObject.make; // 为了定义模板的简洁性
  myDiagram = gojs(go.Diagram, myDiagramDiv.value, {
    initialAutoScale: go.Diagram.Uniform,
    "undoManager.isEnabled": false,
    layout: gojs(GenogramLayout, { direction: 90, layerSpacing: 35 }),
    allowZoom: true
  });
  // 两个不同的节点模板，每个性别一个
  myDiagram.nodeTemplateMap.add(
    "活女",
    gojs(
      go.Node,
      "Auto",
      // 对于排序，将Node.text作为data.name
      new go.Binding("text", "name"),
      // 定义节点的背景
      gojs(go.Shape, "Rectangle", {
        name: "SHAPE",
        fill: "#ec4ec3",
        stroke: "white",
        strokeWidth: 3.5,
        portId: "",
        fromLinkable: true,
        toLinkable: true,
        cursor: "pointer"
      }),
      gojs(
        go.Panel,
        "Horizontal",
        gojs(
          go.Picture,
          {
            name: "Picture",
            desiredSize: new go.Size(75, 75),
            margin: 1
          },
          new go.Binding("source", "img").makeTwoWay()
        ),
        gojs(
          go.Panel,
          "Table",
          {
            minSize: new go.Size(140, NaN),
            maxSize: new go.Size(200, NaN),
            margin: new go.Margin(6, 10, 0, 6),
            defaultAlignment: go.Spot.Left
          },
          gojs(go.TextBlock, "姓名:", textStyle(), { row: 0, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 0,
              column: 1,
              columnSpan: 5,
              font: "12pt Segoe UI,sans-serif",
              minSize: new go.Size(10, 16)
            },
            new go.Binding("text", "name").makeTwoWay()
          ),
          gojs(go.TextBlock, "生辰:", textStyle(), { row: 1, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 1,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "birthDay").makeTwoWay()
          ),
          gojs(go.TextBlock, "电话：", textStyle(), { row: 2, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 2,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "phone").makeTwoWay()
          ),
          gojs(go.TextBlock, "现住址：", textStyle(), {
            row: 3,
            column: 0
          }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 3,
              column: 1,
              columnSpan: 5,
              font: "italic 9pt sans-serif",
              wrap: go.TextBlock.WrapFit,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "addressNew").makeTwoWay()
          )
        )
      )
    )
  );

  myDiagram.nodeTemplateMap.add(
    "亡女",
    gojs(
      go.Node,
      "Auto",
      // 对于排序，将Node.text作为data.name
      new go.Binding("text", "name"),
      // 定义节点的背景
      gojs(go.Shape, "Rectangle", {
        name: "SHAPE",
        fill: "#471f1f",
        stroke: "white",
        strokeWidth: 3.5,
        portId: "",
        fromLinkable: true,
        toLinkable: true,
        cursor: "pointer"
      }),
      gojs(
        go.Panel,
        "Horizontal",
        gojs(
          go.Picture,
          {
            name: "Picture",
            desiredSize: new go.Size(75, 75),
            margin: 1
          },
          new go.Binding("source", "img").makeTwoWay()
        ),
        gojs(
          go.Panel,
          "Table",
          {
            minSize: new go.Size(140, NaN),
            maxSize: new go.Size(200, NaN),
            margin: new go.Margin(6, 10, 0, 6),
            defaultAlignment: go.Spot.Left
          },
          gojs(go.TextBlock, "姓名:", textStyle(), { row: 0, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 0,
              column: 1,
              columnSpan: 5,
              font: "12pt Segoe UI,sans-serif",
              minSize: new go.Size(10, 16)
            },
            new go.Binding("text", "name").makeTwoWay()
          ),
          gojs(go.TextBlock, "生辰:", textStyle(), { row: 1, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 1,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "birthDay").makeTwoWay()
          ),
          gojs(go.TextBlock, "电话：", textStyle(), { row: 2, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 2,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "phone").makeTwoWay()
          ),
          gojs(go.TextBlock, "现住址：", textStyle(), {
            row: 3,
            column: 0
          }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 3,
              column: 1,
              columnSpan: 5,
              font: "italic 9pt sans-serif",
              wrap: go.TextBlock.WrapFit,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "addressNew").makeTwoWay()
          )
        )
      )
    )
  );

  myDiagram.nodeTemplateMap.add(
    "亡男",
    gojs(
      go.Node,
      "Auto",
      // 对于排序，将Node.text作为data.name
      new go.Binding("text", "name"),
      // 绑定Part.layerName以根据是否选择控制节点的层
      // new go.Binding("layerName", "isSelected", function (sel) { return sel ? "Foreground" : ""; }).ofObject(),
      // 定义节点的背景
      gojs(go.Shape, "Rectangle", {
        name: "SHAPE",
        fill: "#25294b",
        stroke: "white",
        strokeWidth: 3.5,
        portId: "",
        fromLinkable: true,
        toLinkable: true,
        cursor: "pointer"
      }),
      gojs(
        go.Panel,
        "Horizontal",
        gojs(
          go.Picture,
          {
            name: "Picture",
            desiredSize: new go.Size(75, 75),
            margin: 1
          },
          new go.Binding("source", "img").makeTwoWay()
        ),
        gojs(
          go.Panel,
          "Table",
          {
            minSize: new go.Size(140, NaN),
            maxSize: new go.Size(200, NaN),
            margin: new go.Margin(6, 10, 0, 6),
            defaultAlignment: go.Spot.Left
          },
          gojs(go.TextBlock, "姓名:", textStyle(), { row: 0, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 0,
              column: 1,
              columnSpan: 4,
              font: "12pt Segoe UI,sans-serif",
              minSize: new go.Size(10, 16)
            },
            new go.Binding("text", "name").makeTwoWay()
          ),
          gojs(go.TextBlock, "生辰:", textStyle(), { row: 1, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 1,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "birthDay").makeTwoWay()
          ),
          gojs(go.TextBlock, "电话：", textStyle(), { row: 2, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 2,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "phone").makeTwoWay()
          ),
          gojs(go.TextBlock, "现住址：", textStyle(), {
            row: 3,
            column: 0
          }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 3,
              column: 1,
              columnSpan: 4,
              font: "italic 9pt sans-serif",
              wrap: go.TextBlock.WrapFit,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "addressNew").makeTwoWay()
          )
        )
      )
    )
  );

  myDiagram.nodeTemplateMap.add(
    "活男",
    gojs(
      go.Node,
      "Auto",
      // 对于排序，将Node.text作为data.name
      new go.Binding("text", "name"),
      // 绑定Part.layerName以根据是否选择控制节点的层
      // new go.Binding("layerName", "isSelected", function (sel) { return sel ? "Foreground" : ""; }).ofObject(),
      // 定义节点的背景
      gojs(go.Shape, "Rectangle", {
        name: "SHAPE",
        fill: "#6a76cb",
        stroke: "white",
        strokeWidth: 3.5,
        portId: "",
        fromLinkable: true,
        toLinkable: true,
        cursor: "pointer"
      }),
      gojs(
        go.Panel,
        "Horizontal",
        gojs(
          go.Picture,
          {
            name: "Picture",
            desiredSize: new go.Size(75, 75),
            margin: 1
          },
          new go.Binding("source", "img").makeTwoWay()
        ),
        gojs(
          go.Panel,
          "Table",
          {
            minSize: new go.Size(140, NaN),
            maxSize: new go.Size(200, NaN),
            margin: new go.Margin(6, 10, 0, 6),
            defaultAlignment: go.Spot.Left
          },
          gojs(go.TextBlock, "姓名:", textStyle(), { row: 0, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 0,
              column: 1,
              columnSpan: 4,
              font: "12pt Segoe UI,sans-serif",
              minSize: new go.Size(10, 16)
            },
            new go.Binding("text", "name").makeTwoWay()
          ),
          gojs(go.TextBlock, "生辰:", textStyle(), { row: 1, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 1,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "birthDay").makeTwoWay()
          ),
          gojs(go.TextBlock, "电话：", textStyle(), { row: 2, column: 0 }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 2,
              column: 1,
              columnSpan: 4,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "phone").makeTwoWay()
          ),
          gojs(go.TextBlock, "现住址：", textStyle(), {
            row: 3,
            column: 0
          }),
          gojs(
            go.TextBlock,
            textStyle(),
            {
              row: 3,
              column: 1,
              columnSpan: 4,
              font: "italic 9pt sans-serif",
              wrap: go.TextBlock.WrapFit,
              minSize: new go.Size(10, 14)
            },
            new go.Binding("text", "addressNew").makeTwoWay()
          )
        )
      )
    )
  );
  // 每个标签节点的表示形式——婚姻链接上不显示任何内容
  myDiagram.nodeTemplateMap.add(
    "LinkLabel",
    gojs(go.Node, {
      selectable: false,
      width: 1,
      height: 1,
      fromEndSegmentLength: 70
    })
  );
  myDiagram.linkTemplate = gojs(
    // 亲子关系
    go.Link,
    {
      routing: go.Link.Orthogonal,
      corner: 5,
      layerName: "Background",
      selectable: false,
      fromSpot: go.Spot.Bottom,
      toSpot: go.Spot.Top
    },
    gojs(go.Shape, { stroke: "#e30007", strokeWidth: 2 })
  );
  myDiagram.linkTemplateMap.add(
    "Marriage", // 婚姻关系
    gojs(
      go.Link,
      { selectable: false },
      gojs(go.Shape, { strokeWidth: 2, stroke: "#0071c1" })
    )
  );
  _getSourceAll().then(res => {
    if (res.status === 200) {
      let arrays = res.data;
      for (var i = 0; i < arrays.length; i++) {
        let img = arrays[i].userImgUrl;
        if (
          (img == null || img == undefined || img == "") &&
          arrays[i].userSex == "男"
        ) {
          img = ManImg;
        } else if (
          (img == null || img == undefined || img == "") &&
          arrays[i].userSex == "女"
        ) {
          img = WmanImg;
        }
        let arr = {
          key: arrays[i].userId,
          pId: arrays[i].userPId,
          mId: arrays[i].userMID,
          name: arrays[i].userName,
          pinyin: arrays[i].userPinYin,
          birthDay: arrays[i].userBirthDay,
          isDie: arrays[i].isSurvival,
          dieDay: arrays[i].userDieDay,
          sex: arrays[i].userSex,
          phone: arrays[i].userPhone,
          addressNew: arrays[i].userAddressNew,
          addressOld: arrays[i].userAddressOld,
          emil: arrays[i].userEmil,
          img: img,
          mateId: arrays[i].userMateId,
          note: arrays[i].userNote,
          love: arrays[i].userLove,
          pName: arrays[i].pName,
          mName: arrays[i].mName,
          mateName: arrays[i].mateName,
          styleNo: arrays[i].styleNo,
          mate: arrays[i].list
        };
        jsonarray.push(arr);
      }
    }
    setupDiagram(myDiagram, jsonarray, 1);
  });
}

function setupDiagram(diagram, array, focusId) {
  diagram.model = go.GraphObject.make(go.GraphLinksModel, {
    // 声明对链接标签节点的支持
    linkLabelKeysProperty: "labelKeys",
    // 此属性确定使用哪个模板
    nodeCategoryProperty: "styleNo",
    // 如果复制了节点数据对象，请复制其数据
    copiesArrays: true,
    // 为人员创建所有节点
    nodeDataArray: array
  });
  setupMarriages(diagram);
  setupParents(diagram);
  var node = diagram.findNodeForKey(focusId);
  if (node !== null) {
    diagram.select(node);
  }
}

function setupMarriages(diagram) {
  var model = diagram.model;
  var nodeDataArray = model.nodeDataArray;
  for (var i = 0; i < nodeDataArray.length; i++) {
    var data = nodeDataArray[i];
    var key = data.key;
    var uxs = data.mateId;
    if (uxs !== undefined && uxs !== "" && uxs != null) {
      uxs = uxs.toString();
      uxs = uxs.split(",");
      for (var j = 0; j < uxs.length; j++) {
        var wife = uxs[j];
        if (key === wife || wife === "" || wife == null) {
          continue;
        }
        var link = findMarriage(diagram, key, wife);
        if (link === null) {
          // 为婚姻链接添加标签节点
          var mlab = { styleNo: "LinkLabel" };
          model.addNodeData(mlab);
          // 添加婚姻链接本身，也指标签节点
          var mdata = {
            from: key,
            to: wife,
            labelKeys: [mlab.key],
            category: "Marriage"
          };
          model.addLinkData(mdata);
        }
      }
    }
  }
}

function setupParents(diagram) {
  var model = diagram.model;
  var nodeDataArray = model.nodeDataArray;
  for (var i = 0; i < nodeDataArray.length; i++) {
    var data = nodeDataArray[i];
    var key = data.key;
    var mother = data.mId;
    var father = data.pId;
    if (mother !== undefined && father !== undefined) {
      var link = findMarriage(diagram, mother, father);
      if (link === null) {
        continue;
      }
      var mdata = link.data;
      var mlabkey = mdata.labelKeys[0];
      var cdata = { from: mlabkey, to: key };
      myDiagram.model.addLinkData(cdata);
    }
  }
}

function findMarriage(diagram, a, b) {
  // A和B是节点密钥
  var nodeA = diagram.findNodeForKey(a);
  var nodeB = diagram.findNodeForKey(b);
  if (nodeA !== null && nodeB !== null) {
    var it = nodeA.findLinksBetween(nodeB);
    while (it.next()) {
      var link = it.value;
      // 链接数据类别==“Marriage”意味着它是一种婚姻关系
      if (link.data !== null && link.data.category === "Marriage") return link;
    }
  }
  return null;
}

function zoomToFitFun() {
  myDiagram.commandHandler.zoomToFit();
}

function serachByNameFun() {
  let res = myDiagram.findNodesByExample({ name: this.serachName });
  if (res.count < 1) {
    this.$notify.warning("未查到该人员");
    return;
  }
  res.each(function(node) {
    myDiagram.select(node);
    myDiagram.scale = 1;
    myDiagram.commandHandler.scrollToPart(myDiagram.findNodeForKey(node.key));
  });
}
onMounted(() => {
  init();
});
</script>
<style lang="scss" scoped>
</style>