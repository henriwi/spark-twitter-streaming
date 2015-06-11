var bootcamp = bootcamp || {};

$(function () {
    initD3();

    var protocol = location.protocol === "https:" ? "wss:" : "ws:";
    var ws = new WebSocket(protocol + "//" + location.host + "/ws");

    ws.onmessage = function (msg) {
        var data = JSON.parse(msg.data);
        render(data);
    }

    console.log("ready!");
});

function initD3() {
    bootcamp.diameter = 960,
        format = d3.format(",d"),
        color = d3.scale.category20c();

    bootcamp.svg = d3.select("body").append("svg")
        .attr("width", bootcamp.diameter)
        .attr("height", bootcamp.diameter)
        .attr("class", "bubble");

    bootcamp.bubble = d3.layout.pack()
        .sort(null)
        .size([bootcamp.diameter, bootcamp.diameter])
        .padding(1.5);
}

function render(data) {
    var duration = 200;
    var delay = 0;

    var node = bootcamp.svg.selectAll(".node")
        .data(bootcamp.bubble.nodes(classes(data))
            .filter(function (d) {
                return !d.children;
            }));

    var enterNode = node
        .enter()
        .append("g")
        .attr("class", "node")
        .attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });

    enterNode.append("circle")
        .attr("class", "circle")
        .attr("r", function (d) {
            return d.r;
        })
        .style("fill", function (d) {
            return color(d.className);
        });

    enterNode.append("text")
        .attr("dy", ".3em")
        .text(function (d) {
            return d.className
        })
        .style("text-anchor", "middle");

    // Transitions
    node.transition()
        .duration(duration)
        .delay(function (d, i) {
            delay = i * 7;
            return delay;
        })
        .attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });

    node.select("circle")
        .transition()
        .attr('r', function (d) {
            return d.r;
        });

    // Returns a flattened hierarchy containing all leaf nodes under the root.
    function classes(root) {
        var classes = [];

        function recurse(name, node) {
            if (node.children) node.children.forEach(function (child) {
                recurse(node.name, child);
            });
            else classes.push({packageName: name, className: node.hashTag, value: node.count});
        }

        recurse(null, root);
        return {children: classes};
    }

    d3.select(self.frameElement).style("height", bootcamp.diameter + "px");
}

