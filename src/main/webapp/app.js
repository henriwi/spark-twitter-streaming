var bootcamp = bootcamp || {};

$(function () {
    initD3();

    ws = new WebSocket('ws://localhost:8080/ws');
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

    node.transition()
        .duration(duration)
        .delay(function(d, i) {delay = i * 7; return delay;})
        .attr('transform', function(d) { return 'translate(' + d.x + ',' + d.y + ')'; })
        .attr('r', function(d) { return d.r; })
        .style('opacity', 1); // force to 1, so they don't get stuck below 1 at enter()

   var enterSelection = node.enter().append("g");
    enterSelection.append("circle")
        .attr("r", function (d) {
            return d.r;
        })
        .style("fill", function (d) {
            return color(d.className);
        })
        .attr("class", "node")
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        })
        .style('opacity', 0)
        .transition()
        .duration(duration * 1.2)
        .style('opacity', 1);

    node.exit()
        .transition()
        .duration(duration + delay)
        .style('opacity', 0)
        .remove();

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

