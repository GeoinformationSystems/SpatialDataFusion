//init map stats object
function olStats(olResource) {
    //get crs
    this.crs = olResource.crs;
    //get bounds
    this.extent = olResource.extent;
}

//init stats
function f_showStats(resource_r, resource_t, div_stats) {
    //clear
    f_clearStats(div_stats);
    //show stats
    f_displayStats(resource_r, resource_t, div_stats);
}

//clear stats
function f_clearStats(div_stats) {
    document.getElementById(div_stats).innerHTML = '&nbsp;';
}

//display stats
function f_displayStats(resource_r, resource_t, div_stats) {
    //check is stats are set
    var stats_r = (resource_r == null ? null : resource_r.stats);
    var stats_t = (resource_t == null ? null : resource_t.stats);
    //init table
    var html = '<div><table class="stats">';
    html += '<tr><th></th><th>Reference Data</th><th>Target Data</th><th style="width:400px;">Comparison</th></tr>';

    //compare stats
    html += '<tr><td class="row_cap">CRS</td><td>' + (stats_r ? stats_r.crs : '') + '</td><td>' + (stats_t ? stats_t.crs : '') + '</td>' + f_stats_compareCRS(stats_r, stats_t) + '</tr>';
    html += '<tr><td class="row_cap">WGS84 Extent</td><td>' + (stats_r ? stats_r.extent : '') + '</td><td>' + (stats_t ? stats_t.extent : '') + '</td>' + f_stats_compareExtent(stats_r, stats_t) + '</tr>';

    //end
    html += '</table></div>';
    //set info html
    document.getElementById(div_stats).innerHTML = html;
}

//compare crs
function f_stats_compareCRS(stats_r, stats_t) {
    if (stats_r == null || stats_t == null)
        return '<td></td>';
    if (stats_r.crs == stats_t.crs)
        return '<td class="good">CRS are equal</td>';
    else
        return '<td class="bad">CRS are not equal</td>';
}

//compare extents
function f_stats_compareExtent(stats_r, stats_t) {
    if (stats_r == null || stats_t == null)
        return '<td></td>';
    rExtent = stats_r.extent;
    tExtent = stats_t.extent;
    if (rExtent[0] > tExtent[2] || rExtent[2] < tExtent[0] || rExtent[1] > tExtent[3] || rExtent[3] < tExtent[1])
        return '<td class="bad">Bounds do not intersect</td>';
    else
        return '<td class="good">Bounds intersect</td>';
}