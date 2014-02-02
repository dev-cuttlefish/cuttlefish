
    CUTTLEFISH DOCUMENTATION
    
    This document contains the revision of Cuttlefish Documentation, as it 
    exists on the MediaWiki of the project. Due to inability of access to the 
    MediaWiki, this file intends to provide guidelines, information and new 
    data for the content of MediaWiki.
    
    MediaWiki
    https://sourceforge.net/apps/mediawiki/cuttlefish/index.php?title=Main_Page


    Ilias Rinis (iliasr -at- gmail.com)
    30 Sept. 2013



    I. Cuttlefish third-party technologies/libraries

While Cuttlefish 2.0 heavily built on the JUNG library, the latest version of
Cuttlefish now replaces JUNG with two different libraries:

1. Gephi Toolkit v0.8.5 handles the Graph Library (Data Structures, Access and 
Control, Layout algorithms, Input/Output controllers)

2. JOGL v2.0.2 (The Java bindings for OpenGL) is used for the visualization of
the graphs. A custom visualization engine
(module ch.ethz.sg.cuttlefish.gui.visualization) was built on top of JOGL. 
This engine handles element rendering (vertices, edges, labels), as well as 
interaction with the system (layouts, controls) and the user (mouse, zooming,
panning, etc.).



    II. Development notes on the newly introduced technologies

The previous versions of Cuttlefish were based on JUNG 2.0, for the needs
of visualisation of the networks, but also for the necessary data structures,
layout algorithms, and access and control interface.

We chose to replace JUNG for two reasons:
    1. The library has been discontinued and outdated since January 2010
    2. The performance of visualisation of larger graphs was very low, and
       even impossible sometimes

However, no suitable library was found, that would offer both visualization
and graph data structures and control, at the same time. We have chosen two
distinct libraries, one for each purpose.

a. The Gephi Toolkit

The Gephi Toolkit has been released by Gephi, in order to provide core
functionality of the platform for usage in other development projects. The
Toolkit is a collection of the essential modules of Gephi, like Graphs and
Layouts (which are, in fact, the ones that we leverage and employ in
Cuttlefish).

The functionality that Cuttlefish uses from Gephi, is integrated into
Cuttlefish by wrapping Gephi's modules in the respective modules of
Cuttlefish. For example, Gephi's Nodes and Edges are wrapped in
Vertices and Edges in Cuttlefish, in order to provide a seamless and
compatible interface to the rest of the modules of Cuttlefish. In
addition, extra modules, like the LayoutLoader and the NetworkExporter
have been introduced in Cuttlefish, in order to provide a well-defined
interface and wrapping of the respective Gephi modules.

For development documentation, instructions, tutorials and examples see
* https://wiki.gephi.org/index.php/Toolkit_portal
* http://www.slideshare.net/gephi/gephi-toolkit-tutorialtoolkit



    III. Cuttlefish File Formats

a. The CEF Format

A new control parameter has been added to Cuttlefish, that allows the user to
specify the execution of a layout during the evolution of the network. This
parameter can be integrated in an existing step of the evolution, or be declared
in a step on its own. The layout specified will be launched during the step
where it has been declared. The layout parameter is defined as following:

layout{<layout-name>} [parameter{value}]

The layout parameter takes the layout name as an argument (example follows).
Certain layouts receive an additional set of (optional) parameters, which can
either used to enable, or to specify desired values of the properties of a layout.

Consider the following example:

[layout{arf} attraction{0.5} keep_positions{true}]

[...]

[layout{weighted-kcore} alpha{1.5} beta{2}]

In this example, the user can specify the network evolution to start with an ARF
layout, given the parameters attraction=0.5 and keep_positions=true (explanation
of new parameters is given below). At a later step of the evolution, the user
can launch the computation of the Weighted KCore layout, with parameters
alpha=1.5 and beta=2.

All layouts supported by Cuttlefish may be used in a layout{} statement; however,
not all layouts have parameters accessible through such a statement (since many
are provided by the Gephi Toolkit, and this functionality does not exist there).

List of supported layouts that can be used in a CEF:

Name                    Abbreviation used in a CEF
ARF                     arf
Weighted ARF            weighted-arf
KCore                   kcore
Weighted KCore          weighted-kcore
Fixed                   fixed
Circle                  circle
Fruchterman-Reingold    fruchterman-reingold
Yifan Hu                yifanhu
ForceAtlas 2            force-atlas
Random                  random


Not all layouts support the configuration of their parameters through a CEF file.
The following list describes the layouts that support this, and the parameters
that can be configured:

arf, weighted-arf
    keep_positions{boolean}     Does not randomize positions at the
                                    layout initialization.
    alpha{float}                Controls attraction between connected
                                    nodes.
    attraction{float}           Scales the attractive term (affects
                                    both connected & unconnected nodes.
    beta{float}                 Scales the repulsive force
    delta{float}                Controls calculation precision
                                    (smaller delta, higher precision)
    force_cutoff{float}         The maximum force for a node.

*   keep_centered{boolean}      Keeps the layout centered while it is
                                    being computed
*   sensitivity{int(>0)}        Controls how much slower the new layout will 
                                    be computed, by scaling the updates to 
                                    the vertices' coordinates. A value of 1 
                                    will perform unscaled steps. Values greater 
                                    than one will scale the computed 
                                    coordinates down and thus will slow down
                                    convergence. The sensitivity must always
                                    be greater than zero, since the convergence
                                    cannot be sped up in this manner.

weighted-kcore
*   alpha
*   beta



The parameters marked with a star (*) have been only introduced in the
latest version of Cuttlefish. Their purpose is to enable the user to
produce nice animations of evolving networks. Their engineering has
been done in cooperation with Nico.

For instance, one would be able to create an animation of the computation
of the arf layout by choosing very high values for sensitivity, while
keeping the nodes centered, and capture a video of the animation:

layout{arf} sensitivity{1000} keep_centered{true}



    IV. TODOs

a. Spatial indexing for mouse interactions

At the moment, mouse interacts with the visualisation panel by a
linear search to all vertices and edges that could match. For the size
of networks supported so far, this does not pose any noticeable overhead
by the user. However, for increased scalability of mouse interaction,
a more complex spatial indexing technique should be used. E.g. R-Trees,
octrees.

b. Polygon antialiasing

The polygon antialiasing technique used currently in Cuttlefish is a
simple one, based on polygon smoothing. There other techniques, such as
multisampling, can be used for improved visualisation results.

c. Layouts

Due to incompatibility with the new Gephi Toolkit library, some of the
layouts supported previously in Cuttlefish are not ported to the latest
version:

    - (Radial) Tree Layout
    - Kamada Kawai
    - ISO-M
